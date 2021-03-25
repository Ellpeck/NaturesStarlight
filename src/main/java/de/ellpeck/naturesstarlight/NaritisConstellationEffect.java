package de.ellpeck.naturesstarlight;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.event.PlayerAffectionFlags;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NaritisConstellationEffect extends ConstellationEffect {

    public static final NaritisConfig CONFIG = new NaritisConfig();
    private static final PlayerAffectionFlags.AffectionFlag AFFECTION = makeAffectionFlag("naritis");

    protected NaritisConstellationEffect(@Nonnull ILocatable origin, @Nonnull IWeakConstellation cst) {
        super(origin, cst);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        if (world.getGameTime() % 60 != 0)
            return;
        for (int i = world.rand.nextInt(5) + 5; i >= 0; i--) {
            NaturesAuraAPI.instance().spawnMagicParticle(
                    pos.getX() + 0.5F + world.rand.nextGaussian() * 1.5F,
                    pos.getY(),
                    pos.getZ() + 0.5F + world.rand.nextGaussian() * 1.5F,
                    world.rand.nextGaussian() * 0.01F,
                    world.rand.nextFloat() * 0.04F + 0.02F,
                    world.rand.nextGaussian() * 0.01F,
                    0x5ccc30, 1F + world.rand.nextFloat() * 1.5F, 80, 0F, false, true);
        }
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        if (world.isRemote)
            return false;
        int radius = MathHelper.ceil(properties.getSize());
        if (IAuraChunk.getAuraInArea(world, pos, radius) >= IAuraChunk.DEFAULT_AURA * 2)
            return false;
        int toAdd = CONFIG.auraPerTick.get();
        while (toAdd > 0) {
            BlockPos spot = IAuraChunk.getLowestSpot(world, pos, radius, pos);
            toAdd -= IAuraChunk.getAuraChunk(world, spot).storeAura(spot, toAdd);
        }
        return true;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    @Override
    public PlayerAffectionFlags.AffectionFlag getPlayerAffectionFlag() {
        return AFFECTION;
    }

    public static class NaritisConfig extends Config {

        public ForgeConfigSpec.ConfigValue<Integer> auraPerTick;

        public NaritisConfig() {
            super("naritis", 10, 5);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder builder) {
            builder.push(this.getPath());
            super.createEntries(builder);
            this.auraPerTick = builder
                    .comment("Defines the amount of aura that this ritual generates per tick by default")
                    .translation(this.translationKey("range"))
                    .define("auraPerTick", 5);
            builder.pop();
        }

        @Override
        protected String translationKey(String key) {
            return "config." + this.getFullPath() + '.' + key;
        }
    }
}
