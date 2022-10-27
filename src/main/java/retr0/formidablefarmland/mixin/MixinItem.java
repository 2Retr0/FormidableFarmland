package retr0.formidablefarmland.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("CancellableInjectionUsage")
@Mixin(Item.class)
public abstract class MixinItem {
    @Inject(method = "finishUsing", at = @At("TAIL"), cancellable = true)
    public void onFinishUsing(
        ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir)
    { }
}
