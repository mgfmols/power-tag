package xeterios.powertag.configuration;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

@SerializableAs("Map")
public class Map implements ConfigurationSerializable
{

    @Getter private final ArrayList<Player> joinedPlayers;
    @Getter @Setter private String name;
    @Getter @Setter private Location spawn;
    @Getter @Setter private Location pos1;
    @Getter @Setter private Location pos2;

    public Map(String name)
    {
        this.name = name;
        this.joinedPlayers = new ArrayList<>();
    }

    public Map(String name, Location spawn, Location min, Location max)
    {
        this.name = name;
        this.spawn = spawn;
        this.pos1 = min;
        this.pos2 = max;
        this.joinedPlayers = new ArrayList<>();
    }

    public void AddPlayer(Player player)
    {
        joinedPlayers.add(player);
    }

    public void RemovePlayer(Player player)
    {
        joinedPlayers.remove(player);
    }

    public Location getRandomLocation()
    {
        int x = (int) (pos1.getX() + (int) (Math.random() * ((pos2.getX() - pos1.getX()) + 1)));
        int y = (int) (pos1.getY() + (int) (Math.random() * ((pos2.getY() - pos1.getY()) + 1)));
        int z = (int) (pos1.getZ() + (int) (Math.random() * ((pos2.getZ() - pos1.getZ()) + 1)));

        Location toReturn = new Location(spawn.getWorld(), x, y, z);
        World world = toReturn.getWorld();
        boolean valid = false;
        int attempt = 1;
        while (!valid)
        {
            if (world.getBlockAt((int) toReturn.getX(), (int) toReturn.getY() - 1, (int) toReturn.getZ()).isEmpty())
            {
                toReturn.setY(toReturn.getY() - 1);
            }
            else if (!world.getBlockAt((int) toReturn.getX(), (int) toReturn.getY(), (int) toReturn.getZ()).isEmpty())
            {
                toReturn.setY(toReturn.getY() + 1);
            }
            if (!world.getBlockAt((int) toReturn.getX(), (int) toReturn.getY() - 1, (int) toReturn.getZ()).isEmpty() && world.getBlockAt((int) toReturn.getX(), (int) (toReturn.getY()), (int) toReturn.getZ()).isEmpty())
            {
                valid = true;
            }
            attempt++;
            if (attempt == 15 || world.getBlockAt(toReturn).getType().equals(Material.BARRIER) || toReturn.getY() > 255 || toReturn.getY() < 0)
            {
                int x2 = (int) (pos1.getX() + (int) (Math.random() * ((pos2.getX() - pos1.getX()) + 1)));
                int y2 = (int) (pos1.getY() + (int) (Math.random() * ((pos2.getY() - pos1.getY()) + 1)));
                int z2 = (int) (pos1.getZ() + (int) (Math.random() * ((pos2.getZ() - pos1.getZ()) + 1)));
                toReturn = new Location(spawn.getWorld(), x2, y2, z2);
                attempt = 1;
            }
        }
        toReturn.add(0.5, 0.5, 0.5);
        return toReturn;
    }

    @Override
    public java.util.@NotNull Map<String, Object> serialize()
    {
        java.util.Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("spawn", spawn);
        result.put("min", pos1);
        result.put("max", pos2);
        return result;
    }

    public static Map deserialize(java.util.Map<String, Object> args)
    {
        String name = "";
        Location spawn = new Location(null, 0, 0, 0);
        Location min = new Location(null, 0, 0, 0);
        Location max = new Location(null, 0, 0, 0);
        if (args.containsKey("name"))
        {
            name = (String) args.get("name");
        }
        if (args.containsKey("spawn"))
        {
            spawn = (Location) args.get("spawn");
        }
        if (args.containsKey("min"))
        {
            min = (Location) args.get("min");
        }
        if (args.containsKey("max"))
        {
            max = (Location) args.get("max");
        }
        return new Map(name, spawn, min, max);
    }
}
