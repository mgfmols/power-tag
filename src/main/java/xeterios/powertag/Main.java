package xeterios.powertag;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.configuration.Map;
import xeterios.powertag.game.GameManager;
import xeterios.powertag.inventories.CustomInventoryList;
import xeterios.powertag.players.PlayerData;
import xeterios.powertag.players.settings.Settings;

import java.util.ArrayList;
import java.util.logging.Level;

public final class Main extends JavaPlugin implements Listener
{
    @Getter private CustomInventoryList inventoryList;
    @Getter private ArrayList<GameManager> gameManagers;
    private Config config;

    @Override
    public void onLoad()
    {
        // Plugin startup logic
        ConfigurationSerialization.registerClass(PlayerData.class, "PlayerData");
        ConfigurationSerialization.registerClass(Settings.class, "Settings");
        ConfigurationSerialization.registerClass(Map.class, "Map");
    }

    @Override
    public void onEnable()
    {
        // External plugin check
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
        {
            Bukkit.getPluginManager().registerEvents(this, this);
        }
        else
        {
            Messenger.log(Level.SEVERE, "Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        //region Config

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        this.config = new Config(this);
        this.inventoryList = new CustomInventoryList(this.config.getPlayerDataHandler());
        this.gameManagers = new ArrayList<>();
        for (java.util.Map.Entry<String, Map> map : config.getMaps().entrySet())
        {
            GameManager gameManager = new GameManager(this, map.getValue());
            this.gameManagers.add(gameManager);
        }
        Messenger.setConfig(this.config);
        new PAPIExpansion(config).register();

        //endregion

        CommandHandler handler = new CommandHandler(this, config);

        //region Commands

        PluginCommand powertag = this.getCommand("powertag");
        if (powertag == null)
        {
            Messenger.log(Level.SEVERE, "Could not bind powertag command. Please message the plugin developer.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        powertag.setExecutor(handler);
        Messenger.log(Level.INFO, "Command /powertag loaded.");

        PluginCommand tag = this.getCommand("tag");
        if (tag == null)
        {
            Messenger.log(Level.SEVERE, "Could not bind tag command. Please message the plugin developer.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        tag.setExecutor(handler);
        Messenger.log(Level.INFO, "Command /tag loaded.");

        PluginCommand leaderboard = this.getCommand("leaderboard");
        if (leaderboard == null)
        {
            Messenger.log(Level.SEVERE, "Could not bind leaderboard command. Please message the plugin developer.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        leaderboard.setExecutor(handler);
        Messenger.log(Level.INFO, "Command /leaderboard loaded.");

        PluginCommand leave = this.getCommand("leave");
        if (leave == null)
        {
            Messenger.log(Level.SEVERE, "Could not bind leave command. Please message the plugin developer.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        leave.setExecutor(handler);
        Messenger.log(Level.INFO, "Command /leave loaded.");

        PluginCommand profile = this.getCommand("profile");
        if (profile == null)
        {
            Messenger.log(Level.SEVERE, "Could not bind profile command. Please message the plugin developer.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        profile.setExecutor(handler);
        Messenger.log(Level.INFO, "Command /profile loaded.");

        PluginCommand settings = this.getCommand("settings");
        if (settings == null)
        {
            Messenger.log(Level.SEVERE, "Could not bind settings command. Please message the plugin developer.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        settings.setExecutor(handler);
        Messenger.log(Level.INFO, "Command /settings loaded.");

        //endregion
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
        this.config.getPlayerDataHandler().SaveAllPlayerData();
        HandlerList.unregisterAll();
        Messenger.log(Level.INFO, "Unregistering all listeners.");
    }

    public Config config()
    {
        return this.config;
    }

    public Map getMap(String key)
    {
        for (GameManager manager : gameManagers)
        {
            Map map = manager.getMap();
            if (map.getName().equals(key))
            {
                return map;
            }
        }
        return null;
    }

    public GameManager playerHasJoined(Player player)
    {
        for (GameManager manager : gameManagers)
        {
            if (manager.playerHasJoined(player))
            {
                return manager;
            }
        }
        return null;
    }

    public GameManager getGameManager(String key)
    {
        for (GameManager manager : gameManagers)
        {
            Map map = manager.getMap();
            if (map.getName().equals(key))
            {
                return manager;
            }
        }
        return null;
    }

    public GameManager getGameManager(Player input)
    {
        for (GameManager manager : gameManagers)
        {
            for (Player player : manager.getPlayers())
            {
                if (player.getUniqueId().equals(input.getUniqueId()))
                {
                    return manager;
                }
            }
        }
        return null;
    }

    public void createMap(Map map)
    {
        this.config.addMap(map);
        this.gameManagers.add(new GameManager(this, map));
    }

    public void removeMap(Map map)
    {
        this.config.removeMap(map);
        GameManager manager = getGameManager(map.getName());
        assert manager != null;
        manager.stop();
        this.gameManagers.remove(manager);
    }
}
