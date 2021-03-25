package de.ellpeck.naturesstarlight;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NaturesStarlight.ID)
public class NaturesStarlight {

    public static final String ID = "naturesstarlight";

    public NaturesStarlight() {
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        context.getModEventBus().addListener(this::setup);
    }

    public void setup(FMLCommonSetupEvent event) {

    }
}
