package dev.railgun.core;

import java.util.Map;

/** Data only. Prerequisites are hard requirements; policies may add other rules. */
public final class AbilityDefinition {
    public final String id;
    public final int maxRank;
    public final Map<String, Integer> prerequisites;
    public final Map<String, Long> learnCosts;
    public final Map<String, Long> castCosts;
    public final long cooldownTicks;

    public AbilityDefinition(String id, int maxRank, Map<String, Integer> prerequisites,
                             Map<String, Long> learnCosts, Map<String, Long> castCosts,
                             long cooldownTicks) {
        this.id = Ids.check(id);
        if (maxRank < 1 || cooldownTicks < 0) {
            throw new IllegalArgumentException("Invalid rank or cooldown: " + id);
        }
        this.maxRank = maxRank;
        this.prerequisites = Ids.ranks(prerequisites);
        this.learnCosts = Ids.amounts(learnCosts);
        this.castCosts = Ids.amounts(castCosts);
        this.cooldownTicks = cooldownTicks;
    }
}
