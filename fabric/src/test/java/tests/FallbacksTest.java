package tests;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import mixins.ClientPackSourceAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class FallbacksTest {
    public static final LanguageManager MANAGER = new LanguageManager(Language.DEFAULT, System.out::println);

    @BeforeAll
    public static void bootstrapLanguageManager() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void testTranslation() {
        Component component = fromJson("""
                {
                  "translate": "options.on",
                  "fallback": "default",
                  "fallbacks": {
                    "en_us": "default for en_us",
                    "ru_ru": "дефолт для ru_ru"
                  }
                }"""
        );

        switchLanguage("en_us");
        Assertions.assertEquals("ON", component.getString());

        switchLanguage("ru_ru");
        Assertions.assertEquals("ON", component.getString());

        switchLanguage("be_by");
        Assertions.assertEquals("ON", component.getString());
    }

    @Test
    public void testMod() {
        Component component = fromJson("""
                {
                  "translate": "translation.key",
                  "fallback": "default",
                  "fallbacks": {
                    "en_us": "default for en_us",
                    "ru_ru": "дефолт для ru_ru"
                  }
                }"""
        );

        switchLanguage("en_us");
        Assertions.assertEquals("default for en_us", component.getString());

        switchLanguage("ru_ru");
        Assertions.assertEquals("дефолт для ru_ru", component.getString());

        switchLanguage("be_by"); // Vanilla behavior
        Assertions.assertEquals("default", component.getString());
    }

    @Test
    public void testVanilla() {
        Component component = fromJson("""
                {
                  "translate": "translation.key",
                  "fallback": "default"
                }"""
        );

        switchLanguage("en_us");
        Assertions.assertEquals("default", component.getString());

        switchLanguage("ru_ru");
        Assertions.assertEquals("default", component.getString());

        switchLanguage("be_by");
        Assertions.assertEquals("default", component.getString());
    }

    public static void switchLanguage(String language) {
        MANAGER.setSelected(language);
        try (CloseableResourceManager resourceManager = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, Collections.singletonList(
                ClientPackSourceAccessor.createVanillaPackSource(FabricLoader.getInstance().getGameDir())
        ))) {
            MANAGER.onResourceManagerReload(resourceManager);
        }
        Assertions.assertEquals(MANAGER.getSelected(), language);
    }

    public static Component fromJson(String json) {
        return ComponentSerialization.CODEC.parse(
                RegistryAccess.EMPTY.createSerializationContext(JsonOps.INSTANCE),
                JsonParser.parseString(json)
        ).getOrThrow();
    }
}
