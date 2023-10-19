package xeterios.powertag.commands.commands;

import xeterios.powertag.Main;
import xeterios.powertag.commands.CmdList;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public record PowerTag(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.tag";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " <subcommand> <subcommand args>";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Used to execute a Power Tag subcommand";
    }

    @Override
    public ArrayList<ArrayList<String>> GetCommandTabComplete(CommandSender sender, Config config)
    {
        return new ArrayList<>()
        {{
            add(new ArrayList<>()
            {{
                for (Map.Entry<String, Cmd> entry : CmdList.commandList(args).entrySet())
                {
                    String cmd = entry.getKey();
                    if (cmd.equals("powertag") || cmd.equals("tag") || !sender.hasPermission(entry.getValue().GetPermissionNode()))
                    {
                        continue;
                    }
                    add(cmd);
                    add(cmd);
                }
            }});
        }};
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        if (args.length <= 0)
        {
            CommandHandler.sendMessage(config, this, label, sender, CommandMessageReason.MISSING_ARGUMENTS);
            return;
        }
        String subLabel = args[0];
        String[] args = Arrays.copyOfRange(this.args, 1, this.args.length);
        Cmd subCmd = CmdList.getCommand(subLabel, args);
        if (subCmd == null)
        {
            CommandHandler.sendMessage(config, this, label, sender, CommandMessageReason.SUBCOMMAND_NOT_FOUND);
            return;
        }
        if (!sender.hasPermission(subCmd.GetPermissionNode()))
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.NO_PERMISSION);
            return;
        }
        subCmd.Execute(sender, label, main, config);
    }
}
