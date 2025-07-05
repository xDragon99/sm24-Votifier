package pl.serweryminecraft24.sm24VoterJava.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.serweryminecraft24.sm24VoterJava.Sm24VoterJava;
import pl.serweryminecraft24.sm24VoterJava.Utils;
import pl.serweryminecraft24.sm24VoterJava.config.PluginConfig;
import pl.serweryminecraft24.sm24VoterJava.service.VoteApiService;

import java.util.Optional;
import java.util.logging.Level;

public final class VoteCommand implements CommandExecutor {

    private final Sm24VoterJava plugin;
    private final PluginConfig config;
    private final VoteApiService voteApiService;
    private final String helpLink = "https://serweryminecraft24.pl/konfiguracja-pluginu";

    public VoteCommand(Sm24VoterJava plugin, PluginConfig config, VoteApiService voteApiService) {
        this.plugin = plugin;
        this.config = config;
        this.voteApiService = voteApiService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (config.getApiToken().equalsIgnoreCase("tutaj_wpisz_token") || config.getApiToken().isBlank()) {

            String line1 = config.getMsgInvalidTokenLine1().replace("{prefix}", config.getMsgPrefix());
            String line2 = config.getMsgInvalidTokenLine2().replace("{prefix}", config.getMsgPrefix());
            Utils.notifySenderWithClickableLink(sender, line1, null);
            Utils.notifySenderWithClickableLink(sender, line2, helpLink);
            return true;
        }

        Optional<String> cachedLinkOpt = config.getCachedVoteLink();

        cachedLinkOpt.ifPresentOrElse(

                cachedLink -> {

                    String message = config.getMsgVoteLinkInfo().replace("{prefix}", config.getMsgPrefix());
                    Utils.notifySenderWithClickableLink(sender, message, cachedLink);
                },

                () -> {

                    String fetchingMessage = config.getMsgVoteFetchingLink().replace("{prefix}", config.getMsgPrefix());
                    Utils.notifySenderWithClickableLink(sender, fetchingMessage, null);

                    voteApiService.fetchVoteLink()
                            .thenAccept(apiResponse -> {
                                if (apiResponse.isSuccess()) {
                                    String successMessage = config.getMsgVoteLinkSuccess().replace("{prefix}", config.getMsgPrefix());
                                    String fetchedLink = apiResponse.getMessage();

                                    plugin.cacheVoteLink(fetchedLink, config.getApiToken());

                                    Utils.notifySenderWithClickableLink(sender, successMessage, fetchedLink);
                                } else {
                                    String errorMessage = config.getMsgRewardApiError()
                                            .replace("{prefix}", config.getMsgPrefix())
                                            .replace("{message}", apiResponse.getMessage());
                                    Utils.notifySenderWithClickableLink(sender, errorMessage, null);
                                }
                            })
                            .exceptionally(error -> {
                                plugin.getLogger().log(Level.SEVERE, "Błąd w komendzie /sm24-glosuj", error);
                                String errorMessage = config.getMsgInternalError().replace("{prefix}", config.getMsgPrefix());
                                Utils.notifySenderWithClickableLink(sender, errorMessage, null);
                                return null;
                            });
                }
        );

        return true;
    }
}