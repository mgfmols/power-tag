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

public record Spawn(String[] args) implements Cmd
{

    @Override
    public String GetPermissionNode()
    {
        return "powertag.spawn";
    }

    @Override
    public String GetCommandFormat(String label)
    {
        return "/" + label + " spawn";
    }

    @Override
    public String GetCommandDescription()
    {
        return "Teleport to the Power Tag spawn";
    }

    @Override
    public void Execute(CommandSender sender, String label, Main main, Config config)
    {
        if (!(sender instanceof Player player))
        {
            CommandHandler.sendMessage(config, sender, CommandMessageReason.SENDER_MUST_BE_PLAYER);
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

        CommandHandler.sendMessage(config, sender, CommandMessageReason.TELEPORT_TO_SPAWN);
        player.teleport(spawn);
    }
}
