package xeterios.powertag.commands.commands;

import xeterios.powertag.Main;
import xeterios.powertag.configuration.Config;
import org.bukkit.command.CommandSender;
import xeterios.powertag.inventories.CustomInventoryList;

import java.util.ArrayList;

public interface Cmd
{

    String GetPermissionNode();

    String GetCommandFormat(String label);

    String GetCommandDescription();

    default ArrayList<ArrayList<String>> getCommandTabComplete(CommandSender sender, Config config, CustomInventoryList inventoryList)
    {
        ArrayList<ArrayList<String>> toReturn = GetCommandTabComplete(config);
        if (toReturn.size() > 0)
        {
            return toReturn;
        }

        toReturn = GetCommandTabComplete(sender, config);
        if (toReturn.size() > 0)
        {
            return toReturn;
        }

        toReturn = GetCommandTabComplete(sender, config, inventoryList);
        if (toReturn.size() > 0)
        {
            return toReturn;
        }
        return new ArrayList<>();
    }

    default ArrayList<ArrayList<String>> GetCommandTabComplete(Config config)
    {
        return new ArrayList<>();
    }

    default ArrayList<ArrayList<String>> GetCommandTabComplete(CommandSender sender, Config config)
    {
        return new ArrayList<>();
    }

    default ArrayList<ArrayList<String>> GetCommandTabComplete(CommandSender sender, Config config, CustomInventoryList inventoryList)
    {
        return new ArrayList<>();
    }

    void Execute(CommandSender sender, String label, Main main, Config config);

}
