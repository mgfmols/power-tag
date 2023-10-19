package xeterios.powertag.game.timers;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import xeterios.powertag.Main;
import xeterios.powertag.Messenger;
import xeterios.powertag.configuration.Map;
import xeterios.powertag.game.GameManager;
import xeterios.powertag.game.GamePlayerType;
import xeterios.powertag.game.GameTimerHandler;

public class GameTimer extends BaseTimer
{

    private boolean activated;

    public GameTimer(GameTimerHandler handler, GameManager gameManager, Map map)
    {
        super(handler, gameManager, map);
        super.i = (int) (Math.max(30 - (gameManager.getRound() * 5), 5) * gameManager.getMain().config().getTimerMultiplier());
        this.activated = false;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), () -> {
            if (activated)
            {
                Particle.DustOptions options;
                if (i > 10 && i <= 15)
                {
                    options = new Particle.DustOptions(Color.YELLOW, 1);
                }
                else if (i > 7 && i <= 10)
                {
                    options = new Particle.DustOptions(Color.ORANGE, 1);
                }
                else if (i > 5 && i <= 7)
                {
                    options = new Particle.DustOptions(Color.ORANGE, 2);
                }
                else if (i <= 5)
                {
                    options = new Particle.DustOptions(Color.RED, 2);
                }
                else
                {
                    options = new Particle.DustOptions(Color.YELLOW, 1);
                }
                for (Player p : gameManager.getPlayersOfType(GamePlayerType.TAGGER))
                {
                    Location location = p.getLocation();
                    location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0.5, 1, 0.5, options);
                }
            }
        }, 0, 1);
    }

    @Override
    public void run()
    {
        if (i == 0)
        {
            super.stopTimer();
            this.activated = false;
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), gameManager::eliminateTaggers);
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), gameManager.getPowerupHandler()::despawnPowerups);
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), gameManager.getPowerupHandler()::disableTimer);
            handler.RunTimer(TimerType.NEXT_ROUND, map);
        }
        else
        {
            TextComponent actionBar = Messenger.empty()
                    .append(Messenger.component("Taggers will be eliminated in ", NamedTextColor.RED))
                    .append(Messenger.component(String.valueOf(i), NamedTextColor.RED, TextDecoration.BOLD))
                    .append(Messenger.component(" seconds.", NamedTextColor.RED));
            Messenger.actionBarAll(gameManager.getPlayers(), actionBar);

            if (i == 15)
            {
                this.activated = true;
            }

            if (i <= 10)
            {
                Messenger.playSoundAll(gameManager.getPlayers(), Sound.BLOCK_NOTE_BLOCK_HAT, 1);
            }
        }
        i--;
    }
}
