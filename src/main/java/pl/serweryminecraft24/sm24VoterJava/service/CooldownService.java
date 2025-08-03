package pl.serweryminecraft24.sm24VoterJava.service;

import pl.serweryminecraft24.sm24VoterJava.config.PluginConfig;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CooldownService {
    private final Map<UUID, Instant> cooldowns = new ConcurrentHashMap<>();
    private final long cooldownSeconds;

    public CooldownService(PluginConfig config) {
        this.cooldownSeconds = config.getRewardCooldown().getSeconds();
    }

    public boolean isOnCooldown(UUID playerId) {
        Instant lastUsage = cooldowns.get(playerId);
        if (lastUsage == null) {
            return false;
        }
        return Instant.now().getEpochSecond() < lastUsage.getEpochSecond() + cooldownSeconds;
    }

    public Duration getRemainingCooldown(UUID playerId) {
        Instant lastUsage = cooldowns.get(playerId);
        if (lastUsage == null) {
            return Duration.ZERO;
        }

        long secondsLeft = lastUsage.getEpochSecond() + cooldownSeconds - Instant.now().getEpochSecond();
        return secondsLeft <= 0 ? Duration.ZERO : Duration.ofSeconds(secondsLeft);
    }

    public void setCooldown(UUID playerId) {
        cooldowns.put(playerId, Instant.now());
    }

    public void clearExpiredCooldowns() {
        long now = Instant.now().getEpochSecond();
        cooldowns.entrySet().removeIf(entry -> now >= entry.getValue().getEpochSecond() + cooldownSeconds);
    }
}
