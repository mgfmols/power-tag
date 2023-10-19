package xeterios.powertag.game.powerups;

import org.bukkit.Bukkit;
import xeterios.powertag.configuration.Map;
import xeterios.powertag.game.GameManager;
import xeterios.powertag.game.handlers.PowerupHandler;
import xeterios.powertag.game.timers.BaseTimer;

public class PowerupTimer extends BaseTimer
{
    private final PowerupHandler powerupHandler;

    public PowerupTimer(PowerupHandler powerupHandler, GameManager manager, Map map)
    {
        super(manager, map);
        this.powerupHandler = powerupHandler;
        this.i = 10;
    }

    @Override
    public void run()
    {
        if (i == 0)
        {
            int amountOfPowerups = (int) Math.ceil(Math.max(gameManager.getGamePlayers().size() * 0.33f, 1f));
            for(int j = 0; j < amountOfPowerups; j++)
            {
                Bukkit.getScheduler().runTaskLater(gameManager.getMain(), powerupHandler::spawnPowerup, j * 4L);
            }
            setTime(10);
        }
        else
        {
            i--;
        }
    }
}
