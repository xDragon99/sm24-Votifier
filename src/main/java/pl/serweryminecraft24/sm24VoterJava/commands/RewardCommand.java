package pl.serweryminecraft24.sm24VoterJava.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.serweryminecraft24.sm24VoterJava.Sm24VoterJava;
import pl.serweryminecraft24.sm24VoterJava.Utils;
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
    private static final String HELP_LINK = "https://serweryminecraft24.pl/konfiguracja-pluginu";

    public RewardCommand(Sm24VoterJava plugin, PluginConfig config, CooldownService cooldownService, 
                        RewardService rewardService, VoteApiService voteApiService) {
        this.plugin = plugin;
        this.config = config;
        this.cooldownService = cooldownService;
        this.rewardService = rewardService;
        this.voteApiService = voteApiService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.message(config.getMsgPlayerOnlyCommand().replace("{prefix}", config.getMsgPrefix())));
            return true;
        }

        String apiToken = config.getApiToken();
        if (apiToken.isBlank() || apiToken.equalsIgnoreCase("tutaj_wpisz_token")) {
            Utils.notifySenderWithClickableLink(player, 
                config.getMsgInvalidTokenLine1().replace("{prefix}", config.getMsgPrefix()), null);
            Utils.notifySenderWithClickableLink(player, 
                config.getMsgInvalidTokenLine2().replace("{prefix}", config.getMsgPrefix()), HELP_LINK);
            return true;
        }

        if (config.isPermissionRequired() && !player.hasPermission(config.getPermissionNode())) {
            Utils.notifySenderWithClickableLink(player, 
                config.getMsgNoPermission().replace("{prefix}", config.getMsgPrefix()), null);
            return true;
        }

        if (cooldownService.isOnCooldown(player.getUniqueId())) {
            String message = config.getMsgRewardOnCooldown()
                .replace("{prefix}", config.getMsgPrefix())
                .replace("{seconds}", String.valueOf(cooldownService.getRemainingCooldown(player.getUniqueId()).toSeconds() + 1));
            Utils.notifySenderWithClickableLink(player, message, null);
            return true;
        }

        Utils.notifySenderWithClickableLink(player, 
            config.getMsgRewardVerifyingVote().replace("{prefix}", config.getMsgPrefix()), null);

        voteApiService.verifyVote(player).thenAccept(apiResponse -> plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (apiResponse == null) {
                Utils.notifySenderWithClickableLink(player, 
                    config.getMsgInternalError().replace("{prefix}", config.getMsgPrefix()), null);
                return;
            }

            if (apiResponse.success()) {
                rewardService.issueReward(player);
                cooldownService.setCooldown(player.getUniqueId());
            } else {
                String errorMessage = config.getMsgRewardApiError()
                    .replace("{prefix}", config.getMsgPrefix())
                    .replace("{message}", apiResponse.message());
                Utils.notifySenderWithClickableLink(player, errorMessage, null);
            }
        })).exceptionally(error -> {
            plugin.getLogger().log(Level.SEVERE, "Błąd podczas weryfikacji głosu dla " + player.getName(), error);
            plugin.getServer().getScheduler().runTask(plugin, () -> 
                Utils.notifySenderWithClickableLink(player, 
                    config.getMsgInternalError().replace("{prefix}", config.getMsgPrefix()), null));
            return null;
        });

        return true;
    }
}
