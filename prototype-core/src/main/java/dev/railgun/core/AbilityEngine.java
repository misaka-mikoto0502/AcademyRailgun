package dev.railgun.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Server-side core; adapters must bind requests to their actual sender and server thread. */
public final class AbilityEngine {
    private final Catalog catalog;
    private final Rules rules;
    private final Map<String, GameAdapters.AbilityBehavior> behaviors;

    public AbilityEngine(Catalog catalog, Rules rules,
                         Map<String, GameAdapters.AbilityBehavior> behaviors) {
        this.catalog = Objects.requireNonNull(catalog);
        this.rules = Objects.requireNonNull(rules);
        Map<String, GameAdapters.AbilityBehavior> copy = new LinkedHashMap<>();
        behaviors.forEach((id, behavior) -> {
            if (!catalog.ability(id).isPresent()) {
                throw new IllegalArgumentException("Behavior has no definition: " + id);
            }
            copy.put(id, Objects.requireNonNull(behavior));
        });
        this.behaviors = Collections.unmodifiableMap(copy);
    }

    public Outcome select(PlayerState state, String slot, String profileId) {
        Ids.check(slot);
        ProfileDefinition profile = catalog.profile(profileId).orElse(null);
        if (profile == null) return Outcome.reject("unknown_profile");
        String rejected = rules.selectionRejection(state.snapshot(), slot, profile);
        if (rejected != null) return Outcome.reject(rejected);
        state.select(slot, profile.id);
        return Outcome.accept();
    }

    public Outcome learn(PlayerState state, String abilityId) {
        AbilityDefinition ability = catalog.ability(abilityId).orElse(null);
        if (ability == null) return Outcome.reject("unknown_ability");
        PlayerState.Snapshot before = state.snapshot();
        if (before.rank(abilityId) >= ability.maxRank) return Outcome.reject("max_rank");
        for (Map.Entry<String, Integer> prerequisite : ability.prerequisites.entrySet()) {
            if (before.rank(prerequisite.getKey()) < prerequisite.getValue()) {
                return Outcome.reject("missing_prerequisite");
            }
        }
        String rejected = rules.learningRejection(before, ability, before.rank(abilityId) + 1);
        if (rejected != null) return Outcome.reject(rejected);
        if (!canAfford(before, ability.learnCosts)) return Outcome.reject("insufficient_resources");
        debit(state, before, ability.learnCosts);
        state.setRank(abilityId, before.rank(abilityId) + 1);
        return Outcome.accept();
    }

    public Outcome cast(PlayerState state, String abilityId, long serverTick) {
        if (serverTick < 0) throw new IllegalArgumentException("Negative server tick");
        AbilityDefinition ability = catalog.ability(abilityId).orElse(null);
        if (ability == null) return Outcome.reject("unknown_ability");
        PlayerState.Snapshot before = state.snapshot();
        if (before.rank(abilityId) == 0) return Outcome.reject("not_learned");
        if (before.readyAt(abilityId) > serverTick) return Outcome.reject("cooldown");
        String rejected = rules.castingRejection(before, ability, serverTick);
        if (rejected != null) return Outcome.reject(rejected);
        if (!canAfford(before, ability.castCosts)) return Outcome.reject("insufficient_resources");
        GameAdapters.AbilityBehavior behavior = behaviors.get(abilityId);
        if (behavior == null) return Outcome.reject("no_behavior");
        long deadline = Math.addExact(serverTick, ability.cooldownTicks);
        Outcome prepared = Objects.requireNonNull(behavior.prepare(before, ability, serverTick));
        if (!prepared.accepted) return prepared;
        debit(state, before, ability.castCosts);
        state.setCooldown(abilityId, deadline);
        return prepared;
    }

    /** Trusted adapter operation, also usable for regeneration, rewards and administrative changes. */
    public void adjustResource(PlayerState state, String resourceId, long delta) {
        Ids.check(resourceId);
        long value = Math.addExact(state.snapshot().resource(resourceId), delta);
        if (value < 0) throw new IllegalArgumentException("Resource would become negative");
        state.setResource(resourceId, value);
    }

    public void progress(PlayerState state, String eventId, Map<String, String> serverFacts,
                         GameAdapters.ProgressionPolicy policy) {
        Ids.check(eventId);
        Map<String, String> facts = new LinkedHashMap<>();
        serverFacts.forEach((key, value) -> facts.put(Objects.requireNonNull(key),
                Objects.requireNonNull(value)));
        PlayerState.Snapshot before = state.snapshot();
        Map<String, Long> rewards = Ids.amounts(policy.rewards(before, eventId,
                Collections.unmodifiableMap(facts)));
        Map<String, Long> balances = new LinkedHashMap<>();
        rewards.forEach((id, amount) -> balances.put(id, Math.addExact(before.resource(id), amount)));
        balances.forEach(state::setResource);
    }

    private static boolean canAfford(PlayerState.Snapshot player, Map<String, Long> costs) {
        return costs.entrySet().stream().allMatch(entry ->
                player.resource(entry.getKey()) >= entry.getValue());
    }

    private static void debit(PlayerState state, PlayerState.Snapshot before, Map<String, Long> costs) {
        costs.forEach((id, cost) -> state.setResource(id, before.resource(id) - cost));
    }
}
