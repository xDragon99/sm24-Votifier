package pl.serweryminecraft24.sm24VoterJava.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.serweryminecraft24.sm24VoterJava.Utils;
import pl.serweryminecraft24.sm24VoterJava.config.PluginConfig;
import pl.serweryminecraft24.sm24VoterJava.service.RewardService;

public final class TestCommand implements CommandExecutor {

    private final PluginConfig config;
    private final RewardService rewardService;

    public TestCommand(PluginConfig config, RewardService rewardService) {
        this.config = config;
        this.rewardService = rewardService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            String message = config.getMsgPlayerOnlyCommand().replace("{prefix}", config.getMsgPrefix());
            sender.sendMessage(Utils.message(message));
            return true;
        }
        final Player player = (Player) sender; // Jawne rzutowanie


        if (!player.isOp()) {
            String message = config.getMsgTestNotOp().replace("{prefix}", config.getMsgPrefix());
            Utils.notifySenderWithClickableLink(player, message, null);
            return true;
        }

        if (config.isPermissionRequired() && !player.hasPermission(config.getPermissionNode())) {
            String message = config.getMsgNoPermission().replace("{prefix}", config.getMsgPrefix());
            Utils.notifySenderWithClickableLink(player, message, null);
            return true;
        }

        String message = config.getMsgTestInfo().replace("{prefix}", config.getMsgPrefix());
        Utils.notifySenderWithClickableLink(player, message, null);

        rewardService.issueReward(player);

        return true;
    }
}