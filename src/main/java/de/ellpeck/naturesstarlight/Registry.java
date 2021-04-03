package de.ellpeck.naturesstarlight;

import de.ellpeck.naturesaura.NaturesAura;
import de.ellpeck.naturesaura.api.NaturesAuraAPI;
import de.ellpeck.naturesstarlight.astral.NaritisConstellationEffect;
import de.ellpeck.naturesstarlight.astral.NaritisMantleEffect;
import de.ellpeck.naturesstarlight.astral.perk.AuraProtectionPerk;
import de.ellpeck.naturesstarlight.astral.perk.ToolIncreasePerk;
import de.ellpeck.naturesstarlight.aura.CrystalGeneratorBlock;
import de.ellpeck.naturesstarlight.aura.CrystalGeneratorTileEntity;
import de.ellpeck.naturesstarlight.aura.StarlightIncreaseEffect;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.engraving.EngravingEffect;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.crystal.property.PropertyConstellation;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
    public static final RegistryObject<Item> CRYSTAL_GENERATOR_ITEM = ITEMS.register("crystal_generator", () -> new BlockItem(CRYSTAL_GENERATOR_BLOCK.get(), new Item.Properties().group(NaturesAura.CREATIVE_TAB)));
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

    // we can't use deferred register for this one since it sets the registry name in the constructor, and it can't be set twice
    public static PerkAttributeType toolIncreasePerk;
    public static PerkAttributeType auraProtectionPerk;

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        TILE_ENTITIES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);

        NaturesAuraAPI.DRAIN_SPOT_EFFECTS.put(StarlightIncreaseEffect.NAME, StarlightIncreaseEffect::new);
        NaturesAuraAPI.EFFECT_POWDERS.put(StarlightIncreaseEffect.NAME, 0x084a8c);

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
    public static void registerEngravingEffects(RegistryEvent.Register<EngravingEffect> event) {
        EngravingEffect effect = new EngravingEffect(NARITIS.get());
        for (String s : NaritisConstellationEffect.CONFIG.engravingEnchantments.get()) {
            String[] value = s.split(",");
            ResourceLocation enchName = new ResourceLocation(value[0].trim());
            int min = Integer.parseInt(value[1].trim());
            int max = Integer.parseInt(value[2].trim());
            effect.addEffect(new EngravingEffect.EnchantmentEffect(() -> ForgeRegistries.ENCHANTMENTS.getValue(enchName), min, max));
        }
        effect.addEffect(new EngravingEffect.ModifierEffect(() -> toolIncreasePerk, ModifierType.ADDED_MULTIPLY, 0.1F, 0.25F).addApplicableType(EnchantmentType.DIGGER));
        effect.addEffect(new EngravingEffect.ModifierEffect(() -> auraProtectionPerk, ModifierType.ADDED_MULTIPLY, 1, 1).addApplicableType(EnchantmentType.ARMOR));
        event.getRegistry().register(effect);
    }

    @SubscribeEvent
    public static void registerCrystalProperties(RegistryEvent.Register<CrystalProperty> event) {
        event.getRegistry().register(new PropertyConstellation(NARITIS.get()));
    }

    @SubscribeEvent
    public static void registerPerkAttributes(RegistryEvent.Register<PerkAttributeType> event) {
        event.getRegistry().registerAll(
                toolIncreasePerk = new ToolIncreasePerk(),
                auraProtectionPerk = new AuraProtectionPerk());
    }

    @SubscribeEvent
    public static void registerMantleEffects(RegistryEvent.Register<MantleEffect> event) {
        event.getRegistry().register(new NaritisMantleEffect(NARITIS.get()));
    }
}
