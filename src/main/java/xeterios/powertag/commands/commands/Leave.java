package xeterios.powertag.commands.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xeterios.powertag.Main;
import xeterios.powertag.commands.CommandHandler;
import xeterios.powertag.commands.CommandMessageReason;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.game.GameManager;

public record Leave(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.leave";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " leave";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Leave a game of Power Tag";
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        if (!(sender instanceof Player player))
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SENDER_MUST_BE_PLAYER);
            return;
        }

        // Check GameManager requirements
        GameManager manager = main.getGameManager(player);
        if (manager == null)
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.PLAYER_NOT_JOINED);
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

        String mapName = manager.getMap().getName();
        CommandHandler.sendMessage(config, sender, CommandMessageReason.MAP_LEFT, mapName);
        manager.removeGamePlayer(player);
    }

}