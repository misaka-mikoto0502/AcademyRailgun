package dev.railgun.core;

/** Pure, side-effect-free extension points. Supply game rules explicitly. */
public interface Rules {
    /** Return null to allow, otherwise a stable rejection code. */
    String selectionRejection(PlayerState.Snapshot state, String slot, ProfileDefinition profile);

    String learningRejection(PlayerState.Snapshot state, AbilityDefinition ability, int nextRank);

    String castingRejection(PlayerState.Snapshot state, AbilityDefinition ability, long serverTick);
}
