package de.ellpeck.naturesstarlight;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(NaturesStarlight.ID)
public final class NaturesStarlight {

    public static final String ID = "naturesstarlight";

    public NaturesStarlight() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        NaritisConstellationEffect.CONFIG.createEntries(builder);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, builder.build());
    }
}
