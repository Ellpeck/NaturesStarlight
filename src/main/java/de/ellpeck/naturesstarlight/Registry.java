package de.ellpeck.naturesstarlight;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.crystal.property.PropertyConstellation;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.awt.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Registry {

    // we need to use a lazy here because the crystal event (which needs this) is fired before the constellation event, bleh
    public static final Lazy<IWeakConstellation> NARITIS = Lazy.of(() -> {
        IWeakConstellation ret = new Constellation.Weak("naritis", new Color(0x1E891E));
        StarLocation tip = ret.addStar(5, 3);
        StarLocation tri1 = ret.addStar(14, 13);
        ret.addConnection(tip, tri1);
        StarLocation tri2 = ret.addStar(12, 14);
        ret.addConnection(tri1, tri2);
        StarLocation tri3 = ret.addStar(17, 18);
        ret.addConnection(tri2, tri3);
        ret.addConnection(tri3, tri1);
        StarLocation isolated = ret.addStar(11, 16);
        ret.addConnection(tip, isolated);
        StarLocation line1 = ret.addStar(7, 17);
        ret.addConnection(tip, line1);
        StarLocation line2 = ret.addStar(12, 21);
        ret.addConnection(line1, line2);
        return ret;
    });

    @SubscribeEvent
    public static void registerConstellations(RegistryEvent.Register<IConstellation> event) {
        event.getRegistry().register(NARITIS.get());
    }

    @SubscribeEvent
    public static void registerConstellationEffects(RegistryEvent.Register<ConstellationEffectProvider> event) {
        event.getRegistry().register(new ConstellationEffectProvider(NARITIS.get()) {
            @Override
            public ConstellationEffect createEffect(@Nullable ILocatable origin) {
                return new NaritisConstellationEffect(origin, NARITIS.get());
            }
        });
    }

    @SubscribeEvent
    public static void registerCrystalProperties(RegistryEvent.Register<CrystalProperty> event) {
        event.getRegistry().register(new PropertyConstellation(NARITIS.get()));
    }
}
