package xeterios.powertag.game;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.*;
import xeterios.powertag.Main;
import xeterios.powertag.Messenger;
import xeterios.powertag.configuration.Map;
import xeterios.powertag.game.handlers.EventsHandler;
import xeterios.powertag.game.handlers.PowerupHandler;
import xeterios.powertag.game.timers.StartupTimer;
import xeterios.powertag.game.timers.TimerType;
import xeterios.powertag.players.PlayerData;
import xeterios.powertag.players.comparers.comparators.GamePointComparator;

import java.util.*;
import java.util.logging.Level;

public class GameManager
{
    @Getter private final Main main;
    @Getter private final Map map;

    @Getter private int round;
    @Setter @Getter private boolean countdown;
    @Setter @Getter private boolean started;

    private final HashMap<Player, GameMode> playerGameMode;
    private final HashMap<Player, Scoreboard> playerScoreboard;

    private final ArrayList<GamePlayer> players;
    private final ArrayList<GamePlayer> notEligibleForPoints;

    private boolean playerIsInTopFive;

    @Getter private final Scoreboard scoreboard;
    @Getter private final Objective objective;
    @Getter private final GameTeam runners;
    @Getter private final GameTeam taggers;
    @Getter private final GameTeam spectators;

    @Getter private final PowerupHandler powerupHandler;
    @Getter private final GameTimerHandler timerHandler;
    private final EventsHandler eventsHandler;

    private int waitingActionBar;

