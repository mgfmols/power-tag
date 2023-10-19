package xeterios.powertag.commands.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xeterios.powertag.Main;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.inventories.CustomInventory;

public record Leaderboard(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.leaderboard";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " leaderboard";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Shows the Power Tag leaderboard";
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        if (!(sender instanceof Player player))
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SENDER_MUST_BE_PLAYER);
            return;
        }
        CustomInventory inventory = main.getInventoryList().getCustomInventory("leaderboard", player, config);
        inventory.OpenInventory(player);
        CommandHandler.sendMessage(config, sender, CommandMessageReason.LEADERBOARD_OPENING);
    }
}
