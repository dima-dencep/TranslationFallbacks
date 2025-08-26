import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redlance.dima_dencep.mods.translationfallbacks.adventure.TranslationFallbacksInjector;
import org.redlance.dima_dencep.mods.translationfallbacks.adventure.duck.FallbacksHolder;

import java.io.IOException;
import java.util.Map;

public class AdventureTests {
    @BeforeAll
    public static void inject() throws ReflectiveOperationException, IOException {
        TranslationFallbacksInjector.inject();
    }

    @Test
    public void testPatch() {
        Assertions.assertThrows(LinkageError.class, TranslationFallbacksInjector::inject);
    }

    @Test
    public void testRead() {
        Component component = GsonComponentSerializer.gson().deserialize("""
                {
                  "translate": "translation.key",
                  "fallback": "default",
                  "fallbacks": {
                    "en_us": "default for en_us",
                    "ru_ru": "дефолт для ru_ru"
                  }
                }"""
        );

        Map<String, String> fallbacks = ((FallbacksHolder) component).tf$get();
        Assertions.assertNotNull(fallbacks);

        Assertions.assertEquals("default for en_us", fallbacks.get("en_us"));
        Assertions.assertEquals("дефолт для ru_ru", fallbacks.get("ru_ru"));
    }

    @Test
    public void testWrite() {
        Component component = GsonComponentSerializer.gson().deserialize("""
                {
                  "translate": "translation.key",
                  "fallback": "default",
                  "fallbacks": {
                    "en_us": "default for en_us",
                    "ru_ru": "дефолт для ru_ru"
                  }
                }"""
        );

        JsonObject obj = GsonComponentSerializer.gson().serializeToTree(component).getAsJsonObject();
        System.out.println(obj);

        Assertions.assertTrue(obj.has("fallbacks"));
    }
}
