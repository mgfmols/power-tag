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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xeterios.powertag.Messenger;
import xeterios.powertag.game.handlers.PowerupHandler;

import java.util.ArrayList;

public class Chains implements Powerup
{

    @Override
    public ItemStack getItem()
    {
        ItemStack item = new ItemStack(Material.SPLASH_POTION, 1);
        PotionMeta itemMeta = (PotionMeta) item.getItemMeta();
        itemMeta.displayName(getName());
        itemMeta.lore(new ArrayList<>()
        {{
            add(Messenger.component("Item", NamedTextColor.AQUA));
            add(Messenger.component("Any target hit with this potion is slowed", NamedTextColor.GRAY));
            add(Messenger.component("down and blinded for " + (int) (effectDuration() * 0.05) + " seconds.", NamedTextColor.GRAY));
        }});
        itemMeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, effectDuration(), 2), false);
        itemMeta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, effectDuration(), 2), false);
        itemMeta.setColor(Color.fromRGB(85, 85, 85));
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public Color getPowerupColor()
    {
        return Color.fromRGB(85, 85, 85);
    }

    @Override
    public TextComponent getName()
    {
        return Messenger.component("CHAINS", NamedTextColor.DARK_GRAY, TextDecoration.BOLD);
    }

    @Override
    public void trigger(Player player, PowerupHandler handler)
    {
        player.getInventory().setItemInOffHand(getItem());
        if (player.getInventory().getItem(9) != null)
        {
            player.getInventory().setItem(9, new ItemStack(Material.AIR, 1));
        }

        Powerup.super.trigger(player, handler);
        Powerup.super.playTriggerSound(handler, player, Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2, false);
    }

    private int effectDuration()
    {
        return 100;
    }
}
