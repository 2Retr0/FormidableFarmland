package retr0.formidablefarmland.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
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
import retr0.formidablefarmland.FormidableFarmland;

@Mixin(ShovelItem.class)
public abstract class MixinShovelItem {
    @Unique private static BlockState blockState;

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
        MixinShovelItem.blockState = blockState;
    }

    // TODO: NOT A CLEAN SOLUTION AS DIRT CAN ACCIDENTALLY BE MADE INTO PATH
    @ModifyVariable(
        method = "useOnBlock",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;",
            shift = At.Shift.BY,
            by = 2),
        ordinal = 1
    )
    public BlockState useOnFarmland(BlockState blockState2) {
        return blockState.getBlock().equals(Blocks.FARMLAND) ? Blocks.DIRT.getDefaultState() : blockState2;
    }
}
