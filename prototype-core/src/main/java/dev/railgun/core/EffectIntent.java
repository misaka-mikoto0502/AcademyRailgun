package dev.railgun.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Server-produced request for a game adapter, never an accepted client command. */
public final class EffectIntent {
    public final String type;
    public final Map<String, String> parameters;

    public EffectIntent(String type, Map<String, String> parameters) {
        this.type = Ids.check(type);
        Map<String, String> copy = new LinkedHashMap<>();
        parameters.forEach((key, value) -> copy.put(Objects.requireNonNull(key),
                Objects.requireNonNull(value)));
        this.parameters = Collections.unmodifiableMap(copy);
    }
}
