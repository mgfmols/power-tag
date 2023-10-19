package xeterios.powertag.players;

import lombok.Getter;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import xeterios.powertag.Messenger;
import xeterios.powertag.players.comparers.SortingType;
import xeterios.powertag.players.settings.Settings;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SerializableAs("PlayerData")
public class PlayerData implements ConfigurationSerializable
{

    @Getter
    private final UUID uuid;
    @Getter
    private final Settings settings;
    @Getter
    private int totalPoints;
    @Getter
    private int totalWins;
    @Getter
    private int winStreak;

    public PlayerData(UUID uuid)
    {
        this.uuid = uuid;
        this.settings = new Settings();
    }

    public PlayerData(UUID uuid, Settings settings, int totalPoints, int totalWins, int winStreak)
    {
        this.uuid = uuid;
        this.settings = settings;
        this.totalPoints = totalPoints;
        this.totalWins = totalWins;
        this.winStreak = winStreak;
    }

    //region Player Display

    public static ItemStack getPlayerHead(OfflinePlayer target, PlayerData playerData, PlayerDataHandler playerDataHandler, SortingType sortingType, boolean includeUuids)
    {
        if (target == null || target.getName() == null)
        {
            ItemStack errorHead = new ItemStack(Material.BARRIER, 1);
            ItemMeta errorHeadMeta = errorHead.getItemMeta();
            errorHeadMeta.displayName(Messenger.component("Player not specified", NamedTextColor.DARK_RED));
            errorHead.setItemMeta(errorHeadMeta);
            return errorHead;
        }

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta playerHeadMeta = playerHead.getItemMeta();

        TextColor nameColor;
        if (sortingType == null)
        {
            nameColor = NamedTextColor.BLUE;
        }
        else
        {
            nameColor = getPlaceColor(playerDataHandler, playerData, sortingType);
        }
        playerHeadMeta.displayName(Messenger.component(target.getName(), nameColor));

        List<TextComponent> lore = new ArrayList<>();
        lore.add(Messenger.component("Stats", NamedTextColor.YELLOW));
        lore.addAll(getLoreOrder(playerData, playerDataHandler, sortingType));
        if (includeUuids)
        {
            lore.add(Messenger.component(target.getUniqueId().toString(), NamedTextColor.DARK_GRAY));
        }
        playerHeadMeta.lore(lore);

        SkullMeta skullMeta = (SkullMeta) playerHeadMeta;
        skullMeta.setOwningPlayer(target);
        playerHead.setItemMeta(skullMeta);
        return playerHead;
    }

    private static List<TextComponent> getLoreOrder(PlayerData playerData, PlayerDataHandler playerDataHandler, SortingType sortingType)
    {
        List<TextComponent> lore = new ArrayList<>();
        ArrayList<SortingType> order = new ArrayList<>();
        order.add(sortingType);
        for (SortingType type : SortingType.values())
        {
            if (!type.equals(sortingType))
            {
                order.add(type);
            }
        }
        for (SortingType type : order)
        {
            switch (type)
            {
                case Points -> lore.add(Messenger.component(playerData.getTotalPoints() + " points ", NamedTextColor.WHITE).append(Messenger.component("(#" + playerDataHandler.GetLeaderboardPosition(SortingType.Points, playerData) + ")", getPlaceColor(playerDataHandler, playerData, SortingType.Points))));
                case Wins -> lore.add(Messenger.component(playerData.getTotalWins() + " wins ", NamedTextColor.WHITE).append(Messenger.component("(#" + playerDataHandler.GetLeaderboardPosition(SortingType.Wins, playerData) + ")", getPlaceColor(playerDataHandler, playerData, SortingType.Wins))));
                case Winstreak -> lore.add(Messenger.component(playerData.getWinStreak() + " winstreak ", NamedTextColor.WHITE).append(Messenger.component("(#" + playerDataHandler.GetLeaderboardPosition(SortingType.Winstreak, playerData) + ")", getPlaceColor(playerDataHandler, playerData, SortingType.Winstreak))));
            }
        }
        return lore;
    }

    private static TextColor getPlaceColor(PlayerDataHandler playerDataHandler, PlayerData playerData, SortingType type)
    {
        int place = playerDataHandler.GetLeaderboardPosition(type, playerData);
        switch (place)
        {
            case 1:
                return TextColor.color(209, 176, 0);
            case 2:
                return TextColor.color(192, 192, 192);
            case 3:
                return TextColor.color(205, 127, 50);
            case 4:
                return TextColor.color(32, 178, 170);
            default:
                if (place <= 10)
                {
                    return TextColor.color(101, 252, 101);
                }
                else if (place <= 50)
                {
                    return TextColor.color(152, 251, 152);
                }
                else if (place <= 100)
                {
                    return TextColor.color(219, 255, 219);
                }
                else
                {
                    return NamedTextColor.WHITE;
                }
        }
    }

    //endregion

    //region Data manipulation

    public void addPoints(int amount)
    {
        totalPoints += amount;
    }

    public boolean removePoints(int amount)
    {
        if (totalPoints - amount >= 0)
        {
            totalPoints -= amount;
            return true;
        }
        return false;
    }

    public boolean setPoints(int amount)
    {
        if (amount >= 0)
        {
            totalPoints = amount;
            return true;
        }
        return false;
    }

    public void addWins(int amount)
    {
        totalWins += amount;
    }

    public boolean removeWins(int amount)
    {
        if (totalWins - amount >= 0)
        {
            totalWins -= amount;
            return true;
        }
        return false;
    }

    public boolean setWins(int amount)
    {
        if (amount >= 0)
        {
            totalWins = amount;
            return true;
        }
        return false;
    }

    public void addWin()
    {
        totalWins++;
        winStreak++;
    }

    public void resetWinStreakCount()
    {
        winStreak = 0;
    }

    //endregion

    //region Serialization

    @Override
    public @NotNull Map<String, Object> serialize()
    {
        Map<String, Object> result = new HashMap<>();
        result.put("uuid", this.uuid.toString());
        result.put("settings", this.settings);
        result.put("points", totalPoints);
        result.put("wins", totalWins);
        result.put("winstreak", winStreak);
        return result;
    }

    public static PlayerData deserialize(Map<String, Object> args)
    {
        UUID uuid = null;
        Settings settings = null;
        int points = 0;
        int wins = 0;
        int winStreak = 0;

        if (args.containsKey("uuid"))
        {
            uuid = UUID.fromString(String.valueOf(args.get("uuid")));
        }
        if (args.containsKey("settings"))
        {
            settings = (Settings) args.get("settings");
        }
        if (args.containsKey("points"))
        {
            points = (int) args.get("points");
        }
        if (args.containsKey("wins"))
        {
            wins = (int) args.get("wins");
        }
        if (args.containsKey("winstreak"))
        {
            winStreak = (int) args.get("winstreak");
        }

        return new PlayerData(uuid, settings, points, wins, winStreak);
    }

    //endregion
}
