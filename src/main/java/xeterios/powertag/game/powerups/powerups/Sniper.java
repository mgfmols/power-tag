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

public class Sniper implements Powerup
{

    @Override
    public ItemStack getItem()
    {
        ItemStack item = new ItemStack(Material.BOW, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(getName());
        itemMeta.lore(new ArrayList<>()
        {{
            add(Messenger.component("Item", NamedTextColor.AQUA));
            add(Messenger.component("If you are a tagger, you can use this bow to shoot runners.", NamedTextColor.GRAY));
            add(Messenger.component("The target you hit will then become a tagger and", NamedTextColor.GRAY));
            add(Messenger.component("you will become a runner.", NamedTextColor.GRAY));
            add(Messenger.empty());
            add(Messenger.component("If you manage to hit a player from ", NamedTextColor.GRAY).append(Messenger.component("20 meters", NamedTextColor.AQUA)).append(Messenger.component(" or", NamedTextColor.GRAY)));
            add(Messenger.component("further, you will gain a bonus point.", NamedTextColor.GRAY));
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
        return Color.RED;
    }

    @Override
    public TextComponent getName()
    {
        return Messenger.component("SNIPER", NamedTextColor.RED, TextDecoration.BOLD);
    }

    @Override
    public void trigger(Player player, PowerupHandler handler)
    {
        player.getInventory().setItemInOffHand(getItem());
        player.getInventory().setItem(9, new ItemStack(Material.ARROW, 1));

        Powerup.super.trigger(player, handler);
        Powerup.super.playTriggerSound(handler, player, Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2, false);
    }

    public static ItemStack getBow()
    {
        ItemStack item = new ItemStack(Material.BOW, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Messenger.component("SNIPER", NamedTextColor.RED, TextDecoration.BOLD));
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static int bonusPointDistance()
    {
        return 20;
    }

    public static int bonusPointAmount()
    {
        return 1;
    }
}
