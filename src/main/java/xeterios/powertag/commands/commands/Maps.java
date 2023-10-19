package xeterios.powertag.commands.commands;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xeterios.powertag.Main;
import xeterios.powertag.Messenger;
import xeterios.powertag.configuration.Config;

import java.util.Map;

public record Maps(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.maps";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " maps";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Shows all the Power Tag maps";
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        TextComponent message = Messenger.empty();
        message = message.append(Messenger.component("┌──────────── ", NamedTextColor.DARK_GRAY).append(Messenger.component("Maps", config.getPrimaryPluginColor(), TextDecoration.BOLD))).append(Messenger.nextLine());
        message = message.append(Messenger.component("│", NamedTextColor.DARK_GRAY)).append(Messenger.nextLine());
        if (config.getMaps().size() == 0)
        {
            message = message.append(Messenger.component("│ ", NamedTextColor.DARK_GRAY)).append(Messenger.component("There are no maps!", config.getPrimaryPluginColor())).append(Messenger.nextLine());
        }
        else
        {
            for (Map.Entry<String, xeterios.powertag.configuration.Map> entry : config.getMaps().entrySet())
            {
                xeterios.powertag.configuration.Map map = entry.getValue();
                message = message
                        .append(Messenger.component("│ ", NamedTextColor.DARK_GRAY))
                        .append(Messenger.component(map.getName(), NamedTextColor.WHITE))
                        .append(Messenger.space());

                TextComponent hover = Messenger.component("[Info]", NamedTextColor.GRAY).hoverEvent(HoverEvent.showText(createHoverInfo(config, map, sender)));
                if (sender instanceof Player && map.getSpawn() != null)
                {
                    hover = hover.clickEvent(ClickEvent.runCommand("/tag map spawn " + map.getName()));
                }
                message = message.append(hover);
                message = message.append(Messenger.nextLine());
            }
        }
        message = message.append(Messenger.component("│", NamedTextColor.DARK_GRAY)).append(Messenger.nextLine());
        message = message.append(Messenger.component("└──────────── ", NamedTextColor.DARK_GRAY).append(Messenger.component("Maps", config.getPrimaryPluginColor(), TextDecoration.BOLD)));
        sender.sendMessage(message);
    }

    private TextComponent createHoverInfo(Config config, xeterios.powertag.configuration.Map map, CommandSender sender)
    {
        boolean addSpawnTeleporter = false;
        TextComponent text = Messenger.component("Map Info", config.getPrimaryPluginColor(), TextDecoration.BOLD).append(Messenger.component(" - " + map.getName(), config.getPrimaryPluginColor())).append(Messenger.nextLine());
        if (map.getSpawn() == null)
        {
            text = text.append(Messenger.component("Spawn: ", NamedTextColor.WHITE)).append(Messenger.component("Not set!", NamedTextColor.RED).append(Messenger.nextLine()));
        }
        else
        {
            addSpawnTeleporter = true;
            text = text.append(Messenger.component("Spawn: ", NamedTextColor.WHITE)).append(Messenger.component(map.getSpawn().getWorld().getName() + ": " + map.getSpawn().getBlockX() + " " + map.getSpawn().getBlockY() + " " + map.getSpawn().getBlockZ(), NamedTextColor.GRAY)).append(Messenger.nextLine());
        }
        if (map.getPos1() == null)
        {
            text = text.append(Messenger.component("Pos1: ", NamedTextColor.WHITE)).append(Messenger.component("Not set!", NamedTextColor.RED).append(Messenger.nextLine()));
        }
        else
        {
            text = text.append(Messenger.component("Pos1: ", NamedTextColor.WHITE)).append(Messenger.component(map.getPos1().getWorld().getName() + ": " + map.getPos1().getBlockX() + " " + map.getPos1().getBlockY() + " " + map.getPos1().getBlockZ(), NamedTextColor.GRAY)).append(Messenger.nextLine());
        }
        if (map.getPos2() == null)
        {
            text = text.append(Messenger.component("Pos2: ", NamedTextColor.WHITE)).append(Messenger.component("Not set!", NamedTextColor.RED));
        }
        else
        {
            text = text.append(Messenger.component("Pos2: ", NamedTextColor.WHITE)).append(Messenger.component(map.getPos2().getWorld().getName() + ": " + map.getPos2().getBlockX() + " " + map.getPos2().getBlockY() + " " + map.getPos2().getBlockZ(), NamedTextColor.GRAY));
        }
        if (addSpawnTeleporter && sender instanceof Player)
        {
            text = text.append(Messenger.nextLine().append(Messenger.component("Click", config.getPrimaryPluginColor())).append(Messenger.component(" to teleport to this spawn.", NamedTextColor.GRAY)));
        }
        return text;
    }
}
