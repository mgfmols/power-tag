package xeterios.powertag.game.powerups;

import xeterios.powertag.configuration.Config;
import xeterios.powertag.game.powerups.powerups.*;

import java.util.LinkedHashMap;
import java.util.Random;
import java.util.TreeMap;

public class PowerupList
{
    public static LinkedHashMap<String, Powerup> powerupList()
    {
        return new LinkedHashMap<>()
        {{
            put("boost", new Boost());
            put("sniper", new Sniper());
            put("infrasight", new InfraSight());
            put("chains", new Chains());
            put("invisibility", new Invisibility());
            put("firework", new Firework());
            put("shuffle", new Shuffle());
        }};
    }

    public static Powerup getPowerup(String powerupName)
    {
        return powerupList().get(powerupName);
    }

    public static Powerup getRandomPowerup(Config config)
    {
        // Getting necessary parameters
        TreeMap<Double, String> powerupChances = config.getPowerupChances();
        double powerupChancesMaxRange = config.getPowerupChanceMaxRange();

        // Rolling dice
        Random rnd = new Random();
        double itemToSpawnNext = rnd.nextDouble() * powerupChancesMaxRange;

        // Getting the powerup
        String powerupName = powerupChances.get(powerupChances.ceilingKey(itemToSpawnNext));
        if (powerupName == null)
        {
            powerupName = powerupChances.get(powerupChances.floorKey(itemToSpawnNext));
        }
        return PowerupList.getPowerup(powerupName);
    }
}
