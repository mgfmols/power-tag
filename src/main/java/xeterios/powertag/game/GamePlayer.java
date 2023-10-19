package xeterios.powertag.game;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import xeterios.powertag.Messenger;
import xeterios.powertag.players.PlayerData;

public class GamePlayer
{
    private final GameManager gameManager;
    @Getter private final Player player;
    @Getter private final PlayerData playerData;
    @Getter private GamePlayerType type;
    @Getter private int points;
    @Getter private int bonusPoints;

    public GamePlayer(GameManager gameManager, Player player, PlayerData playerData)
    {
        this.gameManager = gameManager;
        this.player = player;
        this.playerData = playerData;
        this.type = GamePlayerType.RUNNER;
        this.points = 0;
        this.bonusPoints = 0;
    }

    public void addPoints(int amount)
    {
        this.points += amount;
    }

    public void addBonusPoints(int amount)
    {
        this.bonusPoints += amount;
    }

    public void makeRunner()
    {
        this.changeType(GamePlayerType.RUNNER);
        this.removeEffects();
        this.clearGlass();

        this.gameManager.getRunners().addPlayerToTeam(this.player);
        this.gameManager.getTaggers().removePlayerFromTeam(this.player);
        this.gameManager.getScoreboard().resetScores(this.player.getName());
    }

    public void makeTagger(Player taggedBy, int score)
    {
        this.changeType(GamePlayerType.TAGGER);
        this.gameManager.getRunners().removePlayerFromTeam(this.player);
        this.gameManager.getTaggers().addPlayerToTeam(this.player);
        this.player.removePotionEffect(PotionEffectType.GLOWING);
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 100000, 1, false, false);
        speed.apply(this.player);

        if (taggedBy == null)
        {
            Score scoreValue = this.gameManager.getObjective().getScore(player);
            scoreValue.setScore(score);
        }
        else
        {
            Score scoreValue = this.gameManager.getObjective().getScore(taggedBy);
            Score playerScore = this.gameManager.getObjective().getScore(getPlayer());
            playerScore.setScore(scoreValue.getScore());
        }

        ItemStack glass = new ItemStack(Material.RED_STAINED_GLASS, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.displayName(Messenger.component("TAGGER!!!", NamedTextColor.DARK_RED, TextDecoration.BOLD));
        glass.setItemMeta(glassMeta);

        for (int i = 0; i < 9; i++)
        {
            player.getInventory().setItem(i, glass);
        }
    }

    public void makeSpectator()
    {
        this.changeType(GamePlayerType.SPECTATOR);
        this.getPlayer().setGameMode(GameMode.SPECTATOR);
        this.gameManager.getRunners().removePlayerFromTeam(this.player);
        this.gameManager.getTaggers().removePlayerFromTeam(this.player);
        this.gameManager.getSpectators().addPlayerToTeam(this.player);
        this.gameManager.getScoreboard().resetScores(this.player.getName());
    }

    public void removeFromGame()
    {
        this.removeEffects();
        this.clearGlass();
        this.gameManager.getRunners().removePlayerFromTeam(this.player);
        this.gameManager.getTaggers().removePlayerFromTeam(this.player);
        this.gameManager.getScoreboard().resetScores(this.player.getName());
    }

    public void eliminate(boolean makeSpectator)
    {
        if (makeSpectator)
        {
            makeSpectator();
        }
        this.clearGlass();
        this.removeEffects();
        if (this.getPlayerData() == null)
        {
            return;
        }
        this.getPlayerData().resetWinStreakCount();
    }

    private void changeType(GamePlayerType newType)
    {
        this.type = newType;
    }

    private void removeEffects()
    {
        this.getPlayer().removePotionEffect(PotionEffectType.SPEED);
        this.getPlayer().removePotionEffect(PotionEffectType.GLOWING);
    }

    private void clearGlass()
    {
        ItemStack air = new ItemStack(Material.AIR, 1);
        for (int i = 0; i < 9; i++)
        {
            this.getPlayer().getInventory().setItem(i, air);
        }
    }
}
