package xeterios.powertag.game.timers;

import xeterios.powertag.Messenger;
import xeterios.powertag.configuration.Map;
import xeterios.powertag.game.GameManager;
import xeterios.powertag.game.GameTimerHandler;

import java.util.TimerTask;
import java.util.logging.Level;

public class BaseTimer extends TimerTask
{
    protected GameTimerHandler handler;
    protected final GameManager gameManager;
    protected final Map map;
    protected int i;

    public BaseTimer(GameManager gameManager, Map map)
    {
        this.gameManager = gameManager;
        this.map = map;
    }

    public BaseTimer(GameTimerHandler handler, GameManager gameManager, Map map)
    {
        this.handler = handler;
        this.gameManager = gameManager;
        this.map = map;
    }

    @Override
    public void run()
    {
        if (i == 0)
        {
            this.stopTimer();
        }
        else
        {
            Messenger.log(Level.WARNING, "A timer is not working correctly. Please message the plugin developer.");
        }
        i--;
    }

    public void setTime(int time)
    {
        this.i = time;
    }

    protected void stopTimer()
    {
        this.cancel();
        handler.StopTimer();
    }
}
