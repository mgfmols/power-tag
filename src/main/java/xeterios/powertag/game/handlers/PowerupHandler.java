package xeterios.powertag.game.handlers;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import xeterios.powertag.Messenger;
import xeterios.powertag.game.GameManager;
import xeterios.powertag.game.GamePlayer;
import xeterios.powertag.game.GamePlayerType;
import xeterios.powertag.game.TagReason;
import xeterios.powertag.game.powerups.PowerupList;
import xeterios.powertag.game.powerups.PowerupTimer;
import xeterios.powertag.game.powerups.powerups.Powerup;
import xeterios.powertag.game.powerups.powerups.Sniper;

import java.util.*;

public class PowerupHandler implements Listener
{
    private final Plugin plugin;
    @Getter private final GameManager gameManager;
    @Getter private final HashMap<Location, Powerup> powerups;
    @Getter private final HashMap<Location, BukkitTask> powerupEffects;
    private final ArrayList<Projectile> arrows;

    private boolean active;
    private Timer timer;

    public PowerupHandler(Plugin plugin, GameManager gameManager)
    {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.powerups = new HashMap<>();
        this.powerupEffects = new HashMap<>();
        this.arrows = new ArrayList<>();
        this.active = false;

        // Continuously spawn particles on arrows
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            for (Projectile arrow : arrows){
                Particle.DustOptions options = new Particle.DustOptions(Color.RED, 5);

                Location location = arrow.getLocation();
                location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, options);
            }
        }, 0, 0);
    }

    public void despawnPowerup(Location location)
    {
        // Removing entities
        for (Entity entity : location.getWorld().getNearbyEntities(location, 3, 3, 3)){
            if (entity instanceof Player)
            {
                continue;
            }
            entity.remove();
        }

        // Removing particles
        powerupEffects.get(location).cancel();
    }

    public void despawnPowerups()
    {
        // Removing entities
        ArrayList<Location> locations = new ArrayList<>();
        for(Location location : powerups.keySet())
        {
            despawnPowerup(location);
            locations.add(location);
        }
        for(Location location : locations)
        {
            powerups.remove(location);
        }
    }

    public void enableHandler()
    {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        enableTimer();
    }

    public void enableTimer()
    {
        this.timer = new Timer();
        TimerTask task = new PowerupTimer(this, gameManager, gameManager.getMap());
        this.timer.schedule(task, 0, 1000);
        this.active = true;
    }

    public void disableHandler()
    {
        HandlerList.unregisterAll(this);
        disableTimer();
    }

    public void disableTimer()
    {
        if (!this.active)
        {
            return;
        }
        this.timer.cancel();
        this.timer = new Timer();
        this.active = false;
    }

    public void spawnPowerup()
    {
        Powerup powerup = PowerupList.getRandomPowerup(gameManager.getMain().config());
        Location location = gameManager.getMap().getRandomLocation();

        powerups.put(location, powerup);

        // Spawn item
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            Item item = location.getWorld().dropItem(location, powerup.getItem());
            item.setVelocity(new Vector(0, 0, 0));
            item.setGravity(false);
        });

        // Spawn firework
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            Location fireworkLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
            fireworkLocation.add(0, 1, 0);
            Firework fw = (Firework) location.getWorld().spawnEntity(fireworkLocation, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            fwm.setPower(3);
            fwm.addEffect(FireworkEffect.builder().withColor(powerup.getPowerupColor()).flicker(true).build());
            fw.setFireworkMeta(fwm);
            Bukkit.getScheduler().runTaskLater(this.plugin, fw::detonate, 28L);
        });

        // Spawn particles
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            Particle.DustOptions options = new Particle.DustOptions(powerup.getPowerupColor(), 2);
            location.getWorld().spawnParticle(Particle.REDSTONE, new Location(location.getWorld(), location.getX(), location.getY()-0.6, location.getZ()), 1, 1, 1, 1, options);
            location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, new Location(location.getWorld(), location.getX(), location.getY()-0.6, location.getZ()), 1, 1, 1, 1, 0);
        }, 0, (long) 3.5);

        powerupEffects.put(location, task);

        // Spawn hologram
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getX(), location.getY()-1.25, location.getZ()), EntityType.ARMOR_STAND);
            hologram.setGravity(false);
            hologram.setCanPickupItems(false);
            hologram.setCanMove(false);
            hologram.setVisible(false);
            hologram.customName(powerup.getName());
            hologram.setCustomNameVisible(true);
        });
    }

    @EventHandler
    public void PickupPowerup(EntityPickupItemEvent e)
    {
        Location powerupLocation = e.getItem().getLocation();
        powerupLocation.setYaw(0);
        Powerup powerup = powerups.get(powerupLocation);
        powerup.trigger((Player) e.getEntity(), this);
        despawnPowerup(powerupLocation);
        powerups.remove(powerupLocation);

        e.getItem().remove();
        e.setCancelled(true);
    }

    @EventHandler
    public void ShootSniperBow(EntityShootBowEvent e)
    {
        if (e.getEntity() instanceof Player shooter)
        {
            shooter.getInventory().setItemInOffHand(new ItemStack(Material.AIR, 1));
            shooter.getInventory().remove(Sniper.getBow());
            this.arrows.add((Projectile) e.getProjectile());
        }
    }

    @EventHandler
    public void DetectTagByArrow(ProjectileHitEvent e)
    {
        Projectile arrow = e.getEntity();
        // Check if projectile is an arrow
        if (!(arrow instanceof Arrow))
        {
            return;
        }

        this.arrows.remove(arrow);
        e.setCancelled(true);
        arrow.remove();

        // Check if both damager and receiver are players
        if (!(arrow.getShooter() instanceof Player damager))
        {
            return;
        }
        if (!(e.getHitEntity() instanceof Player receiver))
        {
            return;
        }

        GamePlayer gamePlayerDamager = gameManager.getGamePlayer(damager);
        GamePlayer gamePlayerReceiver = gameManager.getGamePlayer(receiver);

        // Check if damager is a tagger
        if (!gamePlayerDamager.getType().equals(GamePlayerType.TAGGER))
        {
            return;
        }
        // Check if receiver is a runner
        if (!gamePlayerReceiver.getType().equals(GamePlayerType.RUNNER))
        {
            return;
        }

        // Swap the tag
        gameManager.swapTagger(gamePlayerDamager, gamePlayerReceiver);
        Messenger.announceTaggers(gameManager.getPlayers(), TagReason.SHOT, gamePlayerDamager.getPlayer(), new ArrayList<>(List.of(gamePlayerReceiver.getPlayer())));

        // Bonus point calculation
        double distanceBetween = damager.getLocation().distance(receiver.getLocation());
        if (distanceBetween >= Sniper.bonusPointDistance())
        {
            gamePlayerDamager.addBonusPoints(Sniper.bonusPointAmount());
            Messenger.message(damager, Messenger.component("You sniped ", NamedTextColor.GRAY)
                    .append(Messenger.component(receiver.getName(), NamedTextColor.RED))
                    .append(Messenger.component(" from over ", NamedTextColor.GRAY))
                    .append(Messenger.component(Sniper.bonusPointDistance() + "m", NamedTextColor.DARK_AQUA, TextDecoration.UNDERLINED))
                    .append(Messenger.component(" and gained ", NamedTextColor.GRAY))
                    .append(Messenger.component(String.valueOf(Sniper.bonusPointAmount()), NamedTextColor.AQUA))
                    .append(Messenger.component(" bonus point(s)", NamedTextColor.GRAY)));
        }
    }

    @EventHandler
    public void DetectFireworkLaunch(PlayerInteractEvent e)
    {
        if (!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)))
        {
            return;
        }

        if (e.getItem() == null)
        {
            return;
        }

        // Launching player
        Player player = e.getPlayer();
        if (!e.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.FIREWORK_ROCKET))
        {
            return;
        }

        player.setVelocity(new Vector(player.getVelocity().getX(), xeterios.powertag.game.powerups.powerups.Firework.launchHeight(), player.getVelocity().getZ()));
        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR, 1));
        player.getInventory().remove(player.getInventory().getItemInOffHand());

        // Particles
        Messenger.playSound(player, player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, 1);
        int schedule = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 1, 0, 0, 0, 0), 0, 2);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Bukkit.getScheduler().cancelTask(schedule), 20);
    }
}
