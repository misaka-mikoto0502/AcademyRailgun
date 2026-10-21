package dev.railgun.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Validated, immutable definitions. Construct a new catalog when reloading content. */
public final class Catalog {
    private final Map<String, AbilityDefinition> abilities;
    private final Map<String, ProfileDefinition> profiles;

    public Catalog(Collection<AbilityDefinition> abilities, Collection<ProfileDefinition> profiles) {
        Map<String, AbilityDefinition> abilityMap = new LinkedHashMap<>();
        for (AbilityDefinition ability : abilities) {
            if (abilityMap.putIfAbsent(ability.id, ability) != null) {
                throw new IllegalArgumentException("Duplicate ability: " + ability.id);
            }
        }
        for (AbilityDefinition ability : abilities) {
            ability.prerequisites.forEach((id, rank) -> {
                AbilityDefinition dependency = abilityMap.get(id);
                if (dependency == null || rank > dependency.maxRank) {
                    throw new IllegalArgumentException("Unreachable prerequisite: " + id);
                }
            });
        }
        Set<String> visited = new HashSet<>();
        for (String id : abilityMap.keySet()) {
            visit(id, abilityMap, new HashSet<>(), visited);
        }
        Map<String, ProfileDefinition> profileMap = new LinkedHashMap<>();
        for (ProfileDefinition profile : profiles) {
            if (profileMap.putIfAbsent(profile.id, profile) != null) {
                throw new IllegalArgumentException("Duplicate profile: " + profile.id);
            }
            for (String id : profile.abilityIds) {
                if (!abilityMap.containsKey(id)) {
                    throw new IllegalArgumentException("Unknown profile ability: " + id);
                }
            }
        }
        this.abilities = Collections.unmodifiableMap(abilityMap);
        this.profiles = Collections.unmodifiableMap(profileMap);
    }

    private static void visit(String id, Map<String, AbilityDefinition> definitions,
                              Set<String> visiting, Set<String> visited) {
        if (visited.contains(id)) return;
        if (!visiting.add(id)) throw new IllegalArgumentException("Prerequisite cycle: " + id);
        for (String prerequisite : definitions.get(id).prerequisites.keySet()) {
            visit(prerequisite, definitions, visiting, visited);
        }
        visiting.remove(id);
        visited.add(id);
    }

    public Optional<AbilityDefinition> ability(String id) {
        return Optional.ofNullable(abilities.get(id));
    }

    public Optional<ProfileDefinition> profile(String id) {
        return Optional.ofNullable(profiles.get(id));
    }

    public Map<String, AbilityDefinition> abilities() { return abilities; }
}
