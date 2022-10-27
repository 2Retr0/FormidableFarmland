package retr0.formidablefarmland.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import retr0.formidablefarmland.FormidableFarmland;

import java.util.LinkedHashSet;
import java.util.Set;

@Mixin(ShovelItem.class)
public abstract class MixinShovelItem extends MixinItem {
    @Unique private BlockPos blockPos;
    @Unique private BlockState blockState;
    @Unique private BlockPos lastFlattenedBlockPos;

    /**
     * Caches the proper local variables to be used in the 'useOnFarmland' ModifyVariable.
     */
    @Inject(
        method = "useOnBlock",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void getBlockState(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir,
                              World world, BlockPos blockPos, BlockState blockState)
    {
        this.blockPos = blockPos;
        this.blockState = blockState;
    }



    /**
     * Handles logic for determining when farmland→dirt replacement and dirt→path replacement should occur.
     */
    @ModifyVariable(method = "useOnBlock", at = @At(value = "STORE"), ordinal = 1)
    public BlockState useOnFarmland(BlockState original)
    {
        if (blockPos == null) return original; // If the 'getBlockState' Inject fails exit immediately!

        // Farmland->dirt replacement takes priority over dirt->path replacement.
        if (blockState.getBlock().equals(Blocks.FARMLAND)) {
            lastFlattenedBlockPos = blockPos;
            return Blocks.DIRT.getDefaultState();
        }
        // Withold dirt->path replacement after flattening farmland unless the player has moved their cursor to a
        // different block (or has released right-click).
        if (!blockPos.equals(lastFlattenedBlockPos)) {
            lastFlattenedBlockPos = null;
            return original;
        }
        return null;
    }



    /**
     * Re-allows dirt→path replacement on any block.
     */
    @Override
    public void onFinishUsing(
        ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir)
    {
        lastFlattenedBlockPos = null;
        cir.setReturnValue(stack);
    }
}
