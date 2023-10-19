package xeterios.powertag.game;

import xeterios.powertag.configuration.Map;
import xeterios.powertag.game.timers.*;

import java.util.Timer;
import java.util.TimerTask;

public class GameTimerHandler
{
    private final GameManager gameManager;
    private Timer timer;
    private TimerTask activeTask;

    public GameTimerHandler(GameManager gameManager)
    {
        this.gameManager = gameManager;
        this.timer = new Timer();
    }

    public TimerTask GetTimer(TimerType type, Map map)
    {
        return switch (type)
        {
            case STARTUP -> new StartupTimer(this, gameManager, map);
            case GAME -> new GameTimer(this, gameManager, map);
            case NEXT_ROUND -> new NextRoundTimer(this, gameManager, map);
        };
    }

    public void RunTimer(TimerType type, Map map)
    {
        this.timer = new Timer();
        if (GetTimer(type, map) != null)
        {
            activeTask = GetTimer(type, map);
            timer.schedule(activeTask, 0, 1000);
        }
    }

    public void StopTimer()
    {
        this.timer.cancel();
        this.timer.purge();
        this.timer = new Timer();
    }

    public TimerTask activeTask()
    {
        return activeTask;
    }
}
