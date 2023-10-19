package xeterios.powertag.game.powerups.powerups;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xeterios.powertag.Messenger;
import xeterios.powertag.game.handlers.PowerupHandler;

public interface Powerup
{
    ItemStack getItem();

    Color getPowerupColor();

    TextComponent getName();

    default void trigger(Player p, PowerupHandler handler)
    {
        Messenger.title(p, getName(), Messenger.component("acquired!", NamedTextColor.GRAY), 0, 20, 10);
    }

    default void playTriggerSound(PowerupHandler powerupHandler, Player p, Sound sound, float volume, float pitch, boolean globalSound)
    {
        if (globalSound)
        {
            Messenger.playSoundAll(powerupHandler.getGameManager().getPlayers(), sound, pitch);
        }
        else
        {
            Messenger.playSound(p, p.getLocation(), sound, volume, pitch);
        }
    }
}
