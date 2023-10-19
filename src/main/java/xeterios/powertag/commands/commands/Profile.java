package xeterios.powertag.commands.commands;

import xeterios.powertag.Main;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.inventories.CustomInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public record Profile(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.profile";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " profile";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Shows your own profile";
    }

    @Override
    public ArrayList<ArrayList<String>> GetCommandTabComplete(CommandSender sender, Config config)
    {
        return new ArrayList<>();
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        if (!(sender instanceof Player player))
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SENDER_MUST_BE_PLAYER);
            return;
        }
        CustomInventory inventory = main.getInventoryList().getCustomInventory("profile", player, config);
        inventory.OpenInventory(player);
        CommandHandler.sendMessage(config, sender, CommandMessageReason.PROFILE_OPENING);
    }
}
