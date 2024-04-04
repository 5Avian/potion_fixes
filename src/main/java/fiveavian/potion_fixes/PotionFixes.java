package fiveavian.potion_fixes;

import fiveavian.potion_fixes.mixin.BrewingRecipeRegistryAccessor;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.service.MixinService;

public class PotionFixes implements ModInitializer {
    public static final String MOD_ID = "potion_fixes";
    public static final ILogger LOGGER = MixinService.getService().getLogger(MOD_ID);

    public static boolean insertItem(DefaultedList<Slot> slots, ItemStack source, int startIndex, int endIndex, boolean fromLast) {
        var inserted = false;
        for (
                var i = fromLast ? endIndex - 1 : startIndex;
                !source.isEmpty() && i >= 0 && i < endIndex;
                i += fromLast ? -1 : 1
        ) {
            var slot = slots.get(i);
            var target = slot.getStack();
            if (target.isEmpty() || !slot.canInsert(source) || !ItemStack.canCombine(source, target))
                continue;
            var maxCount = Math.min(slot.getMaxItemCount(), source.getMaxCount());
            var toCombine = Math.min(source.getCount(), maxCount - target.getCount());
            if (toCombine <= 0)
                continue;
            source.decrement(toCombine);
            target.increment(toCombine);
            slot.markDirty();
            inserted = true;
        }
        if (source.isEmpty())
            return inserted;
        for (
                var i = fromLast ? endIndex - 1 : startIndex;
                !source.isEmpty() && i >= 0 && i < endIndex;
                i += fromLast ? -1 : 1
        ) {
            var slot = slots.get(i);
            var target = slot.getStack();
            if (!target.isEmpty() || !slot.canInsert(source))
                continue;
            var targetCount = Math.min(slot.getMaxItemCount(), source.getCount());
            slot.setStack(source.split(targetCount));
            slot.markDirty();
            inserted = true;
            break;
        }
        return inserted;
    }

    public static boolean isPotionType(ItemStack stack) {
        return BrewingRecipeRegistryAccessor.getPotionTypes().stream().anyMatch(ingredient -> ingredient.test(stack));
    }

    @Override
    public void onInitialize() {}
}
