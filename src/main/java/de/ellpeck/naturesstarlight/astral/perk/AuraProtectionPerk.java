package de.ellpeck.naturesstarlight.astral.perk;

import de.ellpeck.naturesaura.potion.ModPotions;
import de.ellpeck.naturesstarlight.NaturesStarlight;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

public class AuraProtectionPerk extends PerkAttributeType {

    public AuraProtectionPerk() {
        super(new ResourceLocation(NaturesStarlight.ID, "aura_protection"));
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        eventBus.addListener(this::onPotionAdded);
    }

    private void onPotionAdded(PotionEvent.PotionApplicableEvent event) {
        if (event.getPotionEffect().getPotion() != ModPotions.BREATHLESS)
            return;
        LivingEntity entity = event.getEntityLiving();
        if (!(entity instanceof PlayerEntity))
            return;
        LogicalSide side = this.getSide(entity);
        if (this.hasTypeApplied((PlayerEntity) entity, side))
            event.setResult(Event.Result.DENY);
    }
}
