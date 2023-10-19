package xeterios.powertag.game.powerups.powerups;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xeterios.powertag.Messenger;
import xeterios.powertag.game.handlers.PowerupHandler;

import java.util.ArrayList;


public class Shuffle implements Powerup
{

    @Override
    public ItemStack getItem()
    {
        ItemStack item = new ItemStack(Material.BOOK, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(getName());
        itemMeta.lore(new ArrayList<>()
        {{
            add(Messenger.component("Event", NamedTextColor.DARK_PURPLE));
            add(Messenger.component("New taggers are randomly selected. This could cause", NamedTextColor.GRAY));
            add(Messenger.component("a major shift in the game.", NamedTextColor.GRAY));
        }});
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public Color getPowerupColor()
    {
        return Color.PURPLE;
    }

    @Override
    public TextComponent getName()
    {
        return Messenger.component("SHUFFLE", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD);
    }

    @Override
    public void trigger(Player player, PowerupHandler handler)
    {
        handler.getGameManager().shuffleTaggers();

        Powerup.super.trigger(player, handler);
        Powerup.super.playTriggerSound(handler, player, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 10, 1, true);
    }
}