package xeterios.powertag.game.powerups.powerups;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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

public class Firework implements Powerup
{

    @Override
    public ItemStack getItem()
    {
        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(getName());
        itemMeta.lore(new ArrayList<>()
        {{
            add(Messenger.component("Item", NamedTextColor.AQUA));
            add(Messenger.component("Launch yourself up into the air.", NamedTextColor.GRAY));
            add(Messenger.component("Can be used while in mid-air.", NamedTextColor.GRAY));
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
        return Color.fromRGB(245, 122, 22);
    }

    @Override
    public TextComponent getName()
    {
        return Messenger.component("FIREWORK", TextColor.fromHexString("#f57a16"), TextDecoration.BOLD);
    }

    @Override
    public void trigger(Player player, PowerupHandler handler)
    {
        player.getInventory().setItemInOffHand(getItem());

        Powerup.super.trigger(player, handler);
        Powerup.super.playTriggerSound(handler, player, Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2, false);
    }

    public static ItemStack getFirework()
    {
        ItemStack item = new ItemStack(Material.FIREWORK_ROCKET, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Messenger.component("FIREWORK", TextColor.fromHexString("#f57a16"), TextDecoration.BOLD));
        itemMeta.lore(new ArrayList<>()
        {{
            add(Messenger.component("Item", NamedTextColor.AQUA));
            add(Messenger.component("Launch yourself up into the air.", NamedTextColor.GRAY));
            add(Messenger.component("Can be used while in mid-air.", NamedTextColor.GRAY));
        }});
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static float launchHeight()
    {
        return 1f;
    }
}