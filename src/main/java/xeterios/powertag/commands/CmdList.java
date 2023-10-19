package xeterios.powertag.commands;

import xeterios.powertag.commands.commands.*;
import xeterios.powertag.commands.commands.Map;
import xeterios.powertag.commands.commands.Random;

import java.util.*;

public class CmdList
{

    public static LinkedHashMap<String, Cmd> commandList(String[] args)
    {
        return new LinkedHashMap<>()
        {{
            put("tag", new PowerTag(args));
            put("powertag", new PowerTag(args));
            put("forcestart", new ForceStart(args));
            put("help", new Help(args));
            put("information", new Information(args));
            put("join", new Join(args));
            put("leaderboard", new Leaderboard(args));
            put("leave", new Leave(args));
            put("map", new Map(args));
            put("maps", new Maps(args));
            put("openinv", new OpenInv(args));
            put("profile", new Profile(args));
            put("random", new Random(args));
            put("reload", new Reload(args));
            put("setspawn", new SetSpawn(args));
            put("settings", new Settings(args));
            put("spawn", new Spawn(args));
            put("spectate", new Spectate(args));
            put("start", new Start(args));
            put("stop", new Stop(args));
        }};
    }

    public static Cmd getCommand(String commandName, String[] args)
    {
        return commandList(args).get(commandName);
    }
}
