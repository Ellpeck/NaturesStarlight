package de.ellpeck.naturesstarlight.mixin;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.packet.PacketHandler;
import de.ellpeck.naturesaura.packet.PacketParticles;
import de.ellpeck.naturesstarlight.NaturesStarlight;
import de.ellpeck.naturesstarlight.aura.CrystalGeneratorTileEntity;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystals;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileCelestialCrystals.class)
public abstract class MixinCelestialCrystals extends TileEntityTick {

    public MixinCelestialCrystals(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Inject(at = @At("HEAD"), method = "setGrowth(I)V", remap = false)
    private void setGrowth(int stage, CallbackInfo callback) {
        Helper.getTileEntitiesInArea(this.world, this.pos, 5, tile -> {
            if (!(tile instanceof CrystalGeneratorTileEntity))
                return false;
            CrystalGeneratorTileEntity gen = (CrystalGeneratorTileEntity) tile;
            BlockPos genPos = gen.getPos();

            int toAdd = NaturesStarlight.crystalGeneratorAura.get();
            if (gen.canGenerateRightNow(35, toAdd)) {
                while (toAdd > 0) {
                    BlockPos spot = IAuraChunk.getLowestSpot(this.world, genPos, 35, genPos);
                    toAdd -= IAuraChunk.getAuraChunk(this.world, spot).storeAura(spot, toAdd);
                }
                PacketHandler.sendToAllAround(this.world, genPos, 32, new PacketParticles(genPos.getX(), genPos.getY(), genPos.getZ(), PacketParticles.Type.FLOWER_GEN_AURA_CREATION));
            }

            PacketHandler.sendToAllAround(this.world, genPos, 32, new PacketParticles(this.pos.getX(), this.pos.getY(), this.pos.getZ(), PacketParticles.Type.FLOWER_GEN_CONSUME, 0x0032a8));
            return true;
        });
    }

}
