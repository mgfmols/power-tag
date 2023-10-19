package xeterios.powertag.game.powerups.powerups;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xeterios.powertag.Messenger;
import xeterios.powertag.game.GameManager;
import xeterios.powertag.game.GamePlayerType;
import xeterios.powertag.game.GameTeam;
import xeterios.powertag.game.handlers.PowerupHandler;

import java.util.ArrayList;

public class InfraSight implements Powerup
{
    @Override
    public ItemStack getItem()
    {
        ItemStack item = new ItemStack(Material.GLOWSTONE_DUST, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(getName());
        itemMeta.lore(new ArrayList<>()
        {{
            add(Messenger.component("Effect", NamedTextColor.YELLOW));
            add(Messenger.component("Picking up this power-up makes all players of the", NamedTextColor.GRAY));
            add(Messenger.component("opposite team visible through walls for " + (int) (effectDuration() * 0.05) + " seconds.", NamedTextColor.GRAY));
            add(Messenger.empty());
            add(Messenger.component("Taggers are displayed in red, while runners are", NamedTextColor.GRAY));
            add(Messenger.component("displayed in green.", NamedTextColor.GRAY));
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
        return Color.YELLOW;
    }

    @Override
    public TextComponent getName()
    {
        return Messenger.component("INFRA SIGHT", NamedTextColor.YELLOW, TextDecoration.BOLD);
    }

    @Override
    public void trigger(Player player, PowerupHandler handler)
    {
        GameManager gameManager = handler.getGameManager();
        GameTeam runners = gameManager.getRunners();
        GameTeam taggers = gameManager.getTaggers();
        if (gameManager.getPlayersOfType(runners.getType()).contains(player))
        {
            giveEffect(handler, taggers, getDuration(gameManager, taggers));
        }
        else if (gameManager.getPlayersOfType(taggers.getType()).contains(player))
        {
            giveEffect(handler, runners, getDuration(gameManager, runners));
        }

        Powerup.super.trigger(player, handler);
        Powerup.super.playTriggerSound(handler, player, Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2, false);
    }

    private int effectDuration()
    {
        return 100;
    }

    private int getDuration(GameManager gameManager, GameTeam team)
    {
        for (Player player : gameManager.getPlayersOfType(team.getType()))
        {
            if (player.getPotionEffect(PotionEffectType.GLOWING) == null)
            {
                continue;
            }
            PotionEffect presentEffect = player.getPotionEffect(PotionEffectType.GLOWING);
            if (presentEffect == null)
            {
                continue;
            }
            return presentEffect.getDuration() + effectDuration();
        }
        return effectDuration();
    }

    private void giveEffect(PowerupHandler handler, GameTeam team, int duration)
    {
        if (team.getType().equals(GamePlayerType.RUNNER))
        {
            handler.getGameManager().getRunners().activateInfraSight(duration);
        }
        else if (team.getType().equals(GamePlayerType.TAGGER))
        {
            handler.getGameManager().getTaggers().activateInfraSight(duration);
        }
    }
}

