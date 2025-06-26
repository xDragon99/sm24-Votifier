package pl.serweryminecraft24.sm24VoterJava.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.serweryminecraft24.sm24VoterJava.Sm24VoterJava;
import pl.serweryminecraft24.sm24VoterJava.Utils;
import pl.serweryminecraft24.sm24VoterJava.config.PluginConfig;

public final class ReloadCommand implements CommandExecutor {


    private final Sm24VoterJava plugin;
    private static final String RELOAD_PERMISSION = "sm24.command.reload";

    public ReloadCommand(Sm24VoterJava plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(RELOAD_PERMISSION)) {

            String noPermMessage = plugin.getPluginConfig().getMsgNoPermission().replace("{prefix}", plugin.getPluginConfig().getMsgPrefix());
            sender.sendMessage(Utils.message(noPermMessage));
            return true;
        }

        plugin.reloadPluginConfiguration();

        String successMessage = plugin.getPluginConfig().getMsgReloadSuccess().replace("{prefix}", plugin.getPluginConfig().getMsgPrefix());
        sender.sendMessage(Utils.message(successMessage));

        return true;
    }
}