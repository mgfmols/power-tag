package xeterios.powertag.commands.commands;

import xeterios.powertag.Main;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public record Reload(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.reload";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " reload";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Reload the Power Tag configurations";
    }

    @Override
    public ArrayList<ArrayList<String>> GetCommandTabComplete(CommandSender sender, Config config)
    {
        return new ArrayList<>();
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        config.reloadConfig();
        CommandHandler.sendMessage(config, sender, CommandMessageReason.RELOAD_SUCCESSFUL);
    }
}
