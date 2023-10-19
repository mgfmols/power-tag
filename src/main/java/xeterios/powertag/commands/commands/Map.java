package xeterios.powertag.commands.commands;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xeterios.powertag.Main;
import xeterios.powertag.Messenger;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record Map(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.map";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " map <create/remove/editregion/setspawn/spawn> <name>";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Create a new map for Power Tag";
    }

    @Override
    public ArrayList<ArrayList<String>> GetCommandTabComplete(CommandSender sender, Config config)
    {
        return new ArrayList<>()
        {{
            add(new ArrayList<>()
            {{
                for (java.util.Map.Entry<String, MapSubcommand> entry : subcommandList(null, null, null, null, null).entrySet())
                {
                    add(entry.getKey());
                }
            }});
            add(new ArrayList<>()
            {{
                for (java.util.Map.Entry<String, xeterios.powertag.configuration.Map> entry : config.getMaps().entrySet())
                {
                    add(entry.getKey());
                }
            }});
        }};
    }

    public static HashMap<String, MapSubcommand> subcommandList(Main main, Config config, xeterios.powertag.configuration.Map map, Player player, String[] args)
    {
        return new HashMap<>()
        {{
            put("create", new MapCreate(main, config, map, player, args));
            put("remove", new MapRemove(main, config, map, player, args));
            put("editregion", new MapSetRegion(main, config, map, player, args));
            put("setspawn", new MapSetSpawn(main, config, map, player, args));
            put("spawn", new MapSpawn(main, config, map, player, args));
        }};
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        if (!(sender instanceof Player player))
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SENDER_MUST_BE_PLAYER);
            return;
        }

        if (args.length <= 0)
        {
            CommandHandler.sendMessage(config, this, label, sender, CommandMessageReason.MISSING_ARGUMENTS);
            return;
        }

        MapSubcommand subcommand = subcommandList(main, config, null, player, args).get(args[0]);
        if (subcommand == null)
        {
            CommandHandler.sendMessage(config, this, label, sender, CommandMessageReason.SUBCOMMAND_NOT_FOUND);
            return;
        }

        if (args.length <= 1)
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.MAP_NAME_MISSING);
            return;
        }

        // Check map requirements
        xeterios.powertag.configuration.Map map = main.getMap(args[1]);
        if (subcommand instanceof MapCreate)
        {
            if (map != null)
            {
                CommandHandler.sendMessage(config, sender, CommandMessageReason.MAP_ALREADY_EXISTS, args[1]);
                return;
            }
        }
        else
        {
            if (map == null)
            {
                CommandHandler.sendMessage(config, sender, CommandMessageReason.MAP_DOESNT_EXIST, args[1]);
                return;
            }
            subcommand.SetMap(map);
        }

        subcommand.Execute();
    }
}

abstract class MapSubcommand
{

    protected Main main;
    protected Config config;
    protected xeterios.powertag.configuration.Map map;
    protected Player player;
    protected String[] args;

    public MapSubcommand(Main main, Config config, xeterios.powertag.configuration.Map map, Player player, String[] args)
    {
        this.main = main;
        this.config = config;
        this.map = map;
        this.player = player;
        this.args = args;
    }

    public void SetMap(xeterios.powertag.configuration.Map map)
    {
        this.map = map;
    }

    abstract void Execute();
}

class MapCreate extends MapSubcommand
{

    public MapCreate(Main main, Config config, xeterios.powertag.configuration.Map map, Player player, String[] args)
    {
        super(main, config, map, player, args);
    }

    public void Execute()
    {
        xeterios.powertag.configuration.Map map = new xeterios.powertag.configuration.Map(args[1]);
        main.createMap(map);
        CommandHandler.sendMessage(config, player, CommandMessageReason.MAP_CREATED, args[1]);
    }
}

class MapRemove extends MapSubcommand
{

