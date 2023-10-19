package xeterios.powertag.inventories.inventories;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xeterios.powertag.Messenger;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.game.powerups.PowerupList;
import xeterios.powertag.inventories.CustomInventory;
import xeterios.powertag.players.PlayerDataHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class InformationInventory extends CustomInventory
{

    public InformationInventory(PlayerDataHandler handler, Config config)
    {
        super(handler, 54, Messenger.component("Information", NamedTextColor.DARK_GRAY), config);
        SetItems();
        super.FillRemaining(Material.GRAY_STAINED_GLASS_PANE, true);
    }

    // Use SetItems to set the items in the super class.
    // These items are used to build the inventory with.
    // Region is used to clean up IDE
    protected void SetItems()
    {
        HashMap<Integer, ItemStack> items = new HashMap<>();

        items.put(12, getGameInformationItem(1));
        items.put(13, getGameInformationItem(2));
        items.put(14, getGameInformationItem(3));

        items.put(28, PowerupList.getPowerup("boost").getItem());
        items.put(29, PowerupList.getPowerup("infrasight").getItem());
        items.put(30, PowerupList.getPowerup("invisibility").getItem());
        items.put(31, PowerupList.getPowerup("chains").getItem());
        items.put(32, PowerupList.getPowerup("sniper").getItem());
        items.put(33, PowerupList.getPowerup("firework").getItem());
        items.put(34, PowerupList.getPowerup("shuffle").getItem());

        ItemStack closeItem = new ItemStack(Material.BARRIER, 1);
        ItemMeta closeItemMeta = closeItem.getItemMeta();
        closeItemMeta.displayName(Messenger.component("Close", NamedTextColor.RED, TextDecoration.BOLD));
        closeItem.setItemMeta(closeItemMeta);
        items.put(49, closeItem);

        super.items = items;
        BuildInventory();
    }

    @EventHandler
    @Override
    public void OnClick(InventoryClickEvent e)
    {
        if (!(e.getWhoClicked() instanceof Player player))
        {
            return;
        }

        ItemStack itemStack = e.getCurrentItem();
        if (itemStack == null)
        {
            e.setCancelled(true);
            return;
        }

        if (itemStack.getType() == Material.BARRIER)
        {
            player.closeInventory();
        }
        e.setCancelled(true);
    }

    private ItemStack getGameInformationItem(int index)
    {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Messenger.component("Game Explanation #" + index, NamedTextColor.YELLOW));
        switch (index)
        {
            case 1 -> itemMeta.lore(new ArrayList<>(){{
                add(Messenger.component("Power Tag is the game of tag, but with power-ups.", NamedTextColor.GRAY));
                add(Messenger.component("Every round, random taggers will be selected.", NamedTextColor.GRAY));
                add(Messenger.component("At the end of each round, all taggers will be eliminated.", NamedTextColor.GRAY));
                add(Messenger.empty());
                add(Messenger.component("New taggers will be selected when a new round starts.", NamedTextColor.GRAY));
                add(Messenger.component("Every round you survive, you will gain 1 point.", NamedTextColor.GRAY));
                add(Messenger.component("The last player to remain will be the winner.", NamedTextColor.GRAY));
            }});
            case 2 -> itemMeta.lore(new ArrayList<>(){{
                add(Messenger.component("During the game, you can use power-ups to influence", NamedTextColor.GRAY));
                add(Messenger.component("the game. They can be used at your advantage.", NamedTextColor.GRAY));
                add(Messenger.empty());
                add(Messenger.component("Every couple of seconds, power-ups will spawn in the map.", NamedTextColor.GRAY));
                add(Messenger.component("Picking up these power-ups will give you their effect.", NamedTextColor.GRAY));
                add(Messenger.component("Some power-ups give potion effects, others give you items.", NamedTextColor.GRAY));
            }});
            case 3 -> itemMeta.lore(new ArrayList<>(){{
                add(Messenger.component("The game is in free-for-all format, because you want", NamedTextColor.GRAY));
                add(Messenger.component("to be the last player to remain. At the end of each", NamedTextColor.GRAY));
                add(Messenger.component("game you will see how many points you have won.", NamedTextColor.GRAY));
            }});
        }
        item.setItemMeta(itemMeta);
        return item;
    }
}
