package fiveavian.potion_fixes.mixin;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin extends BlockWithEntity {
    @Unique
    private static final FallibleItemDispenserBehavior POTION_BEHAVIOR = new FallibleItemDispenserBehavior() {
        private static final ItemDispenserBehavior FALLBACK_BEHAVIOR = new ItemDispenserBehavior();

        @Override
        protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            if (PotionUtil.getPotion(stack) != Potions.WATER)
                return FALLBACK_BEHAVIOR.dispense(pointer, stack);
            var world = pointer.getWorld();
            var pos = pointer.getPos();
            var targetPos = pos.offset(pointer.getBlockState().get(DispenserBlock.FACING));
            if (!world.getBlockState(targetPos).isIn(BlockTags.CONVERTABLE_TO_MUD))
                return FALLBACK_BEHAVIOR.dispense(pointer, stack);
            for (int i = 0; i < 5; i++)
                world.spawnParticles(
                        ParticleTypes.SPLASH,
                        pos.getX() + world.random.nextDouble(),
                        pos.getY() + 1.0,
                        pos.getZ() + world.random.nextDouble(),
                        1, 0.0, 0.0, 0.0, 1.0
                );
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            world.setBlockState(targetPos, Blocks.MUD.getDefaultState());
            stack.decrement(1);
            if (stack.isEmpty())
                return new ItemStack(Items.GLASS_BOTTLE);
            var dispenser = (DispenserBlockEntity) pointer.getBlockEntity();
            if (dispenser.addToFirstFreeSlot(new ItemStack(Items.GLASS_BOTTLE)) < 0)
                FALLBACK_BEHAVIOR.dispense(pointer, new ItemStack(Items.GLASS_BOTTLE));
            return stack;
        }
    };

    @Shadow
    @Final
    private static Map<Item, DispenserBehavior> BEHAVIORS;

    private DispenserBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "registerBehavior", at = @At("HEAD"), cancellable = true)
    private static void registerBehavior(ItemConvertible provider, DispenserBehavior behavior, CallbackInfo ci) {
        if (provider.asItem() != Items.POTION)
            return;
        BEHAVIORS.put(Items.POTION, POTION_BEHAVIOR);
        ci.cancel();
    }
}
