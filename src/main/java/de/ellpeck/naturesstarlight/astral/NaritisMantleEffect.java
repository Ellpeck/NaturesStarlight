package de.ellpeck.naturesstarlight.astral;

import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.LogicalSide;

public class NaritisMantleEffect extends MantleEffect {

    public static final NaritisConfig CONFIG = new NaritisConfig();

    public NaritisMantleEffect(IWeakConstellation constellation) {
        super(constellation);
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    @Override
    protected void tickServer(PlayerEntity player) {
        if (player.world.getGameTime() % 20 != 0)
            return;
        // check if there is enough charge
        float charge = CONFIG.chargeConvertedPerSecond.get();
        if (!AlignmentChargeHandler.INSTANCE.hasCharge(player, LogicalSide.SERVER, charge))
            return;
        // check if there is enough space
        int aura = MathHelper.floor(charge * CONFIG.chargeToAuraRatio.get());
        if (!NaturesAuraAPI.instance().insertAuraIntoPlayer(player, aura, true))
            return;
        // actually do it
        NaturesAuraAPI.instance().insertAuraIntoPlayer(player, aura, false);
        AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeConvertedPerSecond.get(), false);
    }

    public static class NaritisConfig extends Config {

        public ForgeConfigSpec.ConfigValue<Float> chargeToAuraRatio;
        public ForgeConfigSpec.ConfigValue<Float> chargeConvertedPerSecond;

        public NaritisConfig() {
            super("naritis");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder builder) {
            builder.push(this.getPath());
            super.createEntries(builder);
            this.chargeToAuraRatio = builder
                    .comment("The amount of aura that one unit of charge creates")
                    .define("chargeToAuraRatio", 0.01F);
            this.chargeConvertedPerSecond = builder
                    .comment("The amount of charge that is converted into aura per second")
                    .define("chargeConvertedPerSecond", 6000F);
            builder.pop(3);
        }

        @Override
        protected String translationKey(String key) {
            return "config." + this.getFullPath() + '.' + key;
        }

    }
}
