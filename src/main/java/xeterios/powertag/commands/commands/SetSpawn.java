package xeterios.powertag.commands.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xeterios.powertag.Main;
import xeterios.powertag.Messenger;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.game.GameManager;

import java.util.ArrayList;
import java.util.Map;

public record SetSpawn(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.setspawn";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " setspawn";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Set the main spawn point of Power Tag.\nPlayers will return to this location when they leave a game.";
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

        Location location = player.getLocation();
        CommandHandler.sendMessage(config, sender, CommandMessageReason.SPAWN_SET, Messenger.getLocationCoordinates(location));
        config.setSpawn(location);
    }
}
