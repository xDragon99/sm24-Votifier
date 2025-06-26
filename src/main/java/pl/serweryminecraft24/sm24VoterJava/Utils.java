package pl.serweryminecraft24.sm24VoterJava;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Utils {


    private static JavaPlugin plugin;

    private Utils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    public static void init(JavaPlugin pluginInstance) {
        Utils.plugin = pluginInstance;
    }

    public static String message(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void notifySenderWithClickableLink(CommandSender sender, String text, String link) {
        Bukkit.getScheduler().runTask(Utils.plugin, () -> {
            sender.sendMessage(message(text));

            if (link != null && !link.trim().isEmpty()) {
                TextComponent clickableComponent = new TextComponent(message(link));
                clickableComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));

                if (sender instanceof Player) {
                    ((Player) sender).spigot().sendMessage(clickableComponent);
                } else {
                    sender.sendMessage(link);
                }
            }
        });
    }
}