package xeterios.powertag.commands.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xeterios.powertag.Main;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.game.GameManager;
import xeterios.powertag.game.timers.StartupTimer;

import java.util.ArrayList;

public record Random(String[] args) implements Cmd
{
    @Override
    public String GetPermissionNode()
    {
        return "powertag.random";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " random";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Join a random game of Power Tag";
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        if (!(sender instanceof Player player))
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SENDER_MUST_BE_PLAYER);
            return;
        }

        // Check map requirements
        Location spawn = config.getSpawn();
        if (spawn == null)
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SPAWN_NO_SPAWN);
            return;
        }
        if (spawn.getWorld() == null)
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SPAWN_NO_WORLD);
            return;
        }

        // Check map requirements
        ArrayList<Integer> indexesToSkip = new ArrayList<>();
        GameManager manager = null;
        int attempt = 0;
        while (manager == null)
        {
            attempt++;
            if (attempt == 15)
            {
                CommandHandler.sendMessage(config, sender, CommandMessageReason.GAME_MANAGER_NOT_FOUND);
                return;
            }
            // Getting GameManager with highest playerCount that hasn't started yet.
            int playerCount = 0;
            GameManager highestPlayerCount = null;
            for(GameManager gameManager : main.getGameManagers())
            {
                if (gameManager.isStarted())
                {
                    continue;
                }
                if (gameManager.getPlayers().size() > playerCount)
                {
                    playerCount = gameManager.getPlayers().size();
                    highestPlayerCount = gameManager;
                }
            }
            if (playerCount > 0)
            {
                manager = highestPlayerCount;
            }
            else // If there are multiple GameManagers with 0 player count, choose a random GameManager.
            {
                // Choosing random GameManager
                java.util.Random rnd = new java.util.Random();
                int index = rnd.nextInt(main.getGameManagers().size());
                // Preventing index to be chosen again if failed previously
                while (indexesToSkip.contains(index))
                {
                    index = rnd.nextInt(main.getGameManagers().size());
                }
                // Getting GameManager
                GameManager gameManager = main.getGameManagers().get(index);
                // If GameManager is not in startup phase, skip
                if (gameManager.isStarted())
                {
                    indexesToSkip.add(index);
                }
                else
                {
                    manager = gameManager;
                }
            }
        }

        GameManager playerHasJoined = main.playerHasJoined(player);
        if (playerHasJoined != null)
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.PLAYER_ALREADY_JOINED, playerHasJoined.getMap().getName());
            return;
        }

        CommandHandler.sendMessage(config, sender, CommandMessageReason.MAP_JOINED, manager.getMap().getName());
        manager.addGamePlayer(player);
    }
}