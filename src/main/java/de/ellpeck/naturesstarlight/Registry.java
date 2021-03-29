package de.ellpeck.naturesstarlight;

import de.ellpeck.naturesstarlight.astral.NaritisConstellationEffect;
import de.ellpeck.naturesstarlight.aura.CrystalGeneratorBlock;
import de.ellpeck.naturesstarlight.aura.CrystalGeneratorTileEntity;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.crystal.property.PropertyConstellation;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.RegistryKey;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.awt.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Registry {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, NaturesStarlight.ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NaturesStarlight.ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NaturesStarlight.ID);

    public static final RegistryObject<Block> CRYSTAL_GENERATOR_BLOCK = BLOCKS.register("crystal_generator", CrystalGeneratorBlock::new);
    public static final RegistryObject<Item> CRYSTAL_GENERATOR_ITEM = ITEMS.register("crystal_generator", () -> new BlockItem(CRYSTAL_GENERATOR_BLOCK.get(), new Item.Properties().group(ItemGroup.DECORATIONS)));
    public static final RegistryObject<TileEntityType<?>> CRYSTAL_GENERATOR_TILE = TILE_ENTITIES.register("crystal_generator", () -> TileEntityType.Builder.create(CrystalGeneratorTileEntity::new, CRYSTAL_GENERATOR_BLOCK.get()).build(null));

    // we need to use a lazy here because the crystal event (which needs this) is fired before the constellation event, bleh
    public static final Lazy<IWeakConstellation> NARITIS = Lazy.of(() -> {
        IWeakConstellation ret = new Constellation.Weak("naritis", new Color(0x1E891E));
        StarLocation tip = ret.addStar(5, 3);
        StarLocation tri1 = ret.addStar(24, 13);
        ret.addConnection(tip, tri1);
        StarLocation tri2 = ret.addStar(20, 17);
        ret.addConnection(tri1, tri2);
        StarLocation tri3 = ret.addStar(28, 22);
        ret.addConnection(tri2, tri3);
        ret.addConnection(tri3, tri1);
        StarLocation isolated = ret.addStar(15, 16);
        ret.addConnection(tip, isolated);
        StarLocation line1 = ret.addStar(7, 17);
        ret.addConnection(tip, line1);
        StarLocation line2 = ret.addStar(12, 21);
        ret.addConnection(line1, line2);
        return ret;
    });

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TILE_ENTITIES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

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
