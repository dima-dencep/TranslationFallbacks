package org.redlance.dima_dencep.mods.translationfallbacks.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redlance.dima_dencep.mods.translationfallbacks.TranslationFallbacksMod;
import org.redlance.dima_dencep.mods.translationfallbacks.ducks.FallbacksHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;
import java.util.function.Function;

@Mixin(TranslatableContents.class)
public abstract class TranslatableContentsMixin implements FallbacksHolder {
    @Unique
    @Nullable
    private Map<String, String> tf$fallbacks;

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;mapCodec(Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;",
                    remap = false
            )
    )
    private static MapCodec<TranslatableContents> tf$parseFallbacks(MapCodec<TranslatableContents> original) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                original.forGetter(Function.identity()),

                Codec.unboundedMap(Codec.STRING, Codec.STRING)
                        .optionalFieldOf("fallbacks")
                        .forGetter(translatableContents -> Optional.ofNullable(translatableContents.tf$get()))

        ).apply(instance, (contents, fallbacks) -> {
            fallbacks.ifPresent(contents::tf$set);
            return contents;
        }));
    }

    @WrapOperation(
            method = "decompose",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/network/chat/contents/TranslatableContents;fallback:Ljava/lang/String;"
            )
    )
    private String tf$implFallbacks(TranslatableContents instance, Operation<String> original) {
        return TranslationFallbacksMod.mapFallback(instance, original.call(instance));
    }

    @WrapOperation(
            method = "resolve",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/contents/TranslatableContents;"
            )
    )
    private TranslatableContents tf$setFallbacks(String key, String fallback, Object[] args, Operation<TranslatableContents> original) {
        TranslatableContents contents = original.call(key, fallback, args);

        Map<String, String> fallbacks = tf$get();
        if (fallbacks != null) contents.tf$set(fallbacks);

        return contents;
    }

    @Override
    public void tf$set(@NotNull Map<String, String> fallbacks) {
        if (this.tf$fallbacks == null) {
            this.tf$fallbacks = new HashMap<>(fallbacks.size());
        } else {
            this.tf$fallbacks.clear();
        }

        this.tf$fallbacks.putAll(fallbacks);
    }

    @Override
    @Nullable
    public Map<String, String> tf$get() {
        return this.tf$fallbacks;
    }

    @ModifyReturnValue(
            method = "getFallback",
            at = @At(
                    value = "RETURN"
            )
    )
    private String tf$mapFallback(String original) {
        return TranslationFallbacksMod.mapFallback(this.tf$fallbacks, original);
    }
}
