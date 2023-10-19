package xeterios.powertag.inventories.inventories;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xeterios.powertag.Messenger;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.inventories.CustomInventory;
import xeterios.powertag.players.PlayerData;
import xeterios.powertag.players.PlayerDataHandler;
import xeterios.powertag.players.comparers.SortingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LeaderboardInventory extends CustomInventory
{

    private SortingType sortingType;
    private int page;

    public LeaderboardInventory(PlayerDataHandler handler, Config config)
    {
        super(handler, config);
        this.page = 0;
        this.sortingType = SortingType.Wins;
    }

    public LeaderboardInventory(PlayerDataHandler handler, OfflinePlayer player, Config config)
    {
        super(handler, 54, Messenger.component("Leaderboard", NamedTextColor.DARK_GRAY), config);
        this.page = 0;
        this.sortingType = SortingType.Wins;
        SetItems(player);
    }

    // Use SetItems to set the items in the super class.
    // These items are used to build the inventory with.
    // Region is used to clean up IDE
    protected void SetItems(OfflinePlayer player)
    {
        HashMap<Integer, ItemStack> items = new HashMap<>();

        // Loading data based on sortingType
        ArrayList<PlayerData> loadedData = new ArrayList<>();
        switch (sortingType)
        {
            case UUID -> loadedData = handler.GetLeaderboard(SortingType.UUID);
            case Points -> loadedData = handler.GetLeaderboard(SortingType.Points);
            case Wins -> loadedData = handler.GetLeaderboard(SortingType.Wins);
            case Winstreak -> loadedData = handler.GetLeaderboard(SortingType.Winstreak);
        }

        // Fill player database for this page
        ArrayList<PlayerData> playerDataList = new ArrayList<>();
        for (int i = page * 36; i < (page + 1) * 36; i++)
        {
            if (i < handler.getPlayerData().size())
            {
                playerDataList.add(loadedData.get(i));
            }
        }

        // Adding player heads
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null)
        {
            return;
        }
        boolean addUuid = onlinePlayer.hasPermission("powertag.leaderboard.sortuuid");

        for (int i = 0; i < playerDataList.size(); i++)
        {
            PlayerData data = playerDataList.get(i);
            OfflinePlayer target = handler.GetOfflinePlayer(data.getUuid());
            ItemStack playerHead = PlayerData.getPlayerHead(target, data, handler, sortingType, addUuid);
            items.put(i, playerHead);
        }

        // Set glass
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.displayName(Messenger.empty());
        glass.setItemMeta(glassMeta);
        for (int i = 36; i < 45; i++)
        {
            items.put(i, glass);
        }

        // Set sorter item
        ItemStack sorter = new ItemStack(Material.SUNFLOWER, 1);
        ItemMeta sorterMeta = sorter.getItemMeta();
        TextColor color = null;
        switch (sortingType)
        {
            case UUID -> color = NamedTextColor.DARK_GRAY;
            case Points -> color = NamedTextColor.AQUA;
            case Wins -> color = NamedTextColor.GOLD;
            case Winstreak -> color = NamedTextColor.GREEN;
        }
        TextComponent sorterName = Messenger.component("Sorting by: ", NamedTextColor.WHITE)
                .append(Messenger.component(sortingType.toString(), color));
        sorterMeta.displayName(sorterName);

        List<Component> lore = new ArrayList<>();
        lore.add(Messenger.component("Click to change the sorting method", NamedTextColor.GRAY));
        sorterMeta.lore(lore);
        sorter.setItemMeta(sorterMeta);
        items.put(49, sorter);

        // Set previous page item
        if (page > 0)
        {
            ItemStack previousPage = new ItemStack(Material.PAPER, 1);
            ItemMeta previousPageMeta = previousPage.getItemMeta();
            previousPageMeta.displayName(Messenger.component("Previous Page", NamedTextColor.YELLOW, TextDecoration.BOLD));
            previousPage.setItemMeta(previousPageMeta);
            items.put(48, previousPage);
        }

        // Set next page item
        if ((page + 1) * 36 < handler.getPlayerData().size())
        {
            ItemStack nextPage = new ItemStack(Material.PAPER, 1);
            ItemMeta nextPageMeta = nextPage.getItemMeta();
            nextPageMeta.displayName(Messenger.component("Next Page", NamedTextColor.YELLOW, TextDecoration.BOLD));
            nextPage.setItemMeta(nextPageMeta);
            items.put(50, nextPage);
        }

        super.items = items;
        super.BuildInventory();
    }

    // Use @Override public void OnClick(InventoryClickEvent e) to override the event handler InventoryClickEvent
    @EventHandler
    @Override
    public void OnClick(InventoryClickEvent e)
    {
        if (e.getWhoClicked() instanceof Player player)
        {
            ItemStack itemStack = e.getCurrentItem();
            if (itemStack == null)
            {
                e.setCancelled(true);
                return;
            }
            ItemMeta itemMeta = itemStack.getItemMeta();
            switch (itemStack.getType())
            {
                case PAPER -> {
                    if (!itemMeta.hasDisplayName())
                    {
                        e.setCancelled(true);
                        return;
                    }
                    String itemName = itemMeta.getDisplayName();
                    if (itemName.contains("Next"))
                    {
                        page++;
                        SetItems(player);
                    }
                    else if (itemName.contains("Previous"))
                    {
                        if (page > 0)
                        {
                            page--;
                            SetItems(player);
                        }
                    }
                }
                case SUNFLOWER -> {
                    SortingType[] types = SortingType.values();
                    if (sortingType.ordinal() + 1 == types.length)
                    {
                        if (player.hasPermission("powertag.leaderboard.sortuuid"))
                        {
                            sortingType = types[0];
                        }
                        else
                        {
                            sortingType = types[1];
                        }
                    }
                    else
                    {
                        sortingType = types[sortingType.ordinal() + 1];
                    }
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 2);
                    SetItems(player);
                }
            }
        }
        e.setCancelled(true);
    }
}
