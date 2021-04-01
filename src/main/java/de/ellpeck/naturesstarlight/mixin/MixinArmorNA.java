package de.ellpeck.naturesstarlight.mixin;

import de.ellpeck.naturesaura.items.ModItems;
import de.ellpeck.naturesaura.items.tools.ItemArmor;
import de.ellpeck.naturesaura.reg.ModArmorMaterial;
import de.ellpeck.naturesstarlight.Registry;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Lazy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemArmor.class)
public class MixinArmorNA extends ArmorItem {

    private static final Lazy<Item[]> SET = Lazy.of(() -> new Item[]{ModItems.SKY_SHOES, ModItems.SKY_PANTS, ItemsAS.MANTLE, ModItems.SKY_HELMET});

    public MixinArmorNA(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builderIn) {
        super(materialIn, slot, builderIn);
    }

    @Inject(at = @At("HEAD"), method = "isFullSetEquipped(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/IArmorMaterial;)Z", remap = false, cancellable = true)
    private static void isFullSetEquipped(LivingEntity entity, IArmorMaterial material, CallbackInfoReturnable<Boolean> callback) {
        // allow the mantle to be used as a skyseeker's chestplate
        if (material == ModArmorMaterial.SKY) {
            for (int i = 0; i < 4; i++) {
                EquipmentSlotType slot = EquipmentSlotType.values()[i + 2];
                ItemStack stack = entity.getItemStackFromSlot(slot);
                // if the set item doesn't match, we let the default behavior take over
                if (stack.getItem() != SET.get()[i])
                    return;
                // if the mantle doesn't have naritis, we immediately return false
                if (slot == EquipmentSlotType.CHEST && ItemMantle.getEffect(stack, Registry.NARITIS.get()) == null) {
                    callback.setReturnValue(false);
                    return;
                }
            }
            // if all slots pass, we return true
            callback.setReturnValue(true);
        }
    }
}
