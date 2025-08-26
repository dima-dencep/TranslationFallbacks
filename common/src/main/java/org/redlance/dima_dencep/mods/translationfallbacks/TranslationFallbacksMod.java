package org.redlance.dima_dencep.mods.translationfallbacks;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redlance.dima_dencep.mods.translationfallbacks.ducks.FallbacksHolder;

import java.util.Locale;
import java.util.Map;

public class TranslationFallbacksMod {
    public static String mapFallback(@NotNull FallbacksHolder holder, String original) {
        return mapFallback(holder.tf$get(), original);
    }

    public static String mapFallback(@Nullable Map<String, String> fallbacks, String original) {
        if (fallbacks == null || fallbacks.isEmpty()) return original;
        String selectedLocale = Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase(Locale.ROOT);
        return fallbacks.getOrDefault(selectedLocale, original);
    }
}
