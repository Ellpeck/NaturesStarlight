package de.ellpeck.naturesstarlight;

import de.ellpeck.naturesstarlight.astral.NaritisConstellationEffect;
import de.ellpeck.naturesstarlight.astral.NaritisMantleEffect;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(NaturesStarlight.ID)
public final class NaturesStarlight {

    public static final String ID = "naturesstarlight";

    public static ForgeConfigSpec.ConfigValue<Integer> crystalGeneratorAura;
    public static ForgeConfigSpec.ConfigValue<Float> starlightEffectAddedStarlight;
    public static ForgeConfigSpec.ConfigValue<Integer> starlightEffectAuraDrain;

    public NaturesStarlight() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        crystalGeneratorAura = builder
                .comment("The amount of aura that the crystal generator generates per crystal growth")
                .define("crystalGeneratorAura", 30000);
        starlightEffectAddedStarlight = builder
                .comment("The maximum amount of starlight that is added around the powder of starry skies")
                .define("starlightEffectAddedStarlight", 0.6F);
        starlightEffectAuraDrain = builder
                .comment("The amount of aura that the powder of starry skies drains per second")
                .define("starlightEffectAuraDrain", 400);
        NaritisConstellationEffect.CONFIG.createEntries(builder);
        NaritisMantleEffect.CONFIG.createEntries(builder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, builder.build());

        Registry.init();
    }

}
