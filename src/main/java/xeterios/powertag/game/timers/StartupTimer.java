package xeterios.powertag.game.timers;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import xeterios.powertag.Main;
import xeterios.powertag.Messenger;
import xeterios.powertag.configuration.Map;
import xeterios.powertag.game.GameManager;
import xeterios.powertag.game.GameTimerHandler;

public class StartupTimer extends BaseTimer
{

    private boolean canAbort;
    private final int startTime;

    public StartupTimer(GameTimerHandler handler, GameManager gameManager, Map map)
    {
        super(handler, gameManager, map);
        startTime = 30;
        canAbort = true;

        if (gameManager.getPlayers().size() >= gameManager.getMain().config().getMinimumPlayers())
        {
            super.i = startTime;
        }
        else
        {
            super.i = 120;
        }
    }

    public int startTime()
    {
        return startTime;
    }

    public boolean canAbort()
    {
        return canAbort;
    }

    @Override
    public void run()
    {
        if (i == 0)
        {
            super.stopTimer();
            // Selecting Taggers
            gameManager.setCountdown(false);
            gameManager.setStarted(true);
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), gameManager::selectTaggers);
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), gameManager.getPowerupHandler()::enableHandler);
            handler.RunTimer(TimerType.GAME, map);
            // Showing start screen
            Messenger.titleAll(gameManager.getPlayers(), Messenger.component("Start", NamedTextColor.GREEN, TextDecoration.BOLD), 0, 20, 0);
            Messenger.playSoundAll(gameManager.getPlayers(), Sound.BLOCK_NOTE_BLOCK_HAT, 1);
        }
        else
        {
            TextColor titleColor = NamedTextColor.WHITE;

            // Showing correct timer color
            switch (i)
            {
                case 10, 9, 8, 7, 6 -> titleColor = NamedTextColor.GREEN;
                case 5, 4 -> titleColor = NamedTextColor.YELLOW;
                case 3, 2 -> titleColor = LegacyComponentSerializer.legacyAmpersand().deserialize("&x&f&f&a&6&0&0").color();
                case 1 -> titleColor = NamedTextColor.RED;
            }

            // Building actionbar component
            TextComponent actionBar = Messenger.component("Game starts in ", NamedTextColor.GRAY)
                    .append(Messenger.component(String.valueOf(i), titleColor, TextDecoration.BOLD))
                    .append(Messenger.component(" seconds", NamedTextColor.GRAY));
            Messenger.actionBarAll(gameManager.getPlayers(), actionBar);

            if (i == startTime)
            {
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), gameManager::teleportPlayersToSpawn);
                Messenger.titleAll(gameManager.getPlayers(), Messenger.component("Power Tag", Main.getPlugin(Main.class).config().getPrimaryPluginColor(), TextDecoration.BOLD), Messenger.component("Map: " + map.getName(), NamedTextColor.WHITE), 0, 80, 0);
                canAbort = false;
            }

            if (i <= 10)
            {
                // Building title component
                Messenger.titleAll(gameManager.getPlayers(), Messenger.component("Starting in", NamedTextColor.WHITE), Messenger.component(String.valueOf(i), titleColor, TextDecoration.BOLD), 0, 20, 0);
                Messenger.playSoundAll(gameManager.getPlayers(), Sound.BLOCK_NOTE_BLOCK_HAT, 1);
            }
        }
        i--;
    }
}
