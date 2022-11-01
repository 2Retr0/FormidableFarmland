package retr0.formidablefarmland.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ShovelItem.class)
public abstract class MixinShovelItem {
    @Unique private BlockPos blockPos;
    @Unique private BlockState blockState;

    /**
     * Caches the proper local variables to be used in the 'useOnFarmland' ModifyVariable.
     */
    @Inject(
        method = "useOnBlock",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"),
        locals = LocalCapture.CAPTURE_FAILSOFT)
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
        // Farmland->dirt replacement takes priority over dirt->path replacement.
        if (blockPos != null && blockState.getBlock().equals(Blocks.FARMLAND))
            return Blocks.DIRT.getDefaultState();

        return original;
    }
}
