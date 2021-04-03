package de.ellpeck.naturesstarlight.mixin;

import de.ellpeck.naturesstarlight.NaturesStarlight;
import de.ellpeck.naturesstarlight.aura.StarlightIncreaseEffect;
import hellfirepvp.astralsorcery.common.util.world.SkyCollectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(SkyCollectionHelper.class)
public class MixinSkyCollectionHelper {

    @OnlyIn(Dist.CLIENT)
    @Inject(at = @At("RETURN"), method = "getSkyNoiseDistributionClient(Lnet/minecraft/util/RegistryKey;Lnet/minecraft/util/math/BlockPos;)Ljava/util/Optional;", remap = false, cancellable = true)
    private static void getSkyNoiseDistributionClient(RegistryKey<World> dim, BlockPos pos, CallbackInfoReturnable<Optional<Float>> callback) {
        float ret = callback.getReturnValue().orElse(0F);
        float newFactor = modifyDistribution(Minecraft.getInstance().world, pos, ret);
        callback.setReturnValue(Optional.of(newFactor));
    }

    @Inject(at = @At("RETURN"), method = "getSkyNoiseDistribution(Lnet/minecraft/world/ISeedReader;Lnet/minecraft/util/math/BlockPos;)F", remap = false, cancellable = true)
    private static void getSkyNoiseDistribution(ISeedReader world, BlockPos pos, CallbackInfoReturnable<Float> callback) {
        callback.setReturnValue(modifyDistribution(world.getWorld(), pos, callback.getReturnValue()));
    }

    private static float modifyDistribution(World world, BlockPos pos, float distribution) {
        float factor = StarlightIncreaseEffect.getCurrentFactor(world, pos);
        if (factor > 0)
            distribution += factor * NaturesStarlight.starlightEffectAddedStarlight.get();
        return distribution;
    }
}
