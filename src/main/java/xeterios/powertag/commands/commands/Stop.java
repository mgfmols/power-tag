package xeterios.powertag.commands.commands;

import org.bukkit.command.CommandSender;
import xeterios.powertag.Main;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.game.GameManager;

import java.util.ArrayList;
import java.util.Map;

public record Stop(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.stop";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " stop <map>";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Stops a game of Power Tag";
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
        if (!manager.isCountdown() && !manager.isStarted())
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.MAP_NOT_STARTED, args[0]);
            return;
        }

        CommandHandler.sendMessage(config, sender, CommandMessageReason.MAP_STOPPED, args[0]);
        manager.stop();
    }
}
