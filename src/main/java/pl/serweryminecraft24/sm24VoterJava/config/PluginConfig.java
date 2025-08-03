package pl.serweryminecraft24.sm24VoterJava.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public final class PluginConfig {
    private final String apiToken;
    private final boolean isPermissionRequired;
    private final String permissionNode;
    private final Duration rewardCooldown;
    private final List<String> rewardCommands;
    private final String cachedVoteLink;
    private final String cachedVoteToken;
    private final String msgPrefix;
    private final String msgNoPermission;
    private final String msgPlayerOnlyCommand;
    private final String msgInternalError;
    private final String msgInvalidTokenLine1;
    private final String msgInvalidTokenLine2;
    private final String msgRewardOnCooldown;
    private final String msgRewardVerifyingVote;
    private final String msgRewardApiError;
    private final String msgVoteFetchingLink;
    private final String msgVoteLinkInfo;
    private final String msgVoteLinkSuccess;
    private final String msgTestNotOp;
    private final String msgTestInfo;
    private final String msgReloadSuccess;

    public PluginConfig(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        
        this.apiToken = config.getString("api.server-token", "not_configured");
        this.isPermissionRequired = config.getBoolean("rewards.permission.required", false);
        this.permissionNode = config.getString("rewards.permission.node", "sm24.reward.claim");
        this.rewardCommands = config.getStringList("rewards.commands");
        this.cachedVoteLink = config.getString("vote-cache.link", "");
        this.cachedVoteToken = config.getString("vote-cache.token", "");

        long cooldownSeconds = Math.max(config.getLong("rewards.cooldown-seconds", 60), 60);
        if (cooldownSeconds == 60 && config.getLong("rewards.cooldown-seconds", 60) < 60) {
            plugin.getLogger().warning("Wartość 'rewards.cooldown-seconds' poniżej 60s. Ustawiono 60 sekund.");
        }
        this.rewardCooldown = Duration.ofSeconds(cooldownSeconds);

        this.msgPrefix = config.getString("messages.prefix", "&8[&6SM24&8] &r");
        this.msgNoPermission = config.getString("messages.no-permission", "{prefix}&cNie posiadasz wymaganych uprawnień.");
        this.msgPlayerOnlyCommand = config.getString("messages.player-only-command", "{prefix}&cTej komendy może użyć tylko gracz.");
        this.msgInternalError = config.getString("messages.internal-error", "{prefix}&cWystąpił wewnętrzny błąd. Skontaktuj się z administratorem.");
        this.msgInvalidTokenLine1 = config.getString("messages.invalid-token.line1", "{prefix}&cBrak poprawnego tokena serwera w konfiguracji!");
        this.msgInvalidTokenLine2 = config.getString("messages.invalid-token.line2", "{prefix}&cSzczegóły konfiguracji znajdziesz pod adresem:");
        this.msgRewardOnCooldown = config.getString("messages.reward.on-cooldown", "{prefix}&cMożesz odebrać nagrodę ponownie za &e{seconds} &csekund.");
        this.msgRewardVerifyingVote = config.getString("messages.reward.verifying-vote", "{prefix}&aWeryfikujemy Twój głos, proszę czekać...");
        this.msgRewardApiError = config.getString("messages.reward.api-error", "{prefix}&c{message}");
        this.msgVoteFetchingLink = config.getString("messages.vote.fetching-link", "{prefix}&aPobieranie linku do głosowania...");
        this.msgVoteLinkInfo = config.getString("messages.vote.link-info", "{prefix}&aTwój link do głosowania:");
        this.msgVoteLinkSuccess = config.getString("messages.vote.link-success", "{prefix}&aZagłosuj na serwer tutaj -> ");
        this.msgTestNotOp = config.getString("messages.test.not-op", "{prefix}&cTa komenda jest dostępna tylko dla operatorów serwera.");
        this.msgTestInfo = config.getString("messages.test.info", "{prefix}&aUruchomiono testowe przyznawanie nagrody...");
        this.msgReloadSuccess = config.getString("messages.reload-success", "{prefix}&aKonfiguracja przeładowana!");
    }

    public String getApiToken() { return apiToken; }
    public boolean isPermissionRequired() { return isPermissionRequired; }
    public String getPermissionNode() { return permissionNode; }
    public Duration getRewardCooldown() { return rewardCooldown; }
    public List<String> getRewardCommands() { return rewardCommands; }
    public Optional<String> getCachedVoteLink() { return cachedVoteLink.isBlank() ? Optional.empty() : Optional.of(cachedVoteLink); }
    public Optional<String> getCachedVoteToken() { return cachedVoteToken.isBlank() ? Optional.empty() : Optional.of(cachedVoteToken); }
    public String getMsgPrefix() { return msgPrefix; }
    public String getMsgNoPermission() { return msgNoPermission; }
    public String getMsgPlayerOnlyCommand() { return msgPlayerOnlyCommand; }
    public String getMsgInternalError() { return msgInternalError; }
    public String getMsgInvalidTokenLine1() { return msgInvalidTokenLine1; }
    public String getMsgInvalidTokenLine2() { return msgInvalidTokenLine2; }
    public String getMsgRewardOnCooldown() { return msgRewardOnCooldown; }
    public String getMsgRewardVerifyingVote() { return msgRewardVerifyingVote; }
    public String getMsgRewardApiError() { return msgRewardApiError; }
    public String getMsgVoteFetchingLink() { return msgVoteFetchingLink; }
    public String getMsgVoteLinkInfo() { return msgVoteLinkInfo; }
    public String getMsgVoteLinkSuccess() { return msgVoteLinkSuccess; }
    public String getMsgTestNotOp() { return msgTestNotOp; }
    public String getMsgTestInfo() { return msgTestInfo; }
    public String getMsgReloadSuccess() { return msgReloadSuccess; }
}
