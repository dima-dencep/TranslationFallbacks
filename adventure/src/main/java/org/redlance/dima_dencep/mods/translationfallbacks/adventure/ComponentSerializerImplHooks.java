package org.redlance.dima_dencep.mods.translationfallbacks.adventure;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.TranslatableComponent;
import org.redlance.dima_dencep.mods.translationfallbacks.adventure.duck.FallbacksHolder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

@SuppressWarnings("unused")
public class ComponentSerializerImplHooks {
    private static final Type FALLBACKS_TYPE = TypeToken.getParameterized(Map.class, String.class, String.class).getType();

    public static BuildableComponent<?, ?> doRead(BuildableComponent<?, ?> component, JsonObject styles, Gson gson) {
        JsonObject fallbacks = styles.getAsJsonObject("fallbacks");
        ((FallbacksHolder) component).tf$set(gson.fromJson(fallbacks, FALLBACKS_TYPE));
        return component;
    }

    public static void doWrite(TranslatableComponent component, JsonWriter writer, Gson gson) throws IOException {
        Map<String, String> fallbacks = ((FallbacksHolder) component).tf$get();
        if (fallbacks != null && !fallbacks.isEmpty()) {
            writer.name("fallbacks");
            gson.toJson(fallbacks, FALLBACKS_TYPE, writer);
        }
    }
}
