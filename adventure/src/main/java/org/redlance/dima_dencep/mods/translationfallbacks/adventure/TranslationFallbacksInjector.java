package org.redlance.dima_dencep.mods.translationfallbacks.adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.redlance.dima_dencep.mods.translationfallbacks.adventure.patches.ComponentSerializerImpl;
import org.redlance.dima_dencep.mods.translationfallbacks.adventure.patches.TranslatableComponentImpl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class TranslationFallbacksInjector {
    public static void inject() throws ReflectiveOperationException, IOException {
        patchClass(Component.class, "net/kyori/adventure/text/TranslatableComponentImpl.class", TranslatableComponentImpl::patch);
        patchClass(GsonComponentSerializer.class, "net/kyori/adventure/text/serializer/gson/ComponentSerializerImpl.class", ComponentSerializerImpl::patch);
    }

    private static void patchClass(Class<?> nearClass, String name, UnaryOperator<byte[]> patcher) throws ReflectiveOperationException, IOException {
        try (InputStream is = Objects.requireNonNull(nearClass.getClassLoader().getResourceAsStream(name))) {
            byte[] bytecode = patcher.apply(is.readAllBytes());
            MethodHandles.privateLookupIn(nearClass, MethodHandles.lookup()).defineClass(bytecode);
        }
    }
}
