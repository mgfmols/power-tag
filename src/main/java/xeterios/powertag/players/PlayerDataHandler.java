package xeterios.powertag.players;

import lombok.Getter;
import xeterios.powertag.Main;
import xeterios.powertag.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xeterios.powertag.players.comparers.SortingType;
import xeterios.powertag.players.comparers.comparators.PointComparator;
import xeterios.powertag.players.comparers.comparators.UUIDComparator;
import xeterios.powertag.players.comparers.comparators.WinStreakComparator;
import xeterios.powertag.players.comparers.comparators.WinsComparator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerDataHandler implements Listener
{

    @Getter
    private final ArrayList<PlayerData> playerData;

    public PlayerDataHandler()
    {
        Messenger.log(Level.INFO, "Loading online player data...");
        this.playerData = new ArrayList<>();
        GetOfflinePlayers();
        Bukkit.getPluginManager().registerEvents(this, Main.getPlugin(Main.class));
        Messenger.log(Level.INFO, "Player data loaded.");
    }

    private void GetOnlinePlayers()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            PlayerData playerData = GetPlayerData(player);
            if (playerData == null)
            {
                CreateNewPlayerData(player, true);
                continue;
            }
            AddPlayerDataToCache(playerData);
        }
    }

    private void GetOfflinePlayers()
    {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers())
        {
            PlayerData playerData = GetPlayerData(player);
            if (playerData == null)
            {
                CreateNewPlayerData(player, true);
                continue;
            }
            AddPlayerDataToCache(playerData);
        }
    }

    //region »» PlayerData Retrieving ««
    public PlayerData GetPlayerData(OfflinePlayer player)
    {
        for (PlayerData playerData : this.playerData)
        {
            if (player.getUniqueId().equals(playerData.getUuid()))
            {
                return playerData;
            }
        }
        return LoadPlayerDataFromFile(player.getUniqueId());
    }

    public PlayerData GetPlayerData(UUID uuid)
    {
        for (PlayerData playerData : playerData)
        {
            if (uuid.equals(playerData.getUuid()))
            {
                return playerData;
            }
        }
        return LoadPlayerDataFromFile(uuid);
    }

    public OfflinePlayer GetOfflinePlayer(UUID uuid)
    {
        for (PlayerData playerData : playerData)
        {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playerData.getUuid());
            assert player.getName() != null;
            if (player.getUniqueId().equals(uuid))
            {
                return player;
            }
        }
        return null;
    }

    public ArrayList<PlayerData> GetLeaderboard(SortingType sortingType)
    {
        ArrayList<PlayerData> leaderboard = new ArrayList<>(playerData);
        switch (sortingType)
        {
            case UUID -> leaderboard.sort(new UUIDComparator());
            case Points -> leaderboard.sort(new PointComparator());
            case Wins -> leaderboard.sort(new WinsComparator());
            case Winstreak -> leaderboard.sort(new WinStreakComparator());
        }
        return leaderboard;
    }

    public int GetLeaderboardPosition(SortingType type, PlayerData data)
    {
        ArrayList<PlayerData> leaderboard = GetLeaderboard(type);
        int place = leaderboard.indexOf(data);
        if (place != 0)
        {
            switch (type)
            {
                case UUID -> {
                    for (PlayerData leaderboardData : leaderboard)
                    {
                        if (leaderboardData.getUuid() == data.getUuid())
                        {
                            place = leaderboard.indexOf(leaderboardData);
                            break;
                        }
                    }
                }
                case Points -> {
                    for (PlayerData leaderboardData : leaderboard)
                    {
                        if (leaderboardData.getTotalPoints() == data.getTotalPoints())
                        {
                            place = leaderboard.indexOf(leaderboardData);
                            break;
                        }
                    }
                }
                case Wins -> {
                    for (PlayerData leaderboardData : leaderboard)
                    {
                        if (leaderboardData.getTotalWins() == data.getTotalWins())
                        {
                            place = leaderboard.indexOf(leaderboardData);
                            break;
                        }
                    }
                }
                case Winstreak -> {
                    for (PlayerData leaderboardData : leaderboard)
                    {
                        if (leaderboardData.getWinStreak() == data.getWinStreak())
                        {
                            place = leaderboard.indexOf(leaderboardData);
                            break;
                        }
                    }
                }
            }
        }
        return place + 1;
    }

    //endregion

    //region »» Cache Handling ««
    private void AddPlayerDataToCache(PlayerData data)
    {
        if (playerData.contains(data))
        {
            return;
        }
        playerData.add(data);
        if (playerData.size() > 1)
        {
            playerData.sort(new UUIDComparator());
        }
    }

    private void RemovePlayerDataFromCache(PlayerData data)
    {
        SavePlayerData(data);
        playerData.remove(data);
        if (playerData.size() > 1)
        {
            playerData.sort(new UUIDComparator());
        }
    }

