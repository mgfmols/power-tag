package xeterios.powertag.commands.commands;

import xeterios.powertag.Main;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xeterios.powertag.inventories.CustomInventory;
import xeterios.powertag.inventories.CustomInventoryList;

import java.util.ArrayList;
import java.util.Map;

public record OpenInv(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.openinv";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " openinv <inventory> [player] [target]";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Open a custom inventory\n  <inventory>: The inventory to open\n  [player]: The player whose information is shown (optional)\n  [target]: The target who to open the inventory for (optional)";
    }

    @Override
    public ArrayList<ArrayList<String>> GetCommandTabComplete(CommandSender sender, Config config, CustomInventoryList inventoryList)
    {
        return new ArrayList<>()
        {{
            add(new ArrayList<>()
            {{
                for (Map.Entry<String, CustomInventory> inventories : inventoryList.getMap(null, config).entrySet())
                {
                    add(inventories.getKey());
                }
            }});
            add(new ArrayList<>()
            {{
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    add(player.getName());
                }
            }});
            add(new ArrayList<>()
            {{
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    add(player.getName());
                }
            }});
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
        // Player name is given as arg
        OfflinePlayer openAs = player;
        if (args.length > 1)
        {
            openAs = Bukkit.getOfflinePlayerIfCached(args[1]);
            if (openAs == null)
            {
                CommandHandler.sendMessage(config, sender, CommandMessageReason.PLAYER_DOESNT_EXIST, args[1]);
                return;
            }
        }
        CustomInventory inventory = main.getInventoryList().getCustomInventory(args[0], openAs, config);
        if (inventory == null)
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.INVENTORY_DOESNT_EXIST, args[0]);
            return;
        }
        Player target = player;
        if (args.length > 2)
        {
            target = Bukkit.getPlayer(args[2]);
            if (target == null)
            {
                CommandHandler.sendMessage(config, sender, CommandMessageReason.PLAYER_DOESNT_EXIST, args[2]);
                return;
            }
        }
        inventory.OpenInventory(target);
    }
}
