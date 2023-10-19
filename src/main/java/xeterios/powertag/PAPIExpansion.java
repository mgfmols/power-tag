package xeterios.powertag;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xeterios.powertag.configuration.Config;

public class PAPIExpansion extends PlaceholderExpansion
{
    private final Config config;

    public PAPIExpansion(Config config) {
        this.config = config;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "powertag";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Xeterios";
    }

    @Override
    public @NotNull String getVersion() {
        return Main.getPlugin(Main.class).getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, @NotNull String params) {
        if (p == null) {
            return "";
        }
        if (params.equals("wins")) {
            return String.valueOf(config.getPlayerDataHandler().GetPlayerData(p).getTotalWins());
        }
        if (params.equals("points")) {
            return String.valueOf(config.getPlayerDataHandler().GetPlayerData(p).getTotalPoints());
        }
        if (params.equals("winstreak")) {
            return String.valueOf(config.getPlayerDataHandler().GetPlayerData(p).getWinStreak());
        }
        if (params.equals("test"))
        {
            return "\uE101";
        }
        return null;
    }
}
