package xeterios.powertag.commands.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xeterios.powertag.Main;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.game.GameManager;

import java.util.ArrayList;
import java.util.Map;

public record Join(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.join";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " join <map>";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Join a game of Power Tag";
    }

    @Override
    public ArrayList<ArrayList<String>> GetCommandTabComplete(CommandSender sender, Config config)
    {
        return new ArrayList<>()
        {{
            add(new ArrayList<>()
            {{
                for (Map.Entry<String, xeterios.powertag.configuration.Map> map : config.getMaps().entrySet())
                {
                    add(map.getKey());
                }
            }});
        }};
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        if (!(sender instanceof Player player))
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SENDER_MUST_BE_PLAYER);
            return;
        }

        if (args.length <= 0)
        {
            CommandHandler.sendMessage(config, this, label, sender, CommandMessageReason.MISSING_ARGUMENTS);
            return;
        }

        // Check map requirements
        xeterios.powertag.configuration.Map map = main.getMap(args[0]);
        if (map == null)
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.MAP_DOESNT_EXIST, args[0]);
            return;
        }

        // Check GameManager requirements
        GameManager manager = main.getGameManager(args[0]);
        if (manager == null)
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.GAME_MANAGER_INVALID, args[0]);
            return;
        }
        if (manager.isStarted())
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.MAP_ALREADY_STARTED, args[0]);
            return;
        }
        GameManager playerHasJoined = main.playerHasJoined(player);
        if (playerHasJoined != null)
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.PLAYER_ALREADY_JOINED, playerHasJoined.getMap().getName());
            return;
        }

        // Check map requirements
        Location spawn = config.getSpawn();
        if (spawn == null)
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SPAWN_NO_SPAWN);
            return;
        }
        if (spawn.getWorld() == null)
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SPAWN_NO_WORLD);
            return;
        }

        CommandHandler.sendMessage(config, sender, CommandMessageReason.MAP_JOINED, args[0]);
        manager.addGamePlayer(player);
    }
}