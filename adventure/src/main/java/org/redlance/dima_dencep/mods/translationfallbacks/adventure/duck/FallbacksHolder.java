package org.redlance.dima_dencep.mods.translationfallbacks.adventure.duck;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("unused")
public interface FallbacksHolder {
    default void tf$set(@NotNull Map<String, String> fallbacks) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    default Map<String, String> tf$get() {
        throw new UnsupportedOperationException();
    }
}
