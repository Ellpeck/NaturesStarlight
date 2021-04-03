package de.ellpeck.naturesstarlight.aura;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.api.aura.chunk.IDrainSpotEffect;
import de.ellpeck.naturesaura.api.aura.type.IAuraType;
import de.ellpeck.naturesstarlight.NaturesStarlight;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class StarlightIncreaseEffect implements IDrainSpotEffect {

    public static final ResourceLocation NAME = new ResourceLocation(NaturesStarlight.ID, "starlight_increase");

    @Override
    public ActiveType isActiveHere(PlayerEntity player, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (getFactor(player.world, player.getPosition()) <= 0)
            return ActiveType.INACTIVE;
        if (!NaturesAuraAPI.instance().isEffectPowderActive(player.world, player.getPosition(), NAME))
            return ActiveType.INHIBITED;
        return ActiveType.ACTIVE;
    }

    @Override
    public ItemStack getDisplayIcon() {
        return new ItemStack(ItemsAS.STARDUST);
    }

    @Override
    public void update(World world, Chunk chunk, IAuraChunk auraChunk, BlockPos pos, Integer spot) {
        if (getCurrentFactor(world, pos) > 0)
            auraChunk.drainAura(pos, NaturesStarlight.starlightEffectAuraDrain.get());
    }

    @Override
    public boolean appliesHere(Chunk chunk, IAuraChunk auraChunk, IAuraType type) {
        return true;
    }

    @Override
    public ResourceLocation getName() {
        return NAME;
    }

    public static float getCurrentFactor(World world, BlockPos pos) {
        if (!NaturesAuraAPI.instance().isEffectPowderActive(world, pos, NAME))
            return 0;
        return getFactor(world, pos);
    }

    private static float getFactor(World world, BlockPos pos) {
        int aura = IAuraChunk.getAuraInArea(world, pos, 30);
        return aura >= 1500000 ? Math.min(1, aura / 5000000F) : 0;
    }
}