    public MapRemove(Main main, Config config, xeterios.powertag.configuration.Map map, Player player, String[] args)
    {
        super(main, config, map, player, args);
    }

    public void Execute()
    {
        main.removeMap(map);
        CommandHandler.sendMessage(config, player, CommandMessageReason.MAP_REMOVED, args[1]);
    }
}

class MapSetRegion extends MapSubcommand implements Listener
{

    private Location pos1;
    private Location pos2;

    public MapSetRegion(Main main, Config config, xeterios.powertag.configuration.Map map, Player player, String[] args)
    {
        super(main, config, map, player, args);
        this.pos1 = null;
        this.pos2 = null;
    }

    public void Execute()
    {
        player.getInventory().addItem(regionEditor());
        CommandHandler.sendMessage(config, player, CommandMessageReason.MAP_REGION_POS1_SET_INFO, args[1]);
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    private ItemStack regionEditor()
    {
        ItemStack item = new ItemStack(Material.BLAZE_ROD, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Messenger.component("Region Editor", TextColor.color(this.config.getPrimaryPluginColor())));
        itemMeta.lore(List.of(
                Messenger.component("This tool is used to edit map regions.", NamedTextColor.GRAY),
                Messenger.component("Left-Click", TextColor.color(this.config.getPrimaryPluginColor())).append(Messenger.component(" to set " + map.getName() + "'s pos1", NamedTextColor.GRAY)),
                Messenger.component("Right-Click", TextColor.color(this.config.getPrimaryPluginColor())).append(Messenger.component(" to set " + map.getName() + "'s pos2", NamedTextColor.GRAY))
        ));
        itemMeta.addEnchant(Enchantment.WATER_WORKER, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        return item;
    }

    @EventHandler
    public void OnPlayerClick(PlayerInteractEvent e)
    {
        if (e.getItem() == null)
        {
            return;
        }
        if (!e.getItem().equals(regionEditor()))
        {
            return;
        }
        if (e.getClickedBlock() == null)
        {
            return;
        }
        e.setCancelled(true);
        Location location = e.getClickedBlock().getLocation();
        if (e.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            this.pos1 = location;
            CommandHandler.sendMessage(config, player, CommandMessageReason.MAP_REGION_POS1_SET, args[1], Messenger.getLocationCoordinates(location));
            CommandHandler.sendMessage(config, player, CommandMessageReason.MAP_REGION_POS2_SET_INFO, args[1]);
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            this.pos2 = location;
            CommandHandler.sendMessage(config, player, CommandMessageReason.MAP_REGION_POS2_SET, args[1], Messenger.getLocationCoordinates(location));
        }
        if (this.pos1 == null || this.pos2 == null)
        {
            return;
        }
        this.map.setPos1(pos1);
        this.map.setPos2(pos2);
        config.saveMaps();
        player.getInventory().remove(player.getInventory().getItemInMainHand());
        CommandHandler.sendMessage(config, player, CommandMessageReason.MAP_REGION_SET, args[1]);
        HandlerList.unregisterAll(this);
    }
}

class MapSetSpawn extends MapSubcommand
{

    public MapSetSpawn(Main main, Config config, xeterios.powertag.configuration.Map map, Player player, String[] args)
    {
        super(main, config, map, player, args);
    }

    public void Execute()
    {
        map.setSpawn(player.getLocation());
        config.saveMaps();
        CommandHandler.sendMessage(config, player, CommandMessageReason.MAP_SPAWN_SET, args[1], Messenger.getLocationCoordinates(player.getLocation()));
    }
}

class MapSpawn extends MapSubcommand
{

    public MapSpawn(Main main, Config config, xeterios.powertag.configuration.Map map, Player player, String[] args)
    {
        super(main, config, map, player, args);
    }

    public void Execute()
    {
        player.teleport(map.getSpawn());
        CommandHandler.sendMessage(config, player, CommandMessageReason.MAP_TELEPORT_TO_SPAWN, args[1]);
    }
}