package pl.serweryminecraft24.sm24VoterJava.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.serweryminecraft24.sm24VoterJava.Sm24VoterJava;
import pl.serweryminecraft24.sm24VoterJava.Utils;
import pl.serweryminecraft24.sm24VoterJava.config.PluginConfig;
import pl.serweryminecraft24.sm24VoterJava.service.VoteApiService;
import java.util.logging.Level;

public final class VoteCommand implements CommandExecutor {
    private final Sm24VoterJava plugin;
    private final PluginConfig config;
    private final VoteApiService voteApiService;
    private static final String HELP_LINK = "https://serweryminecraft24.pl/konfiguracja-pluginu";

    public VoteCommand(Sm24VoterJava plugin, PluginConfig config, VoteApiService voteApiService) {
        this.plugin = plugin;
        this.config = config;
        this.voteApiService = voteApiService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String apiToken = config.getApiToken();
        if (apiToken.isBlank() || apiToken.equalsIgnoreCase("tutaj_wpisz_token")) {
            Utils.notifySenderWithClickableLink(sender, 
                config.getMsgInvalidTokenLine1().replace("{prefix}", config.getMsgPrefix()), null);
            Utils.notifySenderWithClickableLink(sender, 
                config.getMsgInvalidTokenLine2().replace("{prefix}", config.getMsgPrefix()), HELP_LINK);
            return true;
        }

        config.getCachedVoteLink().ifPresentOrElse(
            cachedLink -> Utils.notifySenderWithClickableLink(sender, 
                config.getMsgVoteLinkInfo().replace("{prefix}", config.getMsgPrefix()), cachedLink),
            () -> {
                Utils.notifySenderWithClickableLink(sender, 
                    config.getMsgVoteFetchingLink().replace("{prefix}", config.getMsgPrefix()), null);
                
                voteApiService.fetchVoteLink().thenAccept(apiResponse -> 
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        if (apiResponse.success()) {
                            String fetchedLink = apiResponse.message();
                            plugin.cacheVoteLink(fetchedLink, apiToken);
                            Utils.notifySenderWithClickableLink(sender, 
                                config.getMsgVoteLinkSuccess().replace("{prefix}", config.getMsgPrefix()), fetchedLink);
                        } else {
                            Utils.notifySenderWithClickableLink(sender, 
                                config.getMsgRewardApiError()
                                    .replace("{prefix}", config.getMsgPrefix())
                                    .replace("{message}", apiResponse.message()), null);
                        }
                    })
                ).exceptionally(error -> {
                    plugin.getLogger().log(Level.SEVERE, "Błąd w komendzie /sm24-glosuj", error);
                    plugin.getServer().getScheduler().runTask(plugin, () -> 
                        Utils.notifySenderWithClickableLink(sender, 
                            config.getMsgInternalError().replace("{prefix}", config.getMsgPrefix()), null));
                    return null;
                });
            }
        );

        return true;
    }
}
