package mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;
import org.junit.jupiter.api.Disabled;
import org.redlance.dima_dencep.mods.translationfallbacks.TranslationFallbacksMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import tests.FallbacksTest;

@Disabled
@Mixin(value = TranslationFallbacksMod.class, remap = false)
public class TranslationFallbacksModMixin {
    @Redirect(
            method = "mapFallback(Ljava/util/Map;Ljava/lang/String;)Ljava/lang/String;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;getLanguageManager()Lnet/minecraft/client/resources/language/LanguageManager;"
            )
    )
    private static LanguageManager tf$bootTests(Minecraft instance) {
        return FallbacksTest.MANAGER;
    }
}
