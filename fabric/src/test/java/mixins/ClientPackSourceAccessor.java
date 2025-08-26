package mixins;

import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.VanillaPackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.file.Path;

@Mixin(ClientPackSource.class)
public interface ClientPackSourceAccessor {
    @Invoker(value = "createVanillaPackSource")
    static VanillaPackResources createVanillaPackSource(Path assetIndex) {
        throw new AssertionError();
    }
}
