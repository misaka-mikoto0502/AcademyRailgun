package dev.railgun.verification;

import dev.railgun.core.AbilityDefinition;
import dev.railgun.core.AbilityEngine;
import dev.railgun.core.Catalog;
import dev.railgun.core.EffectIntent;
import dev.railgun.core.GameAdapters;
import dev.railgun.core.Outcome;
import dev.railgun.core.PlayerState;
import dev.railgun.core.ProfileDefinition;
import dev.railgun.core.Rules;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Synthetic content checks framework invariants, not a proposed gameplay design. */
public final class FrameworkScenario {
    private static final String BASIC = "demo:basic";
    private static final String ADVANCED = "demo:advanced";
    private static final String SLOT = "demo:identity";
    private static final String PROFILE = "demo:profile";
    private static final String POINTS = "demo:points";
    private static final String ENERGY = "demo:energy";

    public static void main(String[] args) {
        AbilityDefinition basic = definition(BASIC, Collections.emptyMap(), 1L, 0L);
        AbilityDefinition advanced = definition(ADVANCED, Collections.singletonMap(BASIC, 1), 2L, 5L);
        ProfileDefinition profile = new ProfileDefinition(PROFILE,
                new java.util.LinkedHashSet<>(Arrays.asList(BASIC, ADVANCED)), null);
        ProfileDefinition other = new ProfileDefinition("demo:other", Collections.emptySet(), null);
        Catalog catalog = new Catalog(Arrays.asList(basic, advanced), Arrays.asList(profile, other));
        Rules rules = new Rules() {
            @Override public String selectionRejection(PlayerState.Snapshot state, String slot,
                                                        ProfileDefinition chosen) { return null; }
            @Override public String learningRejection(PlayerState.Snapshot state,
                    AbilityDefinition ability, int nextRank) { return access(state, ability); }
            @Override public String castingRejection(PlayerState.Snapshot state,
                    AbilityDefinition ability, long tick) { return access(state, ability); }
            private String access(PlayerState.Snapshot state, AbilityDefinition ability) {
                ProfileDefinition selected = catalog.profile(state.selections.get(SLOT)).orElse(null);
                return selected != null && selected.abilityIds.contains(ability.id) ? null : "profile_denied";
            }
        };
        final int[] preparationMode = {0};
        GameAdapters.AbilityBehavior behavior = (state, ability, tick) -> {
            if (preparationMode[0] == 1) return Outcome.reject("target_rejected");
            if (preparationMode[0] == 2) throw new IllegalStateException("Synthetic preparation error");
            return Outcome.accept(Collections.singletonList(new EffectIntent("demo:effect",
                    Collections.singletonMap("source", state.playerId.toString()))));
        };
        AbilityEngine engine = new AbilityEngine(catalog, rules,
                Collections.singletonMap(ADVANCED, behavior));
        PlayerState player = new PlayerState(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        engine.adjustResource(player, POINTS, 4);
        engine.adjustResource(player, ENERGY, 20);

        rejected(engine.learn(player, BASIC), "profile_denied");
        accepted(engine.select(player, SLOT, PROFILE));
        rejected(engine.learn(player, ADVANCED), "missing_prerequisite");
        check(player.snapshot().resource(POINTS) == 4, "Rejected learning charged points");
        accepted(engine.learn(player, BASIC));
        accepted(engine.learn(player, ADVANCED));
        check(player.snapshot().resource(POINTS) == 1, "Learning costs not committed together");
        rejected(engine.learn(player, ADVANCED), "max_rank");
        System.out.println("PASS: profile rules, prerequisites and learning costs");

        accepted(engine.select(player, SLOT, other.id));
        rejected(engine.cast(player, ADVANCED, 100), "profile_denied");
        check(player.snapshot().rank(ADVANCED) == 1, "Selection silently reset abilities");
        accepted(engine.select(player, SLOT, PROFILE));
        rejected(engine.cast(player, BASIC, 100), "no_behavior");
        preparationMode[0] = 1;
        rejected(engine.cast(player, ADVANCED, 100), "target_rejected");
        preparationMode[0] = 2;
        throwsType(IllegalStateException.class, () -> engine.cast(player, ADVANCED, 100));
        check(player.snapshot().resource(ENERGY) == 20 && player.snapshot().readyAt(ADVANCED) == 0,
                "Failed preparation mutated resources or cooldown");
        System.out.println("PASS: denied and failed casts preserve state");

        preparationMode[0] = 0;
        Outcome cast = engine.cast(player, ADVANCED, 100);
        accepted(cast);
        check(cast.effects.size() == 1, "Prepared effect lost");
        check(player.snapshot().resource(ENERGY) == 15, "Cast cost not consumed");
        rejected(engine.cast(player, ADVANCED, 101), "cooldown");
        PlayerState.Snapshot saved = player.snapshot();
        PlayerState restored = PlayerState.restore(new PlayerState.Snapshot(saved.playerId,
                saved.selections, saved.ranks, saved.resources, saved.cooldowns));
        rejected(engine.cast(restored, ADVANCED, 109), "cooldown");
        accepted(engine.cast(restored, ADVANCED, 110));
        check(restored.snapshot().resource(ENERGY) == 10, "Restored balance incorrect");
        throwsType(UnsupportedOperationException.class, () -> saved.resources.put(ENERGY, 0L));
        System.out.println("PASS: effects, cooldown deadline, snapshot restoration and immutability");

        engine.progress(restored, "demo:event", Collections.emptyMap(),
                (state, event, facts) -> Collections.singletonMap(POINTS, 3L));
        check(restored.snapshot().resource(POINTS) == 4, "Progression policy not applied");
        engine.adjustResource(restored, "demo:overflow", Long.MAX_VALUE);
        Map<String, Long> overflowingRewards = new LinkedHashMap<>();
        overflowingRewards.put(POINTS, 10L);
        overflowingRewards.put("demo:overflow", 1L);
        throwsType(ArithmeticException.class, () -> engine.progress(restored, "demo:event",
                Collections.emptyMap(), (state, event, facts) -> overflowingRewards));
        check(restored.snapshot().resource(POINTS) == 4, "Partial reward committed on overflow");
        throwsType(ArithmeticException.class, () -> engine.cast(restored, ADVANCED, Long.MAX_VALUE));
        check(restored.snapshot().resource(ENERGY) == 10, "Overflowing cooldown charged energy");
        System.out.println("PASS: replaceable progression and atomic numeric validation");

        throwsType(IllegalArgumentException.class, () -> new Catalog(Arrays.asList(basic, basic),
                Collections.emptyList()));
        throwsType(IllegalArgumentException.class, () -> new Catalog(Collections.singletonList(
                definition("demo:orphan", Collections.singletonMap("demo:missing", 1), 0, 0)),
                Collections.emptyList()));
        throwsType(IllegalArgumentException.class, () -> new Catalog(Arrays.asList(
                definition("demo:a", Collections.singletonMap("demo:b", 1), 0, 0),
                definition("demo:b", Collections.singletonMap("demo:a", 1), 0, 0)),
                Collections.emptyList()));
        throwsType(IllegalArgumentException.class, () -> new Catalog(Collections.singletonList(basic),
                Collections.singletonList(new ProfileDefinition("demo:bad_profile",
                        Collections.singleton("demo:missing"), null))));
        System.out.println("PASS: duplicate, missing and cyclic content rejected");
        System.out.println("All framework scenarios passed.");
    }

    private static AbilityDefinition definition(String id, Map<String, Integer> prerequisites,
                                                 long learnCost, long castCost) {
        return new AbilityDefinition(id, 1, prerequisites, Collections.singletonMap(POINTS, learnCost),
                Collections.singletonMap(ENERGY, castCost), 10);
    }

    private static void accepted(Outcome outcome) { check(outcome.accepted, outcome.code); }
    private static void rejected(Outcome outcome, String code) {
        check(!outcome.accepted && code.equals(outcome.code), "Expected " + code + ": " + outcome.code);
    }
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    private static void throwsType(Class<? extends Throwable> type, Runnable action) {
        try { action.run(); }
        catch (Throwable error) {
            if (type.isInstance(error)) return;
            throw new AssertionError("Unexpected exception", error);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }
}
