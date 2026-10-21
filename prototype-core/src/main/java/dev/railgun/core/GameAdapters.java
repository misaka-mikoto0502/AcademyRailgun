package dev.railgun.core;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Contracts to implement after a Minecraft version and loader have been chosen. */
public final class GameAdapters {
    private GameAdapters() { }

    public interface PlayerRepository {
        Optional<PlayerState.Snapshot> load(UUID playerId);
        void save(PlayerState.Snapshot snapshot);
    }

    public interface Presentation {
        void syncOwner(PlayerState.Snapshot snapshot);
        void syncAppearance(UUID playerId, Map<String, String> selections);
        void publishEffects(UUID actor, Outcome acceptedAction);
    }

    /** Use one persistent server/world timeline, also after reload and restart. */
    public interface Clock { long serverTick(); }

    public interface ProgressionPolicy {
        /** Called for events verified by the server; rewards must be nonnegative. */
        Map<String, Long> rewards(PlayerState.Snapshot player, String eventId,
                                  Map<String, String> serverFacts);
    }

    public interface AbilityBehavior {
        /** Prepare intents without mutating the world or player. */
        Outcome prepare(PlayerState.Snapshot state, AbilityDefinition ability, long serverTick);
    }
}
