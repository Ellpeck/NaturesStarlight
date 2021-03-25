package de.ellpeck.naturesstarlight;

import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.event.PlayerAffectionFlags;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NaritisConstellationEffect extends ConstellationEffect {

    private static final NaritisConfig CONFIG = new NaritisConfig();
    private static final PlayerAffectionFlags.AffectionFlag AFFECTION = makeAffectionFlag("naritis");

    protected NaritisConstellationEffect(@Nonnull ILocatable origin, @Nonnull IWeakConstellation cst) {
        super(origin, cst);
    }

    @Override
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {

    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        return false;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    @Override
    public PlayerAffectionFlags.AffectionFlag getPlayerAffectionFlag() {
        return AFFECTION;
    }

    private static class NaritisConfig extends Config {

        public NaritisConfig() {
            super("naritis", 35, 2);
        }
    }
}
