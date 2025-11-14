package org.redlance.dima_dencep.mods.translationfallbacks.compat;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;

@SuppressWarnings("unchecked")
public class LanguageReloadCompat {
    private static final MethodHandle GET_LANGUAGES_HANDLE;

    static {
        MethodHandle getLanguagesHandle;
        try {
            getLanguagesHandle = MethodHandles.publicLookup().findStatic(
                    Class.forName("jerozgen.languagereload.LanguageReload"),
                    "getLanguages", MethodType.methodType(LinkedList.class)
            );
        } catch (ReflectiveOperationException ex) {
            getLanguagesHandle = null;
        }
        GET_LANGUAGES_HANDLE = getLanguagesHandle;
    }

    public static boolean isAvailable() {
        return GET_LANGUAGES_HANDLE != null;
    }

    public static @NotNull List<@NotNull String> getLanguages() {
        try {
            return (LinkedList<String>) GET_LANGUAGES_HANDLE.invokeExact();
        } catch (Throwable th) {
            return Collections.emptyList();
        }
    }

    public static String mapFallback(@NotNull Map<String, String> fallbacks, String original) {
        for (String selectedLocale : LanguageReloadCompat.getLanguages()) {
            String mappedFallback = fallbacks.get(selectedLocale.toLowerCase(Locale.ROOT));
            if (mappedFallback != null && !mappedFallback.isBlank()) return mappedFallback;
        }
        return original;
    }
}
