package xeterios.powertag.inventories.inventories;

import xeterios.powertag.Main;
import xeterios.powertag.Messenger;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.inventories.CustomInventory;
import xeterios.powertag.players.PlayerData;
import xeterios.powertag.players.PlayerDataHandler;
import xeterios.powertag.players.settings.*;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SettingsInventory extends CustomInventory
{

    private final Map<SettingsType, Map<SettingsItemType, ItemStack>> settings;

    public SettingsInventory(PlayerDataHandler handler, Config config)
    {
        super(handler, config);
        this.settings = new HashMap<>();
        super.FillRemaining(Material.GRAY_STAINED_GLASS_PANE, true);
    }

    public SettingsInventory(PlayerDataHandler handler, OfflinePlayer player, Config config)
    {
        super(handler, 45, Messenger.component(player.getName() + "'s Settings", NamedTextColor.DARK_GRAY), config);
        this.settings = new HashMap<>();
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

        ItemStack playerHead = GetPlayerHead(playerData, player);

        //region Settings
        ItemStack statisticXPActionBarDescription = GetSettingsItem(playerData.getSettings(), SettingsType.statisticXPActionBar, SettingsItemType.Description);
        ItemStack statisticXPActionBarToggle = GetSettingsItem(playerData.getSettings(), SettingsType.statisticXPActionBar, SettingsItemType.Toggle);

        ItemStack statisticXPSoundDescription = GetSettingsItem(playerData.getSettings(), SettingsType.statisticXPSound, SettingsItemType.Description);
        ItemStack statisticXPSoundToggle = GetSettingsItem(playerData.getSettings(), SettingsType.statisticXPSound, SettingsItemType.Toggle);

        ItemStack statisticLevelTitleDescription = GetSettingsItem(playerData.getSettings(), SettingsType.statisticLevelTitle, SettingsItemType.Description);
        ItemStack statisticLevelTitleToggle = GetSettingsItem(playerData.getSettings(), SettingsType.statisticLevelTitle, SettingsItemType.Toggle);

        ItemStack statisticLevelSoundDescription = GetSettingsItem(playerData.getSettings(), SettingsType.statisticLevelSound, SettingsItemType.Description);
        ItemStack statisticLevelSoundToggle = GetSettingsItem(playerData.getSettings(), SettingsType.statisticLevelSound, SettingsItemType.Toggle);

        this.settings.put(SettingsType.statisticXPActionBar, new HashMap<>()
        {{
            put(SettingsItemType.Description, statisticXPActionBarDescription);
            put(SettingsItemType.Toggle, statisticXPActionBarToggle);
        }});
        this.settings.put(SettingsType.statisticXPSound, new HashMap<>()
        {{
            put(SettingsItemType.Description, statisticXPSoundDescription);
            put(SettingsItemType.Toggle, statisticXPSoundToggle);
        }});
        this.settings.put(SettingsType.statisticLevelTitle, new HashMap<>()
        {{
            put(SettingsItemType.Description, statisticLevelTitleDescription);
            put(SettingsItemType.Toggle, statisticLevelTitleToggle);
        }});
        this.settings.put(SettingsType.statisticLevelSound, new HashMap<>()
        {{
            put(SettingsItemType.Description, statisticLevelSoundDescription);
            put(SettingsItemType.Toggle, statisticLevelSoundToggle);
        }});
        //endregion

        //region Items
        items.put(4, playerHead);

        items.put(19, statisticXPActionBarDescription);
        items.put(28, statisticXPActionBarToggle);

        items.put(20, statisticXPSoundDescription);
        items.put(29, statisticXPSoundToggle);

        items.put(24, statisticLevelTitleDescription);
        items.put(33, statisticLevelTitleToggle);

        items.put(25, statisticLevelSoundDescription);
        items.put(34, statisticLevelSoundToggle);
        //endregion

        super.items = items;
        BuildInventory();
    }

    private ItemStack GetSettingsItem(Settings settings, SettingsType type, SettingsItemType itemType)
    {
        ItemStack item;
        if (itemType.equals(SettingsItemType.Description))
        {
            item = new ItemStack(Material.PAPER, 1);
        }
        else
        {
            if (settings.GetSetting(type))
            {
                item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            }
            else
            {
                item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            }
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setCustomModelData(1);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        NamespacedKey settingsType = new NamespacedKey(Main.getPlugin(Main.class), "settingsType");
        NamespacedKey settingsItemType = new NamespacedKey(Main.getPlugin(Main.class), "settingsItemType");
        itemMeta.getPersistentDataContainer().set(settingsType, PersistentDataType.STRING, type.toString());
        itemMeta.getPersistentDataContainer().set(settingsItemType, PersistentDataType.STRING, itemType.toString());

        if (itemType.equals(SettingsItemType.Description))
        {
            itemMeta.displayName(Messenger.component(SettingsInfo.GetSettingInfo(type, SettingsInfoType.Name), NamedTextColor.WHITE, TextDecoration.BOLD));
            itemMeta.lore(List.of(Messenger.component(SettingsInfo.GetSettingInfo(type, SettingsInfoType.Description), NamedTextColor.GRAY)));
        }
        else
        {
            if (settings.GetSetting(type))
            {
                itemMeta.displayName(Messenger.component("Enabled", NamedTextColor.GREEN, TextDecoration.BOLD));
            }
            else
            {
                itemMeta.displayName(Messenger.component("Disabled", NamedTextColor.RED, TextDecoration.BOLD));
            }

        }
        item.setItemMeta(itemMeta);
        return item;
    }

    // Use @Override public void OnClick(InventoryClickEvent e) to override the event handler InventoryClickEvent
    @EventHandler
    @Override
    public void OnClick(InventoryClickEvent e)
    {
        if (e.getWhoClicked() instanceof Player player)
        {
            if (Objects.equals(e.getClickedInventory(), super.inventory))
            {
                e.setCancelled(true);
                // Define clicked item as item in the set entries
                ItemStack clickedItem = null;
                for (Map.Entry<Integer, ItemStack> entry : super.items.entrySet())
                {
                    if (Objects.equals(e.getCurrentItem(), entry.getValue()))
                    {
                        clickedItem = entry.getValue();
                        break;
                    }
                }
                if (clickedItem == null)
                {
                    return;
                }
                // Look the SettingsType based on clicked item
                SettingsType type = null;
                SettingsItemType itemType = null;
                for (Map.Entry<SettingsType, Map<SettingsItemType, ItemStack>> entry : settings.entrySet())
                {
                    for (Map.Entry<SettingsItemType, ItemStack> subEntry : entry.getValue().entrySet())
                    {
                        if (clickedItem.equals(subEntry.getValue()))
                        {
                            type = entry.getKey();
                            itemType = subEntry.getKey();
                            break;
                        }
                    }
                }
                if (type == null || itemType == null)
                {
                    return;
                }
                // Set setting to opposite of current value
                if (itemType.equals(SettingsItemType.Toggle))
                {
                    PlayerData playerData = handler.GetPlayerData(player);
                    Settings settings = playerData.getSettings();
                    boolean value = !settings.GetSetting(type);
                    if (!settings.EditSetting(type, value))
                    {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 100f, 0);
                        return;
                    }
                    if (value)
                    {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 100f, 2);
                    }
                    else
                    {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 100f, 1);
                    }
                    handler.SavePlayerData(playerData);
                    // Update Inventory
                    ItemStack newItem = GetSettingsItem(playerData.getSettings(), type, itemType);
                    Map<SettingsItemType, ItemStack> newItemMap = this.settings.get(type);
                    newItemMap.put(itemType, newItem);
                    this.settings.put(type, newItemMap);
                    super.items.put(e.getSlot(), newItem);
                    super.BuildInventory();
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e)
    {
        CommandHandler.sendMessage(config, e.getPlayer(), CommandMessageReason.SETTINGS_CLOSED);
    }
}
