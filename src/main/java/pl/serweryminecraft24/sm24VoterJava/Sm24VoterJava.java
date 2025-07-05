package pl.serweryminecraft24.sm24VoterJava;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import pl.serweryminecraft24.sm24VoterJava.config.PluginConfig;
import pl.serweryminecraft24.sm24VoterJava.commands.ReloadCommand;
import pl.serweryminecraft24.sm24VoterJava.commands.RewardCommand;
import pl.serweryminecraft24.sm24VoterJava.commands.TestCommand;
import pl.serweryminecraft24.sm24VoterJava.commands.VoteCommand;
import pl.serweryminecraft24.sm24VoterJava.service.CooldownService;
import pl.serweryminecraft24.sm24VoterJava.service.RewardService;
import pl.serweryminecraft24.sm24VoterJava.service.VoteApiService;

public final class Sm24VoterJava extends JavaPlugin {


    private PluginConfig pluginConfig;
    private CooldownService cooldownService;
    private RewardService rewardService;
    private VoteApiService voteApiService;

    @Override
    public void onEnable() {
        Utils.init(this);

        reloadPluginConfiguration();
        getLogger().info("[SM24] Plugin został pomyślnie załadowany w nowej architekturze!");
    }


    public void reloadPluginConfiguration() {

        this.saveDefaultConfig();
        this.reloadConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();


        this.pluginConfig = new PluginConfig(this);


        final String currentToken = this.pluginConfig.getApiToken();

        this.pluginConfig.getCachedVoteToken().ifPresent(cachedToken -> {

            if (!cachedToken.equals(currentToken) && !currentToken.equalsIgnoreCase("tutaj_wpisz_token")) {
                getLogger().warning("Wykryto zmianę tokena serwera! Pamięć podręczna linku do głosowania została zresetowana.");

                this.cacheVoteLink(null, null);
                this.pluginConfig = new PluginConfig(this);
            }
        });

        this.cooldownService = new CooldownService(pluginConfig);
        this.rewardService = new RewardService(this, pluginConfig);
        this.voteApiService = new VoteApiService(pluginConfig);

        this.registerCommands();
        getLogger().info("[SM24] Konfiguracja została przeładowana.");
    }

    @Override
    public void onDisable() {
        getLogger().info("[SM24] Plugin został zatrzymany.");
    }


    public void cacheVoteLink(String link, String token) {
        this.getConfig().set("vote-cache.link", link);
        this.getConfig().set("vote-cache.token", token);
        this.saveConfig();
    }

    public PluginConfig getPluginConfig() {
        return this.pluginConfig;
    }

    private void registerCommands() {

        PluginCommand rewardCmd = getCommand("sm24-nagroda");
        if (rewardCmd != null) {
            rewardCmd.setExecutor(new RewardCommand(this, this.pluginConfig, this.cooldownService, this.rewardService, this.voteApiService));
        }
        PluginCommand voteCmd = getCommand("sm24-glosuj");
        if (voteCmd != null) {
            voteCmd.setExecutor(new VoteCommand(this, this.pluginConfig, this.voteApiService));
        }
        PluginCommand testCmd = getCommand("sm24-test");
        if (testCmd != null) {
            testCmd.setExecutor(new TestCommand(this.pluginConfig, this.rewardService));
        }
        PluginCommand reloadCmd = getCommand("sm24-reload");
        if (reloadCmd != null) {
            reloadCmd.setExecutor(new ReloadCommand(this));
        }
    }
}