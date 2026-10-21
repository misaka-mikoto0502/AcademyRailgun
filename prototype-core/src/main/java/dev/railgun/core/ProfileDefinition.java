package dev.railgun.core;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/** Optional identity/content grouping. Membership does not automatically grant a skill. */
public final class ProfileDefinition {
    public final String id;
    public final Set<String> abilityIds;
    public final Optional<String> appearanceId;

    public ProfileDefinition(String id, Set<String> abilityIds, String appearanceId) {
        this.id = Ids.check(id);
        Set<String> copy = new LinkedHashSet<>();
        abilityIds.forEach(value -> copy.add(Ids.check(value)));
        this.abilityIds = Collections.unmodifiableSet(copy);
        this.appearanceId = Optional.ofNullable(appearanceId).map(Ids::check);
    }
}
