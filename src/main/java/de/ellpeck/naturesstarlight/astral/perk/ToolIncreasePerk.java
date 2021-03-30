package de.ellpeck.naturesstarlight.astral.perk;

import de.ellpeck.naturesaura.api.aura.chunk.IAuraChunk;
import de.ellpeck.naturesstarlight.NaturesStarlight;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.perk.type.vanilla.VanillaAttributeType;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ToolIncreasePerk extends VanillaAttributeType {

    private static final UUID ID = UUID.fromString("869d6a47-62c2-47ca-8d3d-41226566a1a4");

    public ToolIncreasePerk() {
        super(new ResourceLocation(NaturesStarlight.ID, "tool_increase"));
    }

    @Override
    protected void attachListeners(IEventBus eventBus) {
        eventBus.addListener(EventPriority.LOW, this::onBreakSpeed);
    }

    @Override
    public UUID getID(ModifierType mode) {
        return ID;
    }

    @Override
    public String getDescription() {
        return "Perk ToolIncrease";
    }

    @Nonnull
    @Override
    public Attribute getAttribute() {
        return Attributes.ATTACK_DAMAGE;
    }

    private void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        PlayerEntity player = event.getPlayer();
        if (!shouldWorkRightNow(player))
            return;
        LogicalSide side = this.getSide(player);
        if (!this.hasTypeApplied(player, side))
            return;
        float speed = PerkAttributeHelper.getOrCreateMap(player, side)
                .modifyValue(player, ResearchHelper.getProgress(player, side), this, event.getNewSpeed());
        speed = AttributeEvent.postProcessModded(player, this, speed);
        event.setNewSpeed(speed);
    }

    private static boolean shouldWorkRightNow(PlayerEntity player) {
        int aura = IAuraChunk.getAuraInArea(player.world, player.getPosition(), 30);
        return aura >= 1500000;
    }

    // basically the super method, just with attribute modifier instances replaced with our own
    @Override
    public void onModeApply(PlayerEntity player, ModifierType mode, LogicalSide side) {
        ModifiableAttributeInstance attr = player.getAttributeManager().createInstanceIfAbsent(this.getAttribute());
        if (attr == null)
            return;
        if (side.isClient()) {
            AttributeModifier modifier;
            if ((modifier = attr.getModifier(this.getID(mode))) != null) {
                if (!(modifier instanceof Modifier)) {
                    attr.removeModifier(this.getID(mode));
                } else {
                    return;
                }
            }
        }
        switch (mode) {
            case ADDITION:
                attr.applyNonPersistentModifier(new Modifier(this.getID(mode), this.getDescription() + " Add", this, mode, player, side));
                break;
            case ADDED_MULTIPLY:
                attr.applyNonPersistentModifier(new Modifier(this.getID(mode), this.getDescription() + " Multiply Add", this, mode, player, side));
                break;
            case STACKING_MULTIPLY:
                attr.applyNonPersistentModifier(new Modifier(this.getID(mode), this.getDescription() + " Stack Add", this, mode, player, side));
                break;
            default:
                break;
        }
    }

    // copied and edited from VanillaAttributeType to allow shouldWorkRightNow check
    private static class Modifier extends AttributeModifier {

        private final PlayerEntity player;
        private final LogicalSide side;
        private final PerkAttributeType type;

        public Modifier(UUID idIn, String nameIn, PerkAttributeType type, ModifierType mode, PlayerEntity player, LogicalSide side) {
            this(idIn, nameIn, type, mode.getVanillaAttributeOperation(), player, side);
        }

        public Modifier(UUID idIn, String nameIn, PerkAttributeType type, Operation operationIn, PlayerEntity player, LogicalSide side) {
            super(idIn, nameIn, operationIn == Operation.MULTIPLY_TOTAL ? 1 : 0, operationIn);
            this.player = player;
            this.side = side;
            this.type = type;
        }

        @Override
        public double getAmount() {
            if (!shouldWorkRightNow(this.player))
                return 0;
            ModifierType mode = ModifierType.fromVanillaAttributeOperation(this.getOperation());
            return PerkAttributeHelper.getOrCreateMap(this.player, this.side)
                    .getModifier(this.player, ResearchHelper.getProgress(this.player, this.side), this.type, mode) - 1;
        }

    }
}