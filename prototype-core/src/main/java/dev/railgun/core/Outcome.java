package dev.railgun.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Outcome {
    public final boolean accepted;
    public final String code;
    public final List<EffectIntent> effects;

    private Outcome(boolean accepted, String code, List<EffectIntent> effects) {
        this.accepted = accepted;
        this.code = Objects.requireNonNull(code);
        List<EffectIntent> copy = new ArrayList<>(effects);
        copy.forEach(Objects::requireNonNull);
        this.effects = Collections.unmodifiableList(copy);
        if (!accepted && !effects.isEmpty()) {
            throw new IllegalArgumentException("Rejected actions cannot have effects");
        }
    }

    public static Outcome accept(List<EffectIntent> effects) {
        return new Outcome(true, "accepted", effects);
    }

    public static Outcome accept() { return accept(Collections.emptyList()); }

    public static Outcome reject(String code) {
        return new Outcome(false, code, Collections.emptyList());
    }
}
