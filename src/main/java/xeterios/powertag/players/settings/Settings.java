package xeterios.powertag.players.settings;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Settings")
public class Settings implements ConfigurationSerializable
{

    @Getter
    private final Map<SettingsType, Boolean> map;

    public Settings()
    {
        this.map = new HashMap<>()
        {{
            put(SettingsType.statisticXPActionBar, true);
            put(SettingsType.statisticXPSound, true);
            put(SettingsType.statisticLevelTitle, true);
            put(SettingsType.statisticLevelSound, true);
        }};
    }

    public Settings(Map<SettingsType, Boolean> map)
    {
        this.map = map;
    }

    public boolean EditSetting(SettingsType type, Boolean value)
    {
        try
        {
            this.map.put(type, value);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public boolean GetSetting(SettingsType type)
    {
        return map.get(type);
    }

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        Map<String, Object> result = new HashMap<>();
        result.put(SettingsType.statisticXPActionBar.toString(), this.map.get(SettingsType.statisticXPActionBar));
        result.put(SettingsType.statisticXPSound.toString(), this.map.get(SettingsType.statisticXPSound));
        result.put(SettingsType.statisticLevelTitle.toString(), this.map.get(SettingsType.statisticLevelTitle));
        result.put(SettingsType.statisticLevelSound.toString(), this.map.get(SettingsType.statisticLevelSound));
        return result;
    }

    public static Settings deserialize(Map<String, Object> args)
    {
        HashMap<SettingsType, Boolean> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : args.entrySet())
        {
            if (entry.getKey().equals("=="))
            {
                continue;
            }
            map.put(SettingsType.valueOf(entry.getKey()), Boolean.valueOf(String.valueOf(entry.getValue())));
        }
        return new Settings(map);
    }
}
