package xeterios.powertag.commands.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xeterios.powertag.Main;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.inventories.CustomInventory;

public record Information(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.information";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " information";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Shows the Power Tag information";
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        if (!(sender instanceof Player player))
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SENDER_MUST_BE_PLAYER);
            return;
        }
        CustomInventory inventory = main.getInventoryList().getCustomInventory("information", player, config);
        inventory.OpenInventory(player);
    }
}
