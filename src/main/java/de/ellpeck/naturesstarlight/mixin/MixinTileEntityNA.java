package de.ellpeck.naturesstarlight.mixin;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityImpl;
import de.ellpeck.naturesstarlight.Registry;
import de.ellpeck.naturesstarlight.astral.NaritisConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityImpl.class)
public class MixinTileEntityNA extends TileEntity {

    public MixinTileEntityNA(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Inject(at = @At("HEAD"), method = "generateAura(I)V", remap = false, cancellable = true)
    private void generateAura(int amount, CallbackInfo callback) {
        TileRitualPedestal pedestal = NaritisConstellationEffect.getLastAffectingPedestal((TileEntityImpl) this.getTileEntity());
        if (pedestal == null)
            return;
        IWeakConstellation constellation = pedestal.getRitualConstellation();
        if (constellation != Registry.NARITIS.get())
            return;
        int radius = MathHelper.ceil(pedestal.getRadius());
        int toAdd = MathHelper.ceil(amount * NaritisConstellationEffect.CONFIG.auraGenIncreaseFactor.get());
        while (toAdd > 0) {
            // we add aura in the area around the ritual pedestal, instead of based on our own position and range
            BlockPos spot = IAuraChunk.getLowestSpot(this.world, pedestal.getPos(), radius, pedestal.getPos());
            toAdd -= IAuraChunk.getAuraChunk(this.world, spot).storeAura(spot, toAdd);
        }
        callback.cancel();
    }
}
