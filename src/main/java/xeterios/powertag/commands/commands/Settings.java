package xeterios.powertag.commands.commands;

import net.kyori.adventure.text.format.NamedTextColor;
import xeterios.powertag.Main;
import xeterios.powertag.Messenger;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.inventories.CustomInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public record Settings(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.settings";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " settings";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Edit your personal settings";
    }

    @Override
    public ArrayList<ArrayList<String>> GetCommandTabComplete(CommandSender sender, Config config)
    {
        return new ArrayList<>();
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        Messenger.message(sender, Messenger.component("Coming soon...", NamedTextColor.RED));
        /*if (!(sender instanceof Player player))
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SENDER_MUST_BE_PLAYER);
            return;
        }
        CustomInventory inventory = main.getInventoryList().getCustomInventory("settings", player, config);
        inventory.OpenInventory(player);
        CommandHandler.sendMessage(config, sender, CommandMessageReason.SETTINGS_OPENING);*/
    }
}
