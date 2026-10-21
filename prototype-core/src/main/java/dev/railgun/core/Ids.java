package dev.railgun.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class Ids {
    private Ids() { }

    static String check(String id) {
        if (id == null || !id.matches("[a-z][a-z0-9_.-]*:[a-z0-9_./-]+")) {
            throw new IllegalArgumentException("Expected a stable namespaced id: " + id);
        }
        return id;
    }

    static Map<String, Long> amounts(Map<String, Long> values) {
        Map<String, Long> copy = new LinkedHashMap<>();
        values.forEach((id, amount) -> {
            if (amount == null || amount < 0) {
                throw new IllegalArgumentException("Negative or null amount: " + id);
            }
            copy.put(check(id), amount);
        });
        return Collections.unmodifiableMap(copy);
    }

    static Map<String, Integer> ranks(Map<String, Integer> values) {
        Map<String, Integer> copy = new LinkedHashMap<>();
        values.forEach((id, rank) -> {
            if (rank == null || rank < 1) {
                throw new IllegalArgumentException("Rank must be positive: " + id);
            }
            copy.put(check(id), rank);
        });
        return Collections.unmodifiableMap(copy);
    }
}
