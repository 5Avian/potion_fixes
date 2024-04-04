package fiveavian.potion_fixes.mixin;

import fiveavian.potion_fixes.PotionFixes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BrewingStandScreenHandler.class)
public abstract class BrewingStandScreenHandlerMixin extends ScreenHandler {
    protected BrewingStandScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Shadow
    public abstract ItemStack quickMove(PlayerEntity player, int slot);
    @Shadow
    public abstract boolean canUse(PlayerEntity player);

    @Override
    public boolean insertItem(ItemStack source, int startIndex, int endIndex, boolean fromLast) {
        return PotionFixes.insertItem(slots, source, startIndex, endIndex, fromLast);
    }

    @Redirect(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", ordinal = 0))
    public int quickMove_getCount(ItemStack instance) {
        return 1;
    }
}
