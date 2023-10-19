package xeterios.powertag.commands.commands;

import xeterios.powertag.Main;
import xeterios.powertag.Messenger;
import xeterios.powertag.commands.CmdList;
import xeterios.powertag.configuration.Config;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.Map;

public record Help(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.help";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " help";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Shows all the Power Tag commands";
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        TextComponent message = Messenger.component("┌──────────── ", NamedTextColor.DARK_GRAY).append(Messenger.component("Power Tag", config.getPrimaryPluginColor(), TextDecoration.BOLD)).append(Messenger.nextLine());
        for (Map.Entry<String, Cmd> entry : CmdList.commandList(args).entrySet())
        {
            Cmd cmd = entry.getValue();
            if (cmd instanceof PowerTag)
            {
                continue;
            }
            if (sender.hasPermission(cmd.GetPermissionNode()))
            {
                message = message.append(Messenger.component("│ ", NamedTextColor.DARK_GRAY).append(Messenger.component("/" + label + " " + cmd.getClass().getSimpleName().toLowerCase() + " ", config.getPrimaryPluginColor())));
                TextComponent hover = Messenger.component("[Info]", NamedTextColor.GRAY)
                        .hoverEvent(HoverEvent.showText(Messenger.component(cmd.GetCommandDescription(), NamedTextColor.GRAY).append(Messenger.nextLine()).append(Messenger.component(cmd.GetCommandFormat(label), NamedTextColor.GRAY))))
                        .append(Messenger.nextLine());
                message = message.append(hover);
            }
        }
        message = message.append(Messenger.component("└──────────── ", NamedTextColor.DARK_GRAY).append(Messenger.component("Power Tag", config.getPrimaryPluginColor(), TextDecoration.BOLD)));
        sender.sendMessage(message);
    }
}