//    private void LoadPlayerData(OfflinePlayer player){
//        if (this.CheckPlayerData(player.getUniqueId())){
//            AddPlayerDataToCache(LoadPlayerDataFromFile(player.getUniqueId()));
//        } else {
//            CreateNewPlayerData(player);
//        }
//    }

    private void LoadPlayerData(OfflinePlayer player)
    {
        PlayerData playerData = GetPlayerData(player);
        if (playerData == null)
        {
            CreateNewPlayerData(player, true);
        }
        //AddPlayerDataToCache(playerData);
    }

    public void CreateNewPlayerData(OfflinePlayer player, boolean addToCache)
    {
        PlayerData data = new PlayerData(player.getUniqueId());
        SavePlayerData(data);
        if (addToCache)
        {
            AddPlayerDataToCache(data);
        }
    }

    private boolean CheckPlayerData(UUID uuid)
    {
        boolean exists = false;
        if (GetPlayerData(uuid) == null)
        {
            File fl = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + "players");
            File[] files = fl.listFiles();
            if (files != null)
            {
                for (File child : files)
                {
                    if (child.getName().replace(".yml", "").equals(uuid.toString()))
                    {
                        exists = true;
                    }
                }
            }
        }
        else
        {
            exists = true;
        }
        return exists;
    }
    //endregion

    //region »» File Handling ««
    private PlayerData LoadPlayerDataFromFile(UUID uuid)
    {
        File fl = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + "players");
        File[] files = fl.listFiles();
        if (files != null)
        {
            for (File child : files)
            {
                String name = child.getName().replaceFirst("[.][^.]+$", "");
                if (name.equals(uuid.toString()))
                {
                    FileConfiguration config = new YamlConfiguration();
                    try
                    {
                        config.load(child);
                    }
                    catch (IOException | InvalidConfigurationException e)
                    {
                        e.printStackTrace();
                    }
                    for (String string : config.getKeys(false))
                    {
                        return config.getObject(string, PlayerData.class);
                    }
                }
            }
        }
        CreateNewPlayerData(Bukkit.getOfflinePlayer(uuid), false);
        return null;
    }

    private ArrayList<PlayerData> RetrieveAllPlayers()
    {
        ArrayList<PlayerData> data = new ArrayList<>();
        File fl = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + "players");
        File[] files = fl.listFiles();
        if (files != null)
        {
            for (File child : files)
            {
                String name = child.getName().replaceFirst("[.][^.]+$", "");
                FileConfiguration config = new YamlConfiguration();
                try
                {
                    config.load(child);
                }
                catch (IOException | InvalidConfigurationException e)
                {
                    e.printStackTrace();
                }
                for (String string : config.getKeys(false))
                {
                    data.add(config.getObject(string, PlayerData.class));
                }
            }
        }
        return data;
    }

    public void SavePlayerData(PlayerData data)
    {
        File fl = new File(Main.getPlugin(Main.class).getDataFolder() + File.separator + "players", data.getUuid() + ".yml");
        FileConfiguration fc = new YamlConfiguration();
        fc.set(fl.getName().replace(".yml", ""), data);
        try
        {
            fc.save(fl);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void SaveAllPlayerData()
    {
        Messenger.log(Level.INFO, "Saving player data...");
        for (PlayerData data : playerData)
        {
            SavePlayerData(data);
        }
        Messenger.log(Level.INFO, "Player data saved.");
    }
    //endregion

    //region »» Event Handling ««
    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent event)
    {
        LoadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void OnPlayerLeave(PlayerQuitEvent event)
    {
        //RemovePlayerDataFromCache(GetPlayerData(event.getPlayer().getUniqueId()));
    }
    //endregion
}
