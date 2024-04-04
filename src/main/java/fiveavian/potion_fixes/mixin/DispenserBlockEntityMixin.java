package fiveavian.potion_fixes.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DispenserBlockEntity.class)
public abstract class DispenserBlockEntityMixin extends LootableContainerBlockEntity {
    @Shadow
    private DefaultedList<ItemStack> inventory;

    private DispenserBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    /**
     * @author 5Avian
     * @reason add combining stacks
     */
    @Overwrite
    public int addToFirstFreeSlot(ItemStack source) {
        for (int i = 0; i < inventory.size(); i++) {
            var target = inventory.get(i);
            if (target.isEmpty()) {
                setStack(i, source);
                return i;
            } else if (ItemStack.canCombine(source, target) && source.getCount() + target.getCount() < source.getMaxCount()) {
                target.increment(source.getCount());
                source.setCount(0);
                return i;
            }
        }
        return -1;
    }
}
