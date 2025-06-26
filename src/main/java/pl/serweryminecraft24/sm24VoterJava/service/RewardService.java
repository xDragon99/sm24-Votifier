package pl.serweryminecraft24.sm24VoterJava.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.serweryminecraft24.sm24VoterJava.config.PluginConfig;

import java.util.List;

public final class RewardService {

    private final JavaPlugin plugin;
    private final List<String> rewardCommands;

    public RewardService(JavaPlugin plugin, PluginConfig config) {
        this.plugin = plugin;
        this.rewardCommands = config.getRewardCommands();
    }

    public void issueReward(Player player) {
        String playerName = player.getName();
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (String commandTemplate : rewardCommands) {
                String commandToRun = commandTemplate.replace("{player}", playerName);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToRun);
            }
        });
    }
}