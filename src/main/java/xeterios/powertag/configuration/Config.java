package xeterios.powertag.configuration;

import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import xeterios.powertag.Messenger;
import xeterios.powertag.players.PlayerDataHandler;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class Config
{
    private final Plugin plugin;
    @Getter private PlayerDataHandler playerDataHandler;

    @Getter private int minimumPlayers;
    @Getter private float timerMultiplier;
    @Getter private TextComponent pluginPrefix;
    @Getter private TextColor primaryPluginColor;
    @Getter private TextColor secondaryPluginColor;

    @Getter private Location spawn;
    @Getter private final LinkedHashMap<String, Map> maps;
    @Getter private final TreeMap<Double, String> powerupChances;
    @Getter private double powerupChanceMaxRange;

    public Config(Plugin plugin)
    {
        this.plugin = plugin;
        this.playerDataHandler = new PlayerDataHandler();
        this.powerupChances = new TreeMap<>();
        this.maps = new LinkedHashMap<>();
        loadData();
    }

    //region Config

    private void loadData()
    {
        if (this.plugin == null)
        {
            return;
        }
        Messenger.log(Level.INFO, "Loading configurations...");

        minimumPlayers = this.plugin.getConfig().getInt("settings.general.minimumPlayers");
        timerMultiplier = this.plugin.getConfig().getLong("settings.general.timerMultiplier");
        pluginPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(Objects.requireNonNull(this.plugin.getConfig().getString("locale.prefix")));
        primaryPluginColor = LegacyComponentSerializer.legacyAmpersand().deserialize(Objects.requireNonNull(this.plugin.getConfig().getString("locale.primaryColor"))).color();
        secondaryPluginColor = LegacyComponentSerializer.legacyAmpersand().deserialize(Objects.requireNonNull(this.plugin.getConfig().getString("locale.secondaryColor"))).color();

        // Loading powerups
        Messenger.log(Level.INFO, "Loading powerups...");
        loadPowerups();
        Messenger.log(Level.INFO, "Powerups loaded.");

        // Loading maps
        Messenger.log(Level.INFO, "Loading maps...");
        loadMaps();
        Messenger.log(Level.INFO, "Maps loaded.");

        // Loading spawn
        Messenger.log(Level.INFO, "Loading spawn...");
        if (loadSpawn())
        {
            Messenger.log(Level.INFO, "Spawn loaded.");
        }
        else
        {
            Messenger.log(Level.INFO, "Couldn't find spawn.yml, use /tag setspawn to set a new spawn point.");
        }

        Messenger.log(Level.INFO, "Configurations loaded.");
    }

    public void reloadConfig()
    {
        Messenger.log(Level.INFO, "Reloading plugin...");
        this.plugin.reloadConfig();
        this.playerDataHandler = new PlayerDataHandler();
        this.loadData();
        Messenger.log(Level.INFO, "Plugin reloaded.");
    }

    //endregion

    //region Maps

    public void addMap(Map map)
    {
        this.getMaps().put(map.getName(), map);
        this.saveMaps();
    }

    public void removeMap(Map map)
    {
        this.getMaps().remove(map.getName());
        this.removeMap(map.getName());
    }

    public void saveMaps()
    {
        // Iterate through all maps
        for (java.util.Map.Entry<String, Map> entry : this.maps.entrySet())
        {
            // Retrieve map from data
            Map map = entry.getValue();
            // Create new file
            File fl = new File(plugin.getDataFolder() + File.separator + "maps", map.getName() + ".yml");
            FileConfiguration fc = new YamlConfiguration();
            // Set config content to serialized map data
            fc.set(fl.getName().replace(".yml", ""), map);
            try
            {
                // Save file
                fc.save(fl);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void loadMaps()
    {
        File fl = new File(plugin.getDataFolder() + File.separator + "maps");
        File[] files = fl.listFiles();
        if (files != null)
        {
            for (File child : files)
            {
                FileConfiguration config = new YamlConfiguration();
                try
                {
                    config.load(child);
                }
                catch (IOException | InvalidConfigurationException e)
                {
                    e.printStackTrace();
                }
                for (String string : config.getKeys(false))
                {
                    Map map = config.getObject(string, Map.class);
                    assert map != null;
                    this.maps.put(map.getName(), map);
                }
            }
        }
    }

    private void removeMap(String name)
    {
        // Retrieve file
        File fl = new File(plugin.getDataFolder() + File.separator + "maps", name + ".yml");
        // Delete file
        if (fl.delete())
        {
            Messenger.log(Level.FINE, "Deleted map file named " + name + ".yml");
        }
        else
        {
            Messenger.log(Level.SEVERE, "Could not delete " + name + ".yml");
        }
    }

    //endregion

    //region Spawn

    public void setSpawn(Location location)
    {
        this.spawn = location;
        this.saveSpawn(location);
    }

    private void saveSpawn(Location location)
    {
        // Create new file
        File fl = new File(plugin.getDataFolder() + File.separator + "spawn.yml");
        FileConfiguration fc = new YamlConfiguration();
        fc.set("spawn", location);
        try
        {
            // Save file
            fc.save(fl);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private boolean loadSpawn()
    {
        FileConfiguration config = new YamlConfiguration();
        try
        {
            config.load(plugin.getDataFolder() + File.separator + "spawn.yml");
        }
        catch (IOException | InvalidConfigurationException e)
        {
            return false;
        }
        for (String string : config.getKeys(false))
        {
            Location location = config.getObject(string, Location.class);
            assert location != null;
            this.spawn = location;
        }
        return true;
    }

    //endregion

    //region Powerups

    private void loadPowerups()
    {
        ArrayList<java.util.Map.Entry<Double, String>> powerupChances = new ArrayList<>();
        powerupChances.add(new AbstractMap.SimpleEntry<>(plugin.getConfig().getDouble("settings.powerups.boost.chance"), "boost"));
        powerupChances.add(new AbstractMap.SimpleEntry<>(plugin.getConfig().getDouble("settings.powerups.sniper.chance"), "sniper"));
        powerupChances.add(new AbstractMap.SimpleEntry<>(plugin.getConfig().getDouble("settings.powerups.infrasight.chance"), "infrasight"));
        powerupChances.add(new AbstractMap.SimpleEntry<>(plugin.getConfig().getDouble("settings.powerups.chains.chance"), "chains"));
        powerupChances.add(new AbstractMap.SimpleEntry<>(plugin.getConfig().getDouble("settings.powerups.invisibility.chance"), "invisibility"));
        powerupChances.add(new AbstractMap.SimpleEntry<>(plugin.getConfig().getDouble("settings.powerups.firework.chance"), "firework"));
        powerupChances.add(new AbstractMap.SimpleEntry<>(plugin.getConfig().getDouble("settings.powerups.shuffle.chance"), "shuffle"));

        double maxRange = 0;
        for (int i = 0; i < powerupChances.size(); i++)
        {
            double chance = powerupChances.get(i).getKey();
            String name = powerupChances.get(i).getValue();
            maxRange += chance;
            if (i == 0)
            {
                this.powerupChances.put(chance, name);
            }
            else
            {
                this.powerupChances.put(maxRange, name);
            }
        }
        powerupChanceMaxRange = maxRange;
    }

    //endregion
}
