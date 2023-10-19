package xeterios.powertag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xeterios.powertag.configuration.Config;
import xeterios.powertag.game.TagReason;

import java.time.Duration;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Messenger
{

    private static final Logger logger = Logger.getLogger("Minecraft");
    private static Config config = null;

    public static void setConfig(Config config)
    {
        Messenger.config = config;
    }

    public static void log(Level level, String message)
    {
        Main main = Main.getPlugin(Main.class);
        logger.log(level, main.getPluginMeta().getLoggerPrefix() + " " + message);
    }

    public static TextComponent component(String message)
    {
        return Messenger.component(message, NamedTextColor.WHITE);
    }

    public static TextComponent component(String message, TextColor color)
    {
        return Component.text(message).color(color)
                .decoration(TextDecoration.BOLD, false)
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, false)
                .decoration(TextDecoration.OBFUSCATED, false)
                .decoration(TextDecoration.STRIKETHROUGH, false);

    }

    public static TextComponent component(String message, TextColor color, TextDecoration... decorations)
    {
        return (TextComponent) component(message, color).decorate(decorations);
    }

    public static TextComponent empty()
    {
        return Component.text("");
    }

    public static TextComponent space()
    {
        return Component.text(" ");
    }

    public static TextComponent nextLine()
    {
        return Component.text("\n");
    }

    public static String getLocationCoordinates(Location location)
    {
        return location.getWorld().getName() + ", " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
    }

    //region Messaging

    public static void message(CommandSender receiver, String message, TextColor color)
    {
        TextComponent text = config.getPluginPrefix();
        text = text.append(component(message, color));
        receiver.sendMessage(text);
    }

    public static void message(CommandSender receiver, TextComponent message)
    {
        TextComponent text = config.getPluginPrefix();
        text = text.append(message);
        receiver.sendMessage(text);
    }

    public static void messageAll(ArrayList<Player> receivers, String message, TextColor color)
    {
        for (Player player : receivers)
        {
            message(player, message, color);
        }
    }

    public static void messageAll(ArrayList<Player> receivers, TextComponent message)
    {
        for (Player player : receivers)
        {
            message(player, message);
        }
    }

    public static void announceTaggers(ArrayList<Player> receivers, TagReason reason, Player tagger, ArrayList<Player> targets)
    {
        TextComponent text = empty();
        switch (reason)
        {
            case START -> text = createStartTagMessage(text, targets);
            case HIT -> text = createHitTagMessage(text, tagger, targets);
            case SHOT -> text = createShotTagMessage(text, tagger, targets);
            case RANDOMIZED -> text = createRandomizedTagMessage(text, targets);
            default -> {
                log(Level.SEVERE, "Tag reason was not specified");
                return;
            }
        }
        messageAll(receivers, text);
    }

    private static TextComponent createStartTagMessage(TextComponent text, ArrayList<Player> targets)
    {
        for (int i = 0; i < targets.size(); i++)
        {
            // Add player name to message
            Player target = targets.get(i);
            text = text.append(component(target.getName(), NamedTextColor.RED));
            // Add comma if player is not the last in the list
            if (i < targets.size() - 2)
            {
                text = text.append(component(", ", NamedTextColor.RED));
            }
            // Add ampersand if player is second to last in the last
            if (i == targets.size() - 2)
            {
                text = text.append(component(" & ", NamedTextColor.RED));
            }
        }
        // If there are more than 1 tagger, use 'is' instead of 'are'
        if (targets.size() == 1)
        {
            text = text.append(component(" is now a tagger!", NamedTextColor.RED));
        }
        else
        {
            text = text.append(component(" are now taggers!", NamedTextColor.RED));
        }
        return text;
    }

    private static TextComponent createShotTagMessage(TextComponent text, Player tagger, ArrayList<Player> targets)
    {
        text = text
                .append(component(tagger.getName(), NamedTextColor.GREEN))
                .append(component(" has SNIPED ", NamedTextColor.GRAY))
                .append(component(targets.get(0).getName(), NamedTextColor.RED))
                .append(component("!", NamedTextColor.GRAY));
        return text;
    }

    private static TextComponent createHitTagMessage(TextComponent text, Player tagger, ArrayList<Player> targets)
    {
        text = text
                .append(component(tagger.getName(), NamedTextColor.GREEN))
                .append(component(" has tagged ", NamedTextColor.GRAY))
                .append(component(targets.get(0).getName(), NamedTextColor.RED))
                .append(component("!", NamedTextColor.GRAY));
        return text;
    }

    private static TextComponent createRandomizedTagMessage(TextComponent text, ArrayList<Player> targets)
    {
        text = text
                .append(component("The taggers have been ", NamedTextColor.LIGHT_PURPLE))
                .append(component("SHUFFLED", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD))
                .append(component("!", NamedTextColor.LIGHT_PURPLE));
        text = text.append(createStartTagMessage(text, targets));
        return text;
    }

    //endregion

    //region Actionbar

    public static void actionBar(Player receiver, String message, TextColor color)
    {
        receiver.sendActionBar(component(message, color));
    }

    public static void actionBar(Player receiver, TextComponent text)
    {
        receiver.sendActionBar(text);
    }

    public static void actionBarAll(ArrayList<Player> receivers, String message, TextColor color)
    {
        TextComponent text = component(message, color);
        for (Player player : receivers)
        {
            actionBar(player, text);
        }
    }

    public static void actionBarAll(ArrayList<Player> receivers, TextComponent text)
    {
        for (Player player : receivers)
        {
            actionBar(player, text);
        }
    }

    //endregion

    //region Title

    // Send title to player
    private static void sendTitle(Player receiver, Title title)
    {
        receiver.showTitle(title);
    }

    // Translate fadeIn, stay and fadeOut to Times interface
    private static Title.Times translateTimes(int fadeIn, int stay, int fadeOut)
    {
        return Title.Times.times(Duration.ofMillis(fadeIn * 50L), Duration.ofMillis(stay * 50L), Duration.ofMillis(fadeOut * 50L));
    }

    // Send title based on component
    public static void title(Player receiver, TextComponent title, int fadeIn, int stay, int fadeOut)
    {
        sendTitle(receiver, Title.title(title, Component.text(""), translateTimes(fadeIn, stay, fadeOut)));
    }

    // Send title and subtitle based on component
    public static void title(Player receiver, TextComponent title, TextComponent subtitle, int fadeIn, int stay, int fadeOut)
    {
        sendTitle(receiver, Title.title(title, subtitle, translateTimes(fadeIn, stay, fadeOut)));
    }

    // Send title to ALL based on component
    public static void titleAll(ArrayList<Player> receivers, TextComponent title, int fadeIn, int stay, int fadeOut)
    {
        for (Player player : receivers)
        {
            sendTitle(player, Title.title(title, Component.text(""), translateTimes(fadeIn, stay, fadeOut)));
        }
    }

    // Send title and subtitle to ALL based on component
    public static void titleAll(ArrayList<Player> receivers, TextComponent title, TextComponent subtitle, int fadeIn, int stay, int fadeOut)
    {
        for (Player player : receivers)
        {
            sendTitle(player, Title.title(title, subtitle, translateTimes(fadeIn, stay, fadeOut)));
        }
    }

    //endregion

    //region Sound

    public static void playSound(Player receiver, Location location, Sound sound, float volume, float pitch)
    {
        receiver.playSound(location, sound, volume, pitch);
    }

    public static void playSoundAll(ArrayList<Player> receivers, Sound sound, float pitch)
    {
        for (Player player : receivers)
        {
            playSound(player, player.getLocation(), sound, 15, pitch);
        }
    }

    public static void playSoundAll(ArrayList<Player> receivers, Location location, Sound sound, float volume, float pitch)
    {
        for (Player player : receivers)
        {
            playSound(player, location, sound, volume, pitch);
        }
    }

    //endregion
}
