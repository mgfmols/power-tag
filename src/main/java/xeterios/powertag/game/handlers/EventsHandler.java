package xeterios.powertag.game.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import xeterios.powertag.Messenger;
import xeterios.powertag.game.GameManager;
import xeterios.powertag.game.GamePlayer;
import xeterios.powertag.game.GamePlayerType;
import xeterios.powertag.game.TagReason;

import java.util.ArrayList;
import java.util.List;

public record EventsHandler(GameManager gameManager) implements Listener
{

    @EventHandler
    public void PreventItemDropping(PlayerDropItemEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    public void PreventInventoryInteract(InventoryInteractEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    public void PreventInventoryItemMove(InventoryMoveItemEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    public void PreventInventoryClicking(InventoryClickEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    public void PreventBlockBreaking(BlockBreakEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    public void PreventBlockPlacing(BlockPlaceEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    public void PreventHologramInteraction(PlayerArmorStandManipulateEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void DetectTagByHit(EntityDamageByEntityEvent e)
    {
        // Check if both damager and receiver are players
        if (!(e.getDamager() instanceof Player damager))
        {
            return;
        }
        if (!(e.getEntity() instanceof Player receiver))
        {
            return;
        }

        GamePlayer gamePlayerDamager = gameManager.getGamePlayer(damager);
        GamePlayer gamePlayerReceiver = gameManager.getGamePlayer(receiver);

        // Check if damager is a tagger
        if (!gamePlayerDamager.getType().equals(GamePlayerType.TAGGER))
        {
            e.setCancelled(true);
            return;
        }
        // Check if receiver is a runner
        if (!gamePlayerReceiver.getType().equals(GamePlayerType.RUNNER))
        {
            e.setCancelled(true);
            return;
        }

        // Swap the tag
        gameManager.swapTagger(gamePlayerDamager, gamePlayerReceiver);
        Messenger.announceTaggers(gameManager.getPlayers(), TagReason.HIT, gamePlayerDamager.getPlayer(), new ArrayList<>(List.of(gamePlayerReceiver.getPlayer())));
    }

    @EventHandler
    public void EliminateOnQuit(PlayerQuitEvent e)
    {
        gameManager.removeGamePlayer(e.getPlayer());
        e.quitMessage(Messenger.empty());
    }

    @EventHandler
    public void LoadResourcePack(PlayerResourcePackStatusEvent e)
    {
        e.getPlayer().setResourcePack("", "");
    }
}
