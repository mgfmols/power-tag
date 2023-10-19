package xeterios.powertag.game.powerups.powerups;

import net.kyori.adventure.text.Component;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xeterios.powertag.Messenger;
import xeterios.powertag.game.handlers.PowerupHandler;

import java.util.ArrayList;
import java.util.Objects;

public class Boost implements Powerup
{

    @Override
    public ItemStack getItem()
    {
        ItemStack item = new ItemStack(Material.SUGAR, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(getName());
        itemMeta.lore(new ArrayList<>()
        {{
            add(Messenger.component("Effect", NamedTextColor.YELLOW));
            add(Messenger.component("Gain a small speed boost for " + (int) (effectDuration() * 0.05) + " seconds.", NamedTextColor.GRAY));
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
        return Color.AQUA;
    }

    @Override
    public TextComponent getName()
    {
        return Messenger.component("BOOST", NamedTextColor.AQUA, TextDecoration.BOLD);
    }

    @Override
    public void trigger(Player player, PowerupHandler handler)
    {
        int duration = effectDuration();
        if (player.getPotionEffect(PotionEffectType.SPEED) != null)
        {
            duration += Objects.requireNonNull(player.getPotionEffect(PotionEffectType.SPEED)).getDuration();
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 2));

        Powerup.super.trigger(player, handler);
        Powerup.super.playTriggerSound(handler, player, Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2, false);
    }

    private int effectDuration()
    {
        return 60;
    }
}