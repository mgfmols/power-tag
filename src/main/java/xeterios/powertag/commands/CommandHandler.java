package xeterios.powertag.commands;

import net.kyori.adventure.text.TextReplacementConfig;
import xeterios.powertag.Main;
import xeterios.powertag.Messenger;
import xeterios.powertag.commands.commands.Cmd;
import xeterios.powertag.commands.commands.PowerTag;
import xeterios.powertag.configuration.Config;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record CommandHandler(Main main, Config config) implements CommandExecutor, TabCompleter
{

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        Cmd labelCmd = CmdList.getCommand(label, args);
        if (CheckPermission(sender, labelCmd))
        {
            labelCmd.Execute(sender, label, main, config);
        }
        else
        {
            sendMessage(config, sender, CommandMessageReason.NO_PERMISSION);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        ArrayList<String> toReturn = new ArrayList<>();
        Cmd cmd = CmdList.getCommand(label, args);
        if (cmd instanceof PowerTag)
        {
            if (args.length > 1)
            {
                String subLabel = args[0];
                args = Arrays.copyOfRange(args, 1, args.length);
                cmd = CmdList.getCommand(subLabel, args);
            }
        }
        if (cmd == null)
        {
            return toReturn;
        }
        if (!CheckPermission(sender, cmd))
        {
            return toReturn;
        }
        int indexOfLatestArg = args.length;
        int tabCompleteSize = cmd.getCommandTabComplete(sender, config, main.getInventoryList()).size();
        if (indexOfLatestArg <= 0 || indexOfLatestArg > tabCompleteSize)
        {
            return toReturn;
        }
        ArrayList<String> comparing = cmd.getCommandTabComplete(sender, config, main.getInventoryList()).get(indexOfLatestArg - 1);
        String lastArg = args[args.length - 1];
        if (lastArg.length() > 0)
        {
            // Compare all characters for each tab complete
            ArrayList<String> returnEarly = new ArrayList<>();
            for (String arg : comparing)
            {
                boolean match = compareStrings(arg, lastArg);
                // If the loop didn't break, meaning that lastArg is a part of one of the tab completes, return that arg
                if (match)
                {
                    returnEarly.add(arg);
                }
            }
            if (returnEarly.size() > 0)
            {
                return returnEarly;
            }
        }
        if (lastArg.length() == 0)
        {
            return comparing;
        }
        return toReturn;
    }

    private boolean compareStrings(String one, String two)
    {
        boolean match = true;
        char[] oneChars = one.toLowerCase().toCharArray();
        char[] twoChars = two.toLowerCase().toCharArray();
        // Loop through all characters of argChars and lastArgChars
        for (int i = 0; i < twoChars.length; i++)
        {
            if (i >= oneChars.length)
            {
                match = false;
                break;
            }
            char oneChar = oneChars[i];
            char twoChar = twoChars[i];
            if (oneChar != twoChar)
            {
                match = false;
                break;
            }
        }
        return match;
    }

    public static void sendMessage(Config config, CommandSender sender, CommandMessageReason reason)
    {
        TextComponent prefix = config.getPluginPrefix();
        TextComponent addition;
        switch (reason)
        {
            default -> addition = Messenger.component("An unknown error occurred.", NamedTextColor.RED);
            case NO_PERMISSION -> addition = Messenger.component("You don't have permission to use this command.", NamedTextColor.RED);
            case SENDER_MUST_BE_PLAYER -> addition = Messenger.component("Sender must be a player.", NamedTextColor.RED);
            case INVALID_AMOUNT -> addition = Messenger.component("The amount that was given is invalid.", NamedTextColor.RED);
            case SETTINGS_OPENING -> addition = Messenger.component("Opening settings menu...", TextColor.color(0x32a852));
            case SETTINGS_CLOSED -> addition = Messenger.component("Settings saved!", TextColor.color(0x32a852));
            case PROFILE_OPENING -> addition = Messenger.component("Opening profile...", TextColor.color(0x32a852));
            case LEADERBOARD_OPENING -> addition = Messenger.component("Opening leaderboard...", TextColor.color(0x32a852));
            case RELOAD_SUCCESSFUL -> addition = Messenger.component("Configurations reloaded!", TextColor.color(0x32a852));
            case MAP_NAME_MISSING -> addition = Messenger.component("Please give a map name.", NamedTextColor.RED);
            case PLAYER_NOT_JOINED -> addition = Messenger.component("You have not joined a map.", NamedTextColor.RED);
            case GAME_MANAGER_NOT_FOUND -> addition = Messenger.component("Couldn't find a valid game.", NamedTextColor.RED);
            case SPAWN_NO_SPAWN -> addition = Messenger.component("Spawn not set.", NamedTextColor.RED);
            case SPAWN_NO_WORLD -> addition = Messenger.component("Spawn world not set.", NamedTextColor.RED);
            case TELEPORT_TO_SPAWN -> addition = Messenger.component("Teleporting to spawn...", NamedTextColor.GRAY);
        }
        TextComponent component = prefix.append(addition);
        sender.sendMessage(component);
    }

    public static void sendMessage(Config config, CommandSender sender, CommandMessageReason reason, String... args)
    {
        TextComponent prefix = config.getPluginPrefix();
        TextComponent addition;
        switch (reason)
        {
            default -> addition = Messenger.component("An unknown error occurred.", NamedTextColor.RED);
            case PLAYER_DOESNT_EXIST, INVENTORY_DOESNT_EXIST, MAP_DOESNT_EXIST -> addition = Messenger.component("%1 does not exist.", NamedTextColor.RED);
            case GAME_MANAGER_INVALID -> addition = Messenger.component("%1 doesn't have a game manager.", NamedTextColor.RED);
            case MAP_ALREADY_EXISTS -> addition = Messenger.component("%1 already exists.", NamedTextColor.RED);
            case MAP_NO_SPAWN -> addition = Messenger.component("%1 does not have a spawn set.", NamedTextColor.RED);
            case MAP_NO_POS1 -> addition = Messenger.component("%1 does not have a pos1 set.", NamedTextColor.RED);
            case MAP_NO_POS2 -> addition = Messenger.component("%1 does not have a pos2 set.", NamedTextColor.RED);
            case MAP_ALREADY_STARTED -> addition = Messenger.component("%1 already has a game started.", NamedTextColor.RED);
            case MAP_JOINED_EMPTY -> addition = Messenger.component("No one has joined %1.", NamedTextColor.RED);
            case MAP_JOINED_TOO_LOW -> addition = Messenger.component("Not enough players have joined %1.", NamedTextColor.RED);
            case MAP_STARTED -> addition = Messenger.component("%1 has been started.", NamedTextColor.GRAY);
            case MAP_NOT_STARTED -> addition = Messenger.component("%1 does not have a game started currently.", NamedTextColor.RED);
            case MAP_STOPPED -> addition = Messenger.component("%1 has been stopped.", NamedTextColor.GRAY);
            case MAP_JOINED -> addition = Messenger.component("Joined %1.", NamedTextColor.GRAY);
            case PLAYER_ALREADY_JOINED -> addition = Messenger.component("You have already joined %1.", NamedTextColor.RED);
            case MAP_LEFT -> addition = Messenger.component("Left %1.", NamedTextColor.GRAY);
            case MAP_SPECTATING -> addition = Messenger.component("Spectating %1.", NamedTextColor.GRAY);
            case MAP_CREATED -> addition = Messenger.component("Created a new map called %1.", NamedTextColor.GRAY);
            case MAP_REMOVED -> addition = Messenger.component("Removed map %1.", NamedTextColor.GRAY);
            case MAP_REGION_POS1_SET_INFO -> addition = Messenger.component("Left click a block to set pos1 of %1.", NamedTextColor.GRAY);
            case MAP_REGION_POS2_SET_INFO -> addition = Messenger.component("Right click a block to set pos2 of %1.", NamedTextColor.GRAY);
            case MAP_REGION_POS1_SET -> addition = Messenger.component("%1 pos1 set to %2.", NamedTextColor.GRAY);
            case MAP_REGION_POS2_SET -> addition = Messenger.component("%1 pos2 set to %2.", NamedTextColor.GRAY);
            case MAP_REGION_SET -> addition = Messenger.component("%1 region set.", NamedTextColor.GRAY);
            case MAP_SPAWN_SET -> addition = Messenger.component("%1 spawn set to %2.", NamedTextColor.GRAY);
            case MAP_TELEPORT_TO_SPAWN -> addition = Messenger.component("Teleporting to %1 spawn.", NamedTextColor.GRAY);
            case SPAWN_SET -> addition = Messenger.component("Power Tag spawn has been set to: ", NamedTextColor.GRAY).append(Messenger.component("%1", NamedTextColor.WHITE).append(Messenger.component(".", NamedTextColor.GRAY)));
        }
        for (int i = 0; i < args.length; i++)
        {
            TextReplacementConfig textReplacementConfig = TextReplacementConfig.builder()
                    .match("[%][" + (i + 1) + "]")
                    .replacement(Messenger.component(args[i], NamedTextColor.WHITE))
                    .build();
            addition = (TextComponent) addition.replaceText(textReplacementConfig);
        }
        TextComponent component = prefix.append(addition);
        sender.sendMessage(component);
    }

    public static void sendMessage(Config config, Cmd cmd, String label, CommandSender sender, CommandMessageReason reason)
    {
        TextComponent prefix = config.getPluginPrefix();
        TextComponent addition;
        switch (reason)
        {
            default -> addition = Messenger.component("An unknown error occurred.", NamedTextColor.RED);
            case WRONG_FORMAT, MISSING_ARGUMENTS -> addition = Messenger.component("Wrong format used.\n", NamedTextColor.RED)
                    .append(prefix).append(Messenger.component("Format: ", NamedTextColor.RED).append(Messenger.component(cmd.GetCommandFormat(label), NamedTextColor.YELLOW)));
            case SUBCOMMAND_NOT_FOUND -> addition = Messenger.component("Subcommand not found.", NamedTextColor.RED);
        }
        addition = addition.decoration(TextDecoration.BOLD, false);
        TextComponent component = prefix.append(addition);
        sender.sendMessage(component);
    }

    private static boolean CheckPermission(CommandSender sender, Cmd cmd)
    {
        return sender.hasPermission(cmd.GetPermissionNode()) || sender.hasPermission("powertag.admin");
    }
}
