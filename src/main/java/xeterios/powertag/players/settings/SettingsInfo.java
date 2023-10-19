package xeterios.powertag.players.settings;

import java.util.HashMap;

public class SettingsInfo
{

    private static final HashMap<SettingsType, HashMap<SettingsInfoType, String>> map = new HashMap<>()
    {{
        put(SettingsType.statisticXPActionBar, new HashMap<>()
        {{
            put(SettingsInfoType.Name, "Stat XP Action Bar");
            put(SettingsInfoType.Description, "Show your Stat XP gain on the action bar.");
        }});
        put(SettingsType.statisticXPSound, new HashMap<>()
        {{
            put(SettingsInfoType.Name, "Stat XP Sound");
            put(SettingsInfoType.Description, "Play a sound when you gain Stat XP");
        }});
        put(SettingsType.statisticLevelTitle, new HashMap<>()
        {{
            put(SettingsInfoType.Name, "Stat Level Up Title");
            put(SettingsInfoType.Description, "Show a Stat Level up in your screen");
        }});
        put(SettingsType.statisticLevelSound, new HashMap<>()
        {{
            put(SettingsInfoType.Name, "Stat Level Up Sound");
            put(SettingsInfoType.Description, "Play a sound when you gain a Stat Level");
        }});
    }};

    public SettingsInfo()
    {

    }

    public static String GetSettingInfo(SettingsType type, SettingsInfoType infoType)
    {
        return map.get(type).get(infoType);
    }
}
