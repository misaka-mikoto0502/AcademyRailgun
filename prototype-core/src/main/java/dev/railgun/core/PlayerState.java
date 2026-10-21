package dev.railgun.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Mutated by the engine on the authoritative server thread only. */
public final class PlayerState {
    public final UUID playerId;
    private final Map<String, String> selections = new LinkedHashMap<>();
    private final Map<String, Integer> ranks = new LinkedHashMap<>();
    private final Map<String, Long> resources = new LinkedHashMap<>();
    private final Map<String, Long> cooldowns = new LinkedHashMap<>();

    public PlayerState(UUID playerId) { this.playerId = Objects.requireNonNull(playerId); }

    public Snapshot snapshot() {
        return new Snapshot(playerId, selections, ranks, resources, cooldowns);
    }

    public static PlayerState restore(Snapshot snapshot) {
        PlayerState state = new PlayerState(snapshot.playerId);
        state.selections.putAll(snapshot.selections);
        state.ranks.putAll(snapshot.ranks);
        state.resources.putAll(snapshot.resources);
        state.cooldowns.putAll(snapshot.cooldowns);
        return state;
    }

    void select(String slot, String profile) { selections.put(slot, profile); }
    void setRank(String id, int rank) { ranks.put(id, rank); }
    void setResource(String id, long amount) { resources.put(id, amount); }
    void setCooldown(String id, long deadline) { cooldowns.put(id, deadline); }

    public static final class Snapshot {
        public static final int SCHEMA_VERSION = 1;
        public final UUID playerId;
        public final Map<String, String> selections;
        public final Map<String, Integer> ranks;
        public final Map<String, Long> resources;
        public final Map<String, Long> cooldowns;

        public Snapshot(UUID playerId, Map<String, String> selections, Map<String, Integer> ranks,
                        Map<String, Long> resources, Map<String, Long> cooldowns) {
            this.playerId = Objects.requireNonNull(playerId);
            Map<String, String> selected = new LinkedHashMap<>();
            selections.forEach((slot, profile) -> selected.put(Ids.check(slot), Ids.check(profile)));
            this.selections = Collections.unmodifiableMap(selected);
            this.ranks = Ids.ranks(ranks);
            this.resources = Ids.amounts(resources);
            this.cooldowns = Ids.amounts(cooldowns);
        }

        public long resource(String id) { return resources.getOrDefault(id, 0L); }
        public int rank(String id) { return ranks.getOrDefault(id, 0); }
        public long readyAt(String id) { return cooldowns.getOrDefault(id, 0L); }
    }
}
