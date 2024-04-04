package fiveavian.potion_fixes.mixin;

import fiveavian.potion_fixes.PotionFixes;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {"net/minecraft/screen/BrewingStandScreenHandler$PotionSlot"})
public class BrewingStandScreenHandlerPotionSlotMixin extends Slot {
    public BrewingStandScreenHandlerPotionSlotMixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(at = @At("HEAD"), method = "matches", cancellable = true)
    private static void matches(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (PotionFixes.isPotionType(stack))
            cir.setReturnValue(true);
    }
}
