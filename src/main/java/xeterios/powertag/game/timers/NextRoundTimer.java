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

public class NextRoundTimer extends BaseTimer
{

    public NextRoundTimer(GameTimerHandler handler, GameManager gameManager, Map map)
    {
        super(handler, gameManager, map);
        super.i = 5;
    }

    @Override
    public void run()
    {
        if (i == 0)
        {
            super.stopTimer();
            gameManager.incrementRound();
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), gameManager::selectTaggers);
            Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), gameManager.getPowerupHandler()::enableTimer);
            // Update scoreboard
            handler.RunTimer(TimerType.GAME, map);
            Messenger.playSoundAll(gameManager.getPlayers(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1);
        }
        else
        {
            TextColor titleColor = NamedTextColor.WHITE;
            switch (i)
            {
                case 5, 4 -> titleColor = NamedTextColor.YELLOW;
                case 3, 2 -> titleColor = LegacyComponentSerializer.legacyAmpersand().deserialize("&x&f&f&a&6&0&0").color();
                case 1 -> titleColor = NamedTextColor.RED;
            }

            TextComponent actionBar = Messenger.component("Next round starts in ", NamedTextColor.GRAY)
                    .append(Messenger.component(String.valueOf(i), titleColor, TextDecoration.BOLD))
                    .append(Messenger.component(" seconds.", NamedTextColor.GRAY));
            Messenger.actionBarAll(gameManager.getPlayers(), actionBar);

            if (i == 3)
            {
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), gameManager::teleportPlayersToSpawn);
            }
            if (i <= 3)
            {
                Messenger.playSoundAll(gameManager.getPlayers(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0);
            }
        }
        i--;
    }
}
