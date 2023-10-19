package xeterios.powertag.inventories;

import xeterios.powertag.configuration.Config;
import xeterios.powertag.inventories.inventories.*;
import org.bukkit.OfflinePlayer;
import xeterios.powertag.players.PlayerDataHandler;

import java.util.LinkedHashMap;

public record CustomInventoryList(PlayerDataHandler handler)
{
    public LinkedHashMap<String, CustomInventory> getMap(OfflinePlayer player, Config config)
    {
        LinkedHashMap<String, CustomInventory> map = new LinkedHashMap<>();
        if (player == null)
        {
            map.put("base", new CustomInventory(handler, config));
            map.put("test", new TestInventory(handler, config));
            map.put("profile", new ProfileInventory(handler, config));
            //map.put("settings", new SettingsInventory(handler, config));
            map.put("leaderboard", new LeaderboardInventory(handler, config));
            map.put("information", new InformationInventory(handler, config));
        }
        else
        {
            map.put("base", new CustomInventory(handler, config));
            map.put("test", new TestInventory(handler, config));
            map.put("profile", new ProfileInventory(handler, player, config));
            //map.put("settings", new SettingsInventory(handler, player, config));
            map.put("leaderboard", new LeaderboardInventory(handler, player, config));
            map.put("information", new InformationInventory(handler, config));
        }
        return map;
    }

    public CustomInventory getCustomInventory(String key, OfflinePlayer player, Config config)
    {
        LinkedHashMap<String, CustomInventory> map = getMap(player, config);
        return map.get(key);
    }
}
