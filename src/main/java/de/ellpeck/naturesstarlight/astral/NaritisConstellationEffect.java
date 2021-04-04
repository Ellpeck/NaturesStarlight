package de.ellpeck.naturesstarlight.astral;

import de.ellpeck.naturesaura.Helper;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesaura.blocks.tiles.TileEntityImpl;
import de.ellpeck.naturesstarlight.NaturesStarlight;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.event.PlayerAffectionFlags;
import hellfirepvp.astralsorcery.common.item.crystal.ItemAttunedCrystalBase;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class NaritisConstellationEffect extends ConstellationEffect {

    public static final NaritisConfig CONFIG = new NaritisConfig();
    private static final PlayerAffectionFlags.AffectionFlag AFFECTION = makeAffectionFlag("naritis");

    public NaritisConstellationEffect(@Nonnull ILocatable origin, @Nonnull IWeakConstellation cst) {
        super(origin, cst);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float alphaMultiplier, boolean extended) {
        if (world.getGameTime() % 60 != 0)
            return;

        // for some reason you have to do all this to find out if the property is corrupted on the client, ok
        ConstellationEffectProperties prop = this.createProperties(pedestal.getMirrorCount());
        ItemStack socket = pedestal.getCurrentCrystal();
        if (!socket.isEmpty() && socket.getItem() instanceof ItemAttunedCrystalBase) {
            IMinorConstellation trait = ((ItemAttunedCrystalBase) socket.getItem()).getTraitConstellation(socket);
            if (trait != null)
                trait.affectConstellationEffect(prop);
        }

        for (int i = world.rand.nextInt(5) + 5; i >= 0; i--) {
            NaturesAuraAPI.instance().spawnMagicParticle(
                    pos.getX() + 0.5F + world.rand.nextGaussian() * 1.5F,
                    pos.getY(),
                    pos.getZ() + 0.5F + world.rand.nextGaussian() * 1.5F,
                    world.rand.nextGaussian() * 0.01F,
                    world.rand.nextFloat() * 0.04F + 0.02F,
                    world.rand.nextGaussian() * 0.01F,
                    prop.isCorrupted() ? 0x871c0c : 0x89cc37, 1F + world.rand.nextFloat() * 1.5F, 80, 0F, false, true);
        }
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, ConstellationEffectProperties properties, @Nullable IMinorConstellation trait) {
        if (world.isRemote)
            return false;
        int radius = MathHelper.ceil(properties.getSize());
        if (properties.isCorrupted()) {
            // drain aura
            BlockPos spot = IAuraChunk.getHighestSpot(world, pos, radius, pos);
            IAuraChunk.getAuraChunk(world, spot).drainAura(spot, CONFIG.auraDrainPerTick.get());

            // notify aura generators
            TileRitualPedestal pedestal = this.getPedestal(world, pos);
            if (pedestal != null) {
                Helper.getTileEntitiesInArea(world, pos, radius, tile -> {
                    if ((tile instanceof TileEntityImpl))
                        setLastAffectingPedestal((TileEntityImpl) tile, pedestal);
                    return false;
                });
            }
        } else {
            if (IAuraChunk.getAuraInArea(world, pos, radius) >= IAuraChunk.DEFAULT_AURA * 2)
                return false;
            int toAdd = CONFIG.auraPerTick.get();
            while (toAdd > 0) {
                BlockPos spot = IAuraChunk.getLowestSpot(world, pos, radius, pos);
                toAdd -= IAuraChunk.getAuraChunk(world, spot).storeAura(spot, toAdd);
            }
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

    public static TileRitualPedestal getLastAffectingPedestal(TileEntityImpl tile) {
        long packedPos = tile.getTileData().getLong(NaturesStarlight.ID + ":affecting_pedestal");
        if (packedPos == 0)
            return null;
        BlockPos pos = BlockPos.fromLong(packedPos);
        TileEntity ret = tile.getWorld().getTileEntity(pos);
        return ret instanceof TileRitualPedestal ? (TileRitualPedestal) ret : null;
    }

    public static void setLastAffectingPedestal(TileEntityImpl tile, TileRitualPedestal affecting) {
        tile.getTileData().putLong(NaturesStarlight.ID + ":affecting_pedestal", affecting.getPos().toLong());
    }

    public static class NaritisConfig extends Config {

        public ForgeConfigSpec.ConfigValue<Integer> auraPerTick;
        public ForgeConfigSpec.ConfigValue<Integer> auraDrainPerTick;
        public ForgeConfigSpec.ConfigValue<Float> auraGenIncreaseFactor;
        public ForgeConfigSpec.ConfigValue<List<String>> engravingEnchantments;

        public NaritisConfig() {
            super("naritis", 5, 2.5);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder builder) {
            builder.push(this.getPath());
            super.createEntries(builder);
            this.auraPerTick = builder
                    .comment("Defines the amount of aura that this ritual generates per tick by default")
                    .define("auraPerTick", 350);
            this.auraDrainPerTick = builder
                    .comment("Defines the amount of aura the corrupted version of this ritual drains per tick by default")
                    .define("auraDrainPerTick", 75);
            this.auraGenIncreaseFactor = builder
                    .comment("Defines the factor that the corrupted version of this ritual increases aura generation in the area by")
                    .define("auraGenIncreaseFactor", 2.5F);
            this.engravingEnchantments = builder
                    .comment("The enchantments that can be applied using stellar refraction, along with the minimum and maximum applied levels")
                    .define("engravingEnchantments", Arrays.asList(
                            "naturesaura:aura_mending, 1, 1",
                            "minecraft:silk_touch, 1, 1",
                            "minecraft:efficiency, 5, 6",
                            "minecraft:thorns, 4, 6"));
            builder.pop(3);
        }

        @Override
        protected String translationKey(String key) {
            return "config." + this.getFullPath() + '.' + key;
        }

    }

}