    public GameManager(Main main, Map map)
    {
        // Main components
        this.main = main;
        this.map = map;

        // Base
        this.countdown = false;
        this.started = false;
        this.playerScoreboard = new HashMap<>();
        this.playerGameMode = new HashMap<>();
        this.players = new ArrayList<>();
        this.notEligibleForPoints = new ArrayList<>();

        // Handlers
        this.eventsHandler = new EventsHandler(this);
        this.powerupHandler = new PowerupHandler(main, this);
        this.timerHandler = new GameTimerHandler(this);

        // Scoreboard
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.runners = new GameTeam(this, scoreboard, TextColor.fromHexString("#5BC625"), NamedTextColor.GREEN, GamePlayerType.RUNNER, "\uE101");
        this.taggers = new GameTeam(this, scoreboard, TextColor.fromHexString("#FF1E2D"), NamedTextColor.RED, GamePlayerType.TAGGER, "\uE102");
        this.spectators = new GameTeam(this, scoreboard, TextColor.fromHexString("A0A0A0"), NamedTextColor.GRAY, GamePlayerType.SPECTATOR, "\uE103");
        this.objective = scoreboard.registerNewObjective(UUID.randomUUID().toString(), Criteria.DUMMY, Messenger.component("Power Tag", TextColor.fromHexString("#e01507"), TextDecoration.BOLD));
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        waitingActionBar = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            if (timerHandler.activeTask() == null)
            {
                Messenger.actionBarAll(getPlayers(), Messenger.component("Waiting for more players...", NamedTextColor.WHITE));
            }
        }, 0, 1);
    }

    //region Base game logic

    public void start()
    {
        this.round = 0;
        this.countdown = true;
        this.timerHandler.RunTimer(TimerType.STARTUP, map);
        this.main.getServer().getPluginManager().registerEvents(eventsHandler, Main.getPlugin(Main.class));
        this.announceStart();
        Bukkit.getScheduler().cancelTask(waitingActionBar);
    }

    public void forceStart()
    {
        this.start();
        if (timerHandler.activeTask() instanceof StartupTimer startupTimer)
        {
            startupTimer.setTime(startupTimer.startTime());
        }
    }

    public void stop()
    {
        if (!(timerHandler.activeTask() instanceof StartupTimer))
        {
            sendPointsMessage();
        }

        ArrayList<Player> playersToTeleport = getPlayers();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> Messenger.messageAll(playersToTeleport, Messenger.component("Teleporting out in 5 seconds...", NamedTextColor.GRAY)), 100L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () ->
        {
            stopGame();
            Messenger.messageAll(playersToTeleport, Messenger.component("Teleporting out...", NamedTextColor.GRAY));
            for(Player p : playersToTeleport)
            {
                p.teleport(main.config().getSpawn());
            }
        }, 200L);

        this.timerHandler.StopTimer();
        disableEvents();
    }

    private void disableEvents()
    {
        HandlerList.unregisterAll(this.eventsHandler);
        this.getPowerupHandler().despawnPowerups();
        this.getPowerupHandler().disableHandler();
    }

    private void stopGame()
    {
        this.round = 0;
        this.clearPlayers();
        this.setStarted(false);
        this.setCountdown(false);
        this.resetScoreboard();
    }

    public void checkEndCondition()
    {
        // End game if necessary
        if (getGamePlayersOfTypes(GamePlayerType.RUNNER).size() <= 1 || round == 10)
        {
            for (GamePlayer gamePlayer : getGamePlayers())
            {
                PlayerData playerData = gamePlayer.getPlayerData();
                playerData.addPoints(gamePlayer.getPoints() + gamePlayer.getBonusPoints());
                if (gamePlayer.getType().equals(GamePlayerType.RUNNER))
                {
                    playerData.addWin();
                }
                main.config().getPlayerDataHandler().SavePlayerData(playerData);
                Messenger.log(Level.INFO, gamePlayer.getPlayer().getName() + " now has " + playerData.getTotalPoints() + " points and has " + playerData.getTotalWins() + " wins");
            }
            stop();
        }
    }

    private void resetScoreboard()
    {
        this.runners.clear();
        this.taggers.clear();
        this.spectators.clear();
    }

    public void incrementRound()
    {
        this.round++;
    }

    //endregion

    //region Handling players

    private void teleportPlayerToSpawn(Player player)
    {
        player.teleport(map.getSpawn());
    }

    public void teleportPlayersToSpawn()
    {
        ArrayList<Player> players = getPlayersOfTypes(GamePlayerType.RUNNER, GamePlayerType.TAGGER);
        for (Player player : players)
        {
            teleportPlayerToSpawn(player);
        }
    }

    public void selectTaggers()
    {
        Score line1 = this.objective.getScore(ChatColor.translateAlternateColorCodes('&', "&1&8----------------------------"));
        line1.setScore(15);
        Score empty1 = this.objective.getScore(ChatColor.translateAlternateColorCodes('&', "&1 "));
        empty1.setScore(14);
        Score header = this.objective.getScore(ChatColor.translateAlternateColorCodes('&', "&c&lTaggers"));
        header.setScore(13);

        int lowestScore = 0;
        ArrayList<GamePlayer> runners = getGamePlayersOfType(GamePlayerType.RUNNER);
        int amountOfRunners = runners.size();
        double amountToBecomeTagged = Math.ceil((float) amountOfRunners / 4f);
        for (int i = 0; i < amountToBecomeTagged; i++)
        {
            // First attempt selection
            Random rnd = new Random();
            int selected = rnd.nextInt(amountOfRunners);
            GamePlayer gamePlayer = runners.get(selected);
            // Second attempt selection loop.
            // Keep selecting until player is not already a tagger.
            while (!gamePlayer.getType().equals(GamePlayerType.RUNNER))
            {
                selected = rnd.nextInt(amountOfRunners);
                gamePlayer = runners.get(selected);
            }
            // Change player to tagger
            gamePlayer.makeTagger(null, 12 - i);
            lowestScore = 12 - i;
        }

        Score empty2 = this.objective.getScore(ChatColor.translateAlternateColorCodes('&', "&2 "));
        empty2.setScore(lowestScore - 1);
        Score line2 = this.objective.getScore(ChatColor.translateAlternateColorCodes('&', "&2&8----------------------------"));
        line2.setScore(lowestScore - 2);
        Messenger.announceTaggers(getPlayers(), TagReason.START, null, getPlayersOfType(GamePlayerType.TAGGER));
    }

    public void eliminateTaggers()
    {
        // Eliminate taggers
        for (GamePlayer gamePlayer : getGamePlayersOfType(GamePlayerType.TAGGER))
        {
            gamePlayer.eliminate(true);
            // Message everyone
            Player player = gamePlayer.getPlayer();
            playEliminateEffect(player);
            Messenger.messageAll(getPlayers(), Messenger.component(player.getName(), NamedTextColor.RED).append(Messenger.component(" is now eliminated.", NamedTextColor.DARK_RED)));
        }

        // Add point to survivors
        for (GamePlayer gamePlayer : getGamePlayersOfType(GamePlayerType.RUNNER))
        {
            gamePlayer.addPoints(1);
            Messenger.message(gamePlayer.getPlayer(), "You got 1 point for surviving this round.", NamedTextColor.GRAY);
        }

        checkEndCondition();
    }

    public void eliminatePlayer(Player player)
    {
        GamePlayer gamePlayer = getGamePlayer(player);
        if (gamePlayer == null)
        {
            return;
        }
        gamePlayer.eliminate(false);
        playEliminateEffect(gamePlayer.getPlayer());
    }

    public void swapTagger(GamePlayer damager, GamePlayer receiver)
    {
        receiver.makeTagger(damager.getPlayer(), 12);
        damager.makeRunner();
    }

    public void shuffleTaggers()
    {
        for(GamePlayer player : getGamePlayersOfType(GamePlayerType.TAGGER))
        {
            player.makeRunner();
        }
        selectTaggers();
        Messenger.titleAll(getPlayers(), Messenger.component("SHUFFLE", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD), 0, 20, 10);
    }

    //endregion

    //region Visual

    public void announceStart()
    {
        ArrayList<Player> playersNotJoined = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers())
        {
            boolean notJoined = true;
            for (Player joinedPlayer : getPlayers())
            {
                if (player.equals(joinedPlayer))
                {
                    notJoined = false;
                    break;
                }
            }
            if (notJoined)
            {
                playersNotJoined.add(player);
            }
        }
        Messenger.messageAll(playersNotJoined, Messenger.component(map.getName(), NamedTextColor.WHITE).append(Messenger.component(" is about to start.", NamedTextColor.GRAY).append(Messenger.nextLine())
                .append(Messenger.component("CLICK HERE", main.config().getPrimaryPluginColor(), TextDecoration.BOLD).clickEvent(ClickEvent.runCommand("/tag join " + map.getName())).append(Messenger.component(" to join the game.", NamedTextColor.GRAY)))));
    }

    private void playEliminateEffect(Player player)
    {
        // Play effects
        map.getSpawn().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1);
        map.getSpawn().getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 3, 1);
    }

    private void sendPointsMessage()
    {
        TextColor primaryColor = TextColor.color(main.config().getPrimaryPluginColor());
        TextColor secondaryColor = TextColor.color(main.config().getSecondaryPluginColor());
        TextComponent line = Messenger.component("────────── ", NamedTextColor.DARK_GRAY)
                .append(Messenger.component("Power Tag", primaryColor, TextDecoration.BOLD))
                .append(Messenger.component(" ──────────", NamedTextColor.DARK_GRAY));
        TextComponent topFive = Messenger.component("                    Top Five", secondaryColor, TextDecoration.BOLD).append(Messenger.nextLine()).append(Messenger.nextLine());

        // Sort players by points
        ArrayList<GamePlayer> gamePlayersSorted = new ArrayList<>(players);
        gamePlayersSorted.sort(new GamePointComparator());

        //region Create hashmap on players and their points

        HashMap<Integer, ArrayList<GamePlayer>> gamePlayersPerPoint = new HashMap<>();
        for (GamePlayer gamePlayer : gamePlayersSorted)
        {
            // Check if points are already registered
            int points = gamePlayer.getPoints();
            ArrayList<GamePlayer> gamePlayersPoint = gamePlayersPerPoint.get(points);
            // In the case there is not already a list, make a new one and add the player to it.
            if (gamePlayersPoint == null)
            {
                ArrayList<GamePlayer> newGamePlayersPoint = new ArrayList<>();
                newGamePlayersPoint.add(gamePlayer);
                gamePlayersPerPoint.put(points, newGamePlayersPoint);
                continue;
            }
            // If there is a list, add the player to it.
            gamePlayersPoint.add(gamePlayer);
            gamePlayersPerPoint.replace(points, gamePlayersPoint);
        }

        //endregion

        //region Sort hashmap in descending order

        List<java.util.Map.Entry<Integer, ArrayList<GamePlayer>>> entryList = new ArrayList<>(gamePlayersPerPoint.entrySet());
        entryList.sort((entry1, entry2) -> {
            // Sort in descending order
            return entry2.getKey().compareTo(entry1.getKey());
        });

        LinkedHashMap<Integer, ArrayList<GamePlayer>> sortedHashMap = new LinkedHashMap<>();
        for (java.util.Map.Entry<Integer, ArrayList<GamePlayer>> entry : entryList)
        {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        //endregion

        // Get index of player
        for (Player player : getPlayers())
        {
            playerIsInTopFive = false;
            java.util.Map.Entry<Integer, ArrayList<GamePlayer>> playerValue = null;
            TextComponent scores = Messenger.empty();
            for (java.util.Map.Entry<Integer, ArrayList<GamePlayer>> entry : sortedHashMap.entrySet())
            {
                int index = new ArrayList<>(sortedHashMap.keySet()).indexOf(entry.getKey());
                if (index > 4)
                {
                    ArrayList<GamePlayer> gamePlayerList = entry.getValue();
                    for (GamePlayer gamePlayer : gamePlayerList)
                    {
                        if (gamePlayer.getPlayer().getName().equals(player.getName()))
                        {
                            playerValue = entry;
                            break;
                        }
                    }
                    if (playerValue != null)
                    {
                        break;
                    }
                    continue;
                }
                scores = scores.append(createScoreText(primaryColor, secondaryColor, entry, player));
            }

            if (!playerIsInTopFive && playerValue != null)
            {
                scores = scores.append(Messenger.component("           ·····", NamedTextColor.DARK_GRAY, TextDecoration.BOLD)).append(Messenger.nextLine());
                scores = scores.append(createScoreText(primaryColor, secondaryColor, playerValue, player));
            }

            TextComponent text = line.append(Messenger.nextLine()).append(topFive).append(scores).append(Messenger.nextLine()).append(line);
            player.sendMessage(text);
        }
    }

    private TextComponent createScoreText(TextColor primaryColor, TextColor secondaryColor, java.util.Map.Entry<Integer, ArrayList<GamePlayer>> entry, Player player)
    {
        TextComponent scoreText = Messenger.component("           " + entry.getKey() + " pts", secondaryColor).append(Messenger.component(" » ", NamedTextColor.DARK_GRAY));
        TextComponent namesText = Messenger.empty();
        TextComponent names = Messenger.empty();
        ArrayList<GamePlayer> gamePlayerList = entry.getValue();
        for (GamePlayer gamePlayer : gamePlayerList)
        {
            if (gamePlayer.getPlayer().getName().equals(player.getName()))
            {
                names = names.append(Messenger.component(gamePlayer.getPlayer().getName(), secondaryColor));
                playerIsInTopFive = true;
            }
            else
            {
                names = names.append(Messenger.component(gamePlayer.getPlayer().getName(), NamedTextColor.WHITE));
            }
            if (gamePlayer.getBonusPoints() > 0)
            {
                names = names.append(Messenger.component(" (+" + gamePlayer.getBonusPoints() + " pts)", NamedTextColor.AQUA));
            }
            if (gamePlayerList.indexOf(gamePlayer) != gamePlayerList.size() - 1)
            {
                names = names.append(Messenger.component(", ", NamedTextColor.WHITE));
            }
        }
        if (gamePlayerList.size() <= 3)
        {
            namesText = names;
        }
        else
        {
            namesText = namesText.append(Messenger.component(gamePlayerList.size() + " players", NamedTextColor.WHITE))
                    .hoverEvent(HoverEvent.showText(Messenger.component("Players", primaryColor).append(Messenger.nextLine()).append(names)));
        }
        return scoreText.append(namesText).append(Messenger.nextLine());
    }

    //endregion

    //region Player logic

    public void addGamePlayer(Player player)
    {
        Messenger.messageAll(getPlayers(), Messenger.component(player.getName(), NamedTextColor.WHITE).append(Messenger.component(" joined.", NamedTextColor.GRAY)));
        if (!(timerHandler.activeTask() instanceof StartupTimer))
        {
            if (players.size() == this.main.config().getMinimumPlayers())
            {
                start();
            }
        }

        PlayerData playerData = main.config().getPlayerDataHandler().GetPlayerData(player);
        GamePlayer gamePlayer = new GamePlayer(this, player, playerData);
        this.players.add(gamePlayer);
        this.playerGameMode.put(player, player.getGameMode());
        this.playerScoreboard.put(player, player.getScoreboard());
        teleportPlayerToSpawn(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.setScoreboard(this.scoreboard);
        this.runners.addPlayerToTeam(player);
    }

    public void removeGamePlayer(Player player)
    {
        GamePlayer target = getGamePlayer(player);
        if (target == null)
        {
            player.setGameMode(playerGameMode.get(player));
            player.setScoreboard(playerScoreboard.get(player));
            playerGameMode.remove(player);
            playerScoreboard.remove(player);
            return;
        }
        if (main.config().getSpawn() != null)
        {
            player.teleport(main.config().getSpawn());
        }
        player.setGameMode(playerGameMode.get(player));
        player.setScoreboard(playerScoreboard.get(player));
        playerGameMode.remove(player);
        playerScoreboard.remove(player);
        players.remove(target);
        notEligibleForPoints.remove(target);

        runners.removePlayerFromTeam(player);
        taggers.removePlayerFromTeam(player);

        if (!started)
        {
            Messenger.messageAll(getPlayers(), Messenger.component(player.getName(), NamedTextColor.WHITE).append(Messenger.component(" left.", NamedTextColor.GRAY)));
            return;
        }

        if (timerHandler.activeTask() instanceof StartupTimer startupTimer)
        {
            if (players.size() < main.config().getMinimumPlayers())
            {
                Messenger.messageAll(getPlayers(), Messenger.component("Not enough players joined, so the start was cancelled.", NamedTextColor.WHITE));
                stop();
            }
            else if (players.size() < this.main.config().getMinimumPlayers() && startupTimer.canAbort())
            {
                Messenger.messageAll(getPlayers(), Messenger.component("Waiting for more people to join...", NamedTextColor.WHITE));
                startupTimer.setTime(120);
            }
            return;
        }

        TextColor nameColor;
        if (target.getType().equals(GamePlayerType.TAGGER))
        {
            nameColor = NamedTextColor.RED;
        }
        else
        {
            nameColor = NamedTextColor.GREEN;
        }
        eliminatePlayer(player);
        Messenger.messageAll(getPlayers(), Messenger.component(player.getName(), nameColor).append(Messenger.component(" is eliminated, because they left the game.", NamedTextColor.DARK_RED)));
        checkEndCondition();
    }

    public void addSpectator(Player player)
    {
        PlayerData playerData = main.config().getPlayerDataHandler().GetPlayerData(player);
        GamePlayer gamePlayer = new GamePlayer(this, player, playerData);
        this.playerGameMode.put(player, player.getGameMode());
        this.playerScoreboard.put(player, player.getScoreboard());
        this.notEligibleForPoints.add(gamePlayer);
        player.setScoreboard(this.scoreboard);
        gamePlayer.makeSpectator();
        teleportPlayerToSpawn(player);
    }

    private void clearPlayers()
    {
        ArrayList<GamePlayer> allPlayersAndSpectators = new ArrayList<>();
        allPlayersAndSpectators.addAll(getGamePlayers());
        allPlayersAndSpectators.addAll(notEligibleForPoints);

        for (GamePlayer player : allPlayersAndSpectators)
        {
            player.removeFromGame();
            player.getPlayer().getInventory().clear();
            player.getPlayer().setScoreboard(playerScoreboard.get(player.getPlayer()));
            player.getPlayer().setGameMode(playerGameMode.get(player.getPlayer()));
        }
        this.players.clear();
        this.notEligibleForPoints.clear();
        this.playerScoreboard.clear();
        this.playerGameMode.clear();
    }

    public ArrayList<Player> getPlayers()
    {
        ArrayList<Player> list = new ArrayList<>();
        for (GamePlayer player : players)
        {
            list.add(player.getPlayer());
        }
        for (GamePlayer player : notEligibleForPoints)
        {
            list.add(player.getPlayer());
        }
        return list;
    }

    public boolean playerHasJoined(Player player)
    {
        for (GamePlayer gamePlayer : players)
        {
            if (gamePlayer.getPlayer().equals(player))
            {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Player> getPlayersOfType(GamePlayerType type)
    {
        ArrayList<Player> list = new ArrayList<>();
        for (GamePlayer player : players)
        {
            if (player.getType().equals(type))
            {
                list.add(player.getPlayer());
            }
        }
        return list;
    }

    public ArrayList<Player> getPlayersOfTypes(GamePlayerType... types)
    {
        ArrayList<Player> list = new ArrayList<>();
        for (GamePlayer player : players)
        {
            for (GamePlayerType type : types)
            {
                if (player.getType().equals(type))
                {
                    list.add(player.getPlayer());
                    break;
                }
            }
        }
        return list;
    }

    public ArrayList<GamePlayer> getGamePlayers()
    {
        return players;
    }

    public GamePlayer getGamePlayer(Player player)
    {
        GamePlayer target = null;
        for (GamePlayer gamePlayer : getGamePlayers())
        {
            if (gamePlayer.getPlayer().getUniqueId().equals(player.getUniqueId()))
            {
                target = gamePlayer;
                break;
            }
        }
        return target;
    }

    public ArrayList<GamePlayer> getGamePlayersOfType(GamePlayerType type)
    {
        ArrayList<GamePlayer> list = new ArrayList<>();
        for (GamePlayer player : players)
        {
            if (player.getType().equals(type))
            {
                list.add(player);
            }
        }
        return list;
    }

    public ArrayList<GamePlayer> getGamePlayersOfTypes(GamePlayerType... types)
    {
        ArrayList<GamePlayer> list = new ArrayList<>();
        for (GamePlayer player : players)
        {
            for (GamePlayerType type : types)
            {
                if (player.getType().equals(type))
                {
                    list.add(player);
                    break;
                }
            }
        }
        return list;
    }

    //endregion
}
