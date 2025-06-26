package pl.serweryminecraft24.sm24VoterJava.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.serweryminecraft24.sm24VoterJava.Sm24VoterJava;
import pl.serweryminecraft24.sm24VoterJava.Utils;
import pl.serweryminecraft24.sm24VoterJava.api.ApiResponse;
import pl.serweryminecraft24.sm24VoterJava.config.PluginConfig;
import pl.serweryminecraft24.sm24VoterJava.service.CooldownService;
import pl.serweryminecraft24.sm24VoterJava.service.RewardService;
import pl.serweryminecraft24.sm24VoterJava.service.VoteApiService;

import java.util.logging.Level;

public final class RewardCommand implements CommandExecutor {

    private final Sm24VoterJava plugin;
    private final PluginConfig config;
    private final CooldownService cooldownService;
    private final RewardService rewardService;
    private final VoteApiService voteApiService;
    private final String helpLink = "https://serweryminecraft24.pl/konfiguracja-pluginu";


    public RewardCommand(Sm24VoterJava plugin, PluginConfig config, CooldownService cooldownService, RewardService rewardService, VoteApiService voteApiService) {
        this.plugin = plugin;
        this.config = config;
        this.cooldownService = cooldownService;
        this.rewardService = rewardService;
        this.voteApiService = voteApiService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            String message = config.getMsgPlayerOnlyCommand().replace("{prefix}", config.getMsgPrefix());
            sender.sendMessage(Utils.message(message));
            return true;
        }
        final Player player = (Player) sender;


        if (config.getApiToken().equalsIgnoreCase("tutaj_wpisz_token") || config.getApiToken().isBlank()) {
            String line1 = config.getMsgInvalidTokenLine1().replace("{prefix}", config.getMsgPrefix());
            String line2 = config.getMsgInvalidTokenLine2().replace("{prefix}", config.getMsgPrefix());
            Utils.notifySenderWithClickableLink(sender, line1, null);
            Utils.notifySenderWithClickableLink(sender, line2, helpLink);
            return true;
        }

        if (config.isPermissionRequired() && !player.hasPermission(config.getPermissionNode())) {
            String message = config.getMsgNoPermission().replace("{prefix}", config.getMsgPrefix());
            Utils.notifySenderWithClickableLink(player, message, null);
            return true;
        }

        if (cooldownService.isOnCooldown(player.getUniqueId())) {
            long remainingSeconds = cooldownService.getRemainingCooldown(player.getUniqueId()).toSeconds() + 1;
            String message = config.getMsgRewardOnCooldown()
                    .replace("{prefix}", config.getMsgPrefix())
                    .replace("{seconds}", String.valueOf(remainingSeconds));
            Utils.notifySenderWithClickableLink(player, message, null);
            return true;
        }

        String verifyingMessage = config.getMsgRewardVerifyingVote().replace("{prefix}", config.getMsgPrefix());
        Utils.notifySenderWithClickableLink(player, verifyingMessage, null);

        voteApiService.verifyVote(player)
                .thenAccept(apiResponse -> {
                    if (apiResponse == null) {
                        String errorMessage = config.getMsgInternalError().replace("{prefix}", config.getMsgPrefix());
                        Utils.notifySenderWithClickableLink(player, errorMessage, null);
                        return;
                    }

                    if (apiResponse.isSuccess()) {
                        rewardService.issueReward(player);
                        cooldownService.setCooldown(player.getUniqueId());
                    } else {
                        String errorMessage = config.getMsgRewardApiError()
                                .replace("{prefix}", config.getMsgPrefix())
                                .replace("{message}", apiResponse.getMessage());
                        Utils.notifySenderWithClickableLink(player, errorMessage, null);
                    }
                })
                .exceptionally(error -> {
                    plugin.getLogger().log(Level.SEVERE, "Błąd podczas weryfikacji głosu dla " + player.getName(), error);
                    String errorMessage = config.getMsgInternalError().replace("{prefix}", config.getMsgPrefix());
                    Utils.notifySenderWithClickableLink(player, errorMessage, null);
                    return null;
                });

        return true;
    }
}