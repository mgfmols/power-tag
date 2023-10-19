package xeterios.powertag.game;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import xeterios.powertag.Messenger;
import xeterios.powertag.game.powerups.EffectTimer;

import java.util.Timer;
import java.util.UUID;

public class GameTeam
{
    @Getter private final GameManager gameManager;
    @Getter private Timer infraSightTimer;
    @Getter private EffectTimer infraSightTimerTask;
    @Getter private final Scoreboard scoreboard;
    @Getter private final Team team;
    @Getter private final GamePlayerType type;

    public GameTeam(GameManager gameManager, Scoreboard scoreboard, TextColor nameColor, NamedTextColor glowColor, GamePlayerType type, String icon)
    {
        this.gameManager = gameManager;
        this.scoreboard = scoreboard;
        UUID uuid = UUID.randomUUID();
        this.team = scoreboard.registerNewTeam(uuid.toString());
        this.team.prefix(Messenger.component(icon).append(Messenger.space().color(nameColor)));
        this.team.color(glowColor);
        this.type = type;
    }

    public void clear()
    {
        for(String entry : team.getEntries())
        {
            team.removeEntry(entry);
        }
    }

    public void addPlayerToTeam(Player player)
    {
        team.addEntry(player.getName());
    }

    public void removePlayerFromTeam(Player player)
    {
        team.removeEntry(player.getName());
    }

    public void activateInfraSight(int duration)
    {
        PotionEffect effect = new PotionEffect(PotionEffectType.GLOWING, duration, 0, true, true, true);
        this.infraSightTimerTask = new EffectTimer(gameManager, gameManager.getMap(), this, effect);
        this.infraSightTimer = new Timer();
        this.infraSightTimer.schedule(infraSightTimerTask, 0, 200);
    }

    public void deactivateEffect(EffectTimer timer)
    {
        if (timer.equals(infraSightTimerTask))
        {
            infraSightTimer.cancel();
        }
    }
}
