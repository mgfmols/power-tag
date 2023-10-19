package xeterios.powertag.commands;

public enum CommandMessageReason
{
    NO_PERMISSION,
    SENDER_MUST_BE_PLAYER,
    MISSING_ARGUMENTS,
    WRONG_FORMAT,
    PLAYER_DOESNT_EXIST,
    SUBCOMMAND_NOT_FOUND,
    INVALID_AMOUNT,
    SETTINGS_OPENING,
    SETTINGS_CLOSED,
    PROFILE_OPENING,
    LEADERBOARD_OPENING,
    RELOAD_SUCCESSFUL,
    INVENTORY_DOESNT_EXIST,
    GAME_MANAGER_INVALID,
    GAME_MANAGER_NOT_FOUND,
    MAP_NAME_MISSING,
    MAP_DOESNT_EXIST,
    MAP_ALREADY_EXISTS,
    MAP_NO_SPAWN,
    MAP_NO_POS1,
    MAP_NO_POS2,
    MAP_JOINED_EMPTY,
    MAP_JOINED_TOO_LOW,
    MAP_ALREADY_STARTED,
    MAP_STARTED,
    MAP_NOT_STARTED,
    MAP_STOPPED,
    MAP_JOINED,
    MAP_SPECTATING,
    PLAYER_ALREADY_JOINED,
    PLAYER_NOT_JOINED,
    MAP_LEFT,
    MAP_CREATED,
    MAP_REMOVED,
    MAP_SPAWN_SET,
    MAP_TELEPORT_TO_SPAWN,
    MAP_REGION_POS1_SET_INFO,
    MAP_REGION_POS1_SET,
    MAP_REGION_POS2_SET_INFO,
    MAP_REGION_POS2_SET,
    MAP_REGION_SET,
    SPAWN_SET,
    SPAWN_NO_SPAWN,
    SPAWN_NO_WORLD,
    TELEPORT_TO_SPAWN
}