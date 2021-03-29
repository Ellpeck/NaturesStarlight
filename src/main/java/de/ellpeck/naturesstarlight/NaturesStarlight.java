package de.ellpeck.naturesstarlight;

import de.ellpeck.naturesstarlight.astral.NaritisConstellationEffect;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(NaturesStarlight.ID)
public final class NaturesStarlight {

    public static final String ID = "naturesstarlight";

    public static ForgeConfigSpec.ConfigValue<Integer> crystalGeneratorAura;

    public NaturesStarlight() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        crystalGeneratorAura = builder
                .comment("The amount of aura that the crystal generator generates per crystal growth")
                .define("crystalGeneratorAura", 30000);
        NaritisConstellationEffect.CONFIG.createEntries(builder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, builder.build());

        Registry.init();
    }

}
