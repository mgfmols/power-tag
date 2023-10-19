package xeterios.powertag.inventories.inventories;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xeterios.powertag.Messenger;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.inventories.CustomInventory;
import xeterios.powertag.players.PlayerData;
import xeterios.powertag.players.PlayerDataHandler;
import xeterios.powertag.players.comparers.SortingType;

import java.util.HashMap;

public class ProfileInventory extends CustomInventory
{

    public ProfileInventory(PlayerDataHandler handler, Config config)
    {
        super(handler, config);
        super.FillRemaining(Material.GRAY_STAINED_GLASS_PANE, true);
    }

    public ProfileInventory(PlayerDataHandler handler, OfflinePlayer player, Config config)
    {
        super(handler, 27, Messenger.component(player.getName() + "'s Profile", NamedTextColor.DARK_GRAY), config);
        SetItems(player);
        super.FillRemaining(Material.GRAY_STAINED_GLASS_PANE, true);
    }

    // Use SetItems to set the items in the super class.
    // These items are used to build the inventory with.
    // Region is used to clean up IDE
    protected void SetItems(OfflinePlayer player)
    {
        HashMap<Integer, ItemStack> items = new HashMap<>();
        PlayerData playerData = handler.GetPlayerData(player);

        ItemStack playerHead = PlayerData.getPlayerHead(player, playerData, handler, SortingType.Wins, false);
        items.put(13, playerHead);

        super.items = items;
        BuildInventory();
    }
}
