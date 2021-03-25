package de.ellpeck.naturesstarlight;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.crystal.property.PropertyConstellation;
import hellfirepvp.astralsorcery.common.registry.internal.InternalRegistryPrimer;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

@Mod(NaturesStarlight.ID)
public class NaturesStarlight {

    public static final String ID = "naturesstarlight";

    public static IWeakConstellation constellation;

    public NaturesStarlight() {
        FMLJavaModLoadingContext context = FMLJavaModLoadingContext.get();
        context.getModEventBus().addListener(this::setup);
        this.preSetup();
    }

    private void preSetup() {
        // constellation
        // this needs to be registered before the setup event (for some reason)
        constellation = new Constellation.Weak("naritis");
        StarLocation tip = constellation.addStar(5, 3);
        StarLocation tri1 = constellation.addStar(14, 13);
        constellation.addConnection(tip, tri1);
        StarLocation tri2 = constellation.addStar(12, 14);
        constellation.addConnection(tri1, tri2);
        StarLocation tri3 = constellation.addStar(17, 18);
        constellation.addConnection(tri2, tri3);
        constellation.addConnection(tri3, tri1);
        StarLocation isolated = constellation.addStar(11, 16);
        constellation.addConnection(tip, isolated);
        StarLocation line1 = constellation.addStar(7, 17);
        constellation.addConnection(tip, line1);
        StarLocation line2 = constellation.addStar(12, 21);
        constellation.addConnection(line1, line2);
        AstralSorcery.getProxy().getRegistryPrimer().register(constellation);
    }

    public void setup(FMLCommonSetupEvent event) {
        InternalRegistryPrimer primer = AstralSorcery.getProxy().getRegistryPrimer();
        // constellation property
        primer.register(new PropertyConstellation(constellation));
        // constellation effect
        primer.register(new ConstellationEffectProvider(constellation) {
            @Override
            public ConstellationEffect createEffect(@Nullable ILocatable origin) {
                return new NaritisConstellationEffect(origin, constellation);
            }
        });
    }
}
