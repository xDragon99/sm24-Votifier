package pl.serweryminecraft24.sm24VoterJava.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Klasa wczytująca i przechowująca wszystkie wartości z pliku config.yml.
 * Zapewnia bezpieczny i silnie typowany dostęp do konfiguracji.
 */
public final class PluginConfig {

    // --- Pola Głównej Konfiguracji ---
    private final String apiToken;
    private final boolean isPermissionRequired;
    private final String permissionNode;
    private final Duration rewardCooldown;
    private final List<String> rewardCommands;


    private final String cachedVoteLink;
    private final String cachedVoteToken;

    // --- Pola na Wiadomości ---
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
        // --- Wczytywanie Głównej Konfiguracji ---
        this.apiToken = plugin.getConfig().getString("api.server-token", "not_configured");
        this.isPermissionRequired = plugin.getConfig().getBoolean("rewards.permission.required", false);
        this.permissionNode = plugin.getConfig().getString("rewards.permission.node", "sm24.reward.claim");
        this.rewardCommands = plugin.getConfig().getStringList("rewards.commands");


        this.cachedVoteLink = plugin.getConfig().getString("vote-cache.link");
        this.cachedVoteToken = plugin.getConfig().getString("vote-cache.token");

        // --- Walidacja Cooldownu ---
        long cooldownSeconds = plugin.getConfig().getLong("rewards.cooldown-seconds", 60);
        if (cooldownSeconds < 60) {
            plugin.getLogger().warning("Wartość 'rewards.cooldown-seconds' w config.yml (" + cooldownSeconds + "s) jest poniżej wymaganego minimum (60s). Użyto wartości 60 sekund.");
            cooldownSeconds = 60;
        }
        this.rewardCooldown = Duration.ofSeconds(cooldownSeconds);

        // --- Wczytywanie Wiadomości ---
        this.msgPrefix = plugin.getConfig().getString("messages.prefix", "&8[&6SM24&8] &r");
        this.msgNoPermission = plugin.getConfig().getString("messages.no-permission", "{prefix}&cNie posiadasz wymaganych uprawnień.");
        this.msgPlayerOnlyCommand = plugin.getConfig().getString("messages.player-only-command", "{prefix}&cTej komendy może użyć tylko gracz.");
        this.msgInternalError = plugin.getConfig().getString("messages.internal-error", "{prefix}&cWystąpił wewnętrzny błąd. Skontaktuj się z administratorem.");
        this.msgInvalidTokenLine1 = plugin.getConfig().getString("messages.invalid-token.line1", "{prefix}&cBrak poprawnego tokena serwera w konfiguracji!");
        this.msgInvalidTokenLine2 = plugin.getConfig().getString("messages.invalid-token.line2", "{prefix}&cSzczegóły konfiguracji znajdziesz pod adresem:");
        this.msgRewardOnCooldown = plugin.getConfig().getString("messages.reward.on-cooldown", "{prefix}&cMożesz odebrać nagrodę ponownie za &e{seconds} &csekund.");
        this.msgRewardVerifyingVote = plugin.getConfig().getString("messages.reward.verifying-vote", "{prefix}&aWeryfikujemy Twój głos, proszę czekać...");
        this.msgRewardApiError = plugin.getConfig().getString("messages.reward.api-error", "{prefix}&c{message}");
        this.msgVoteFetchingLink = plugin.getConfig().getString("messages.vote.fetching-link", "{prefix}&aPobieranie linku do głosowania...");
        this.msgVoteLinkInfo = plugin.getConfig().getString("messages.vote.link-info", "{prefix}&aTwój link do głosowania:");
        this.msgVoteLinkSuccess = plugin.getConfig().getString("messages.vote.link-success", "{prefix}&aZagłosuj na serwer tutaj -> ");
        this.msgTestNotOp = plugin.getConfig().getString("messages.test.not-op", "{prefix}&cTa komenda jest dostępna tylko dla operatorów serwera.");
        this.msgTestInfo = plugin.getConfig().getString("messages.test.info", "{prefix}&aUruchomiono testowe przyznawanie nagrody...");
        this.msgReloadSuccess = plugin.getConfig().getString("messages.reload-success", "{prefix}&aKonfiguracja przeładowana!");

    }

    // --- Gettery dla Głównej Konfiguracji ---
    public String getApiToken() { return apiToken; }
    public boolean isPermissionRequired() { return isPermissionRequired; }
    public String getPermissionNode() { return permissionNode; }
    public Duration getRewardCooldown() { return rewardCooldown; }
    public List<String> getRewardCommands() { return rewardCommands; }
    public Optional<String> getCachedVoteLink() {
        if (cachedVoteLink == null || cachedVoteLink.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(cachedVoteLink);
    }

    public Optional<String> getCachedVoteToken() {
        if (cachedVoteToken == null || cachedVoteToken.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(cachedVoteToken);
    }

    // --- Gettery dla Wiadomości ---
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