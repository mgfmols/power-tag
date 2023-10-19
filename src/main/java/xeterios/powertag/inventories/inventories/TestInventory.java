package xeterios.powertag.inventories.inventories;

import xeterios.powertag.Messenger;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.inventories.CustomInventory;
import xeterios.powertag.players.PlayerDataHandler;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class TestInventory extends CustomInventory
{

    public TestInventory(PlayerDataHandler handler, Config config)
    {
        super(handler, 27, Messenger.component("Test Inventory", NamedTextColor.DARK_GRAY), config);
        SetItems();
    }

    // Use SetItems to set the items in the super class.
    // These items are used to build the inventory with.
    // Region is used to clean up IDE
    protected void SetItems()
    {
        HashMap<Integer, ItemStack> items = new HashMap<>();
        //region Test Item
        ItemStack testItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta testMeta = testItem.getItemMeta();
        testMeta.displayName(Messenger.component("This is a test", NamedTextColor.DARK_GRAY, TextDecoration.BOLD));
        testMeta.lore(List.of(Messenger.component("This item is a test.", NamedTextColor.GRAY, TextDecoration.ITALIC)));
        testItem.setItemMeta(testMeta);
        items.put(13, testItem);
        //endregion
        super.items = items;
        BuildInventory();
    }

    // Use @Override public void OnClick(InventoryClickEvent e) to override the event handler InventoryClickEvent
    @EventHandler
    @Override
    public void OnClick(InventoryClickEvent e)
    {
        if (e.getWhoClicked() instanceof Player player)
        {
            Messenger.message(player, Messenger.component("Test", NamedTextColor.WHITE));
        }
        e.setCancelled(true);
    }
}
