package xeterios.powertag.inventories;

import org.bukkit.Sound;
import xeterios.powertag.Main;
import xeterios.powertag.Messenger;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.players.PlayerData;
import xeterios.powertag.players.PlayerDataHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;

public class CustomInventory implements Listener
{

    protected PlayerDataHandler handler;

    protected Config config;
    protected HashMap<Integer, ItemStack> items;
    protected final Inventory inventory;

    // Use constructor without parameters to default the parameters
    public CustomInventory(PlayerDataHandler handler, Config config)
    {
        this(handler, 27, Messenger.component("Empty Inventory", NamedTextColor.DARK_GRAY), config);
    }

    public CustomInventory(PlayerDataHandler handler, int size, Component title, Config config)
    {
        this.handler = handler;
        this.items = new HashMap<>();
        this.inventory = Bukkit.createInventory(null, size, title);
        this.config = config;

        // If class is equal to CustomInventory, meaning this will be false if it extends from CustomInventory
        if (this.getClass().equals(CustomInventory.class))
        {
            items = SetItems();
        }
    }

    protected ItemStack GetPlayerHead(PlayerData playerData, OfflinePlayer player)
    {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta playerHeadMeta = playerHead.getItemMeta();
        playerHeadMeta.displayName(Messenger.component(player.getName(), TextColor.color(0x443344)));
        playerHeadMeta.lore(List.of(
                Messenger.component("", NamedTextColor.GRAY)
        ));
        SkullMeta playerHeadSkullMeta = (SkullMeta) playerHeadMeta;
        playerHeadSkullMeta.setOwningPlayer(player);
        playerHead.setItemMeta(playerHeadSkullMeta);
        return playerHead;
    }

    protected void BuildInventory()
    {
        inventory.clear();
        for (Integer integer : items.keySet())
        {
            inventory.setItem(integer, items.get(integer));
        }
    }

    // This method is used by all classes that extend from CustomInventory.
    public void OpenInventory(Player player)
    {
        player.openInventory(this.inventory);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
    }

    protected void FillRemaining(Material material, boolean hideName)
    {
        ItemStack toAdd = new ItemStack(material, 1);
        if (hideName)
        {
            ItemMeta meta = toAdd.getItemMeta();
            meta.displayName(Messenger.empty());
            toAdd.setItemMeta(meta);
        }
        for (int i = 0; i < inventory.getSize(); i++)
        {
            this.items.putIfAbsent(i, toAdd);
        }
        BuildInventory();
    }

    private HashMap<Integer, ItemStack> SetItems()
    {
        HashMap<Integer, ItemStack> items = new HashMap<>();
        ItemStack errorItem = new ItemStack(Material.BARRIER, 1);
        ItemMeta errorMeta = errorItem.getItemMeta();
        errorMeta.displayName(Messenger.component("Error", NamedTextColor.DARK_RED, TextDecoration.BOLD));
        errorMeta.lore(List.of(
                Messenger.component("This inventory was built incorrectly.", NamedTextColor.GRAY, TextDecoration.ITALIC),
                Messenger.component("Please contact an admin as soon as possible.", NamedTextColor.GRAY, TextDecoration.ITALIC)
        ));
        errorItem.setItemMeta(errorMeta);
        items.put(13, errorItem);
        return items;
    }

    @EventHandler
    public void OnClick(InventoryClickEvent e)
    {
        e.setCancelled(true);
    }

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent e)
    {
        if (e.getView().getTopInventory().equals(inventory))
        {
            HandlerList.unregisterAll(this);
        }
    }
}
