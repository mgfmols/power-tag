package xeterios.powertag.game.powerups;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import xeterios.powertag.configuration.Map;
import xeterios.powertag.game.GameManager;
import xeterios.powertag.game.GameTeam;
import xeterios.powertag.game.timers.BaseTimer;

public class EffectTimer extends BaseTimer
{
    private final PotionEffect effect;
    private final GameTeam team;

    public EffectTimer(GameManager gameManager, Map map, GameTeam team, PotionEffect effect)
    {
        super(gameManager, map);
        this.team = team;
        this.effect = effect;
        this.i = effect.getDuration() / 20;
    }

    @Override
    public void run()
    {
        if (i == 0)
        {
            this.team.deactivateEffect(this);
        }
        else
        {
            for(Player player : gameManager.getPlayersOfType(team.getType()))
            {
                Bukkit.getScheduler().runTask(gameManager.getMain(), () -> player.addPotionEffect(effect));
            }
        }
        i--;
    }
}
