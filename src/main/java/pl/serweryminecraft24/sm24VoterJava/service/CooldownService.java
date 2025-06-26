package pl.serweryminecraft24.sm24VoterJava.service;

import pl.serweryminecraft24.sm24VoterJava.config.PluginConfig;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serwis odpowiedzialny za zarządzanie cooldownami dla graczy.
 * Używa nowoczesnego API java.time do precyzyjnego i bezpiecznego zarządzania czasem.
 */
public final class CooldownService {


    private final Map<UUID, Instant> cooldowns = new ConcurrentHashMap<>();
    private final Duration cooldownDuration;

    public CooldownService(PluginConfig config) {

        this.cooldownDuration = config.getRewardCooldown();
    }


    public boolean isOnCooldown(UUID playerId) {
        Instant lastUsage = cooldowns.get(playerId);
        if (lastUsage == null) {

            return false;
        }

        Instant expirationTime = lastUsage.plus(this.cooldownDuration);

        return Instant.now().isBefore(expirationTime);
    }


    public Duration getRemainingCooldown(UUID playerId) {
        Instant lastUsage = cooldowns.get(playerId);
        if (lastUsage == null) {
            return Duration.ZERO;
        }

        Instant expirationTime = lastUsage.plus(this.cooldownDuration);
        Instant now = Instant.now();


        if (now.isAfter(expirationTime)) {
            return Duration.ZERO;
        }

        return Duration.between(now, expirationTime);
    }


    public void setCooldown(UUID playerId) {
        cooldowns.put(playerId, Instant.now());
    }
}