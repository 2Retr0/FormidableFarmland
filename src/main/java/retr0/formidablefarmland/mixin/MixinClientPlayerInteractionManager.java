package retr0.formidablefarmland.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import retr0.formidablefarmland.extension.ExtensionMinecraftClient;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {
    @Shadow @Final private MinecraftClient client;

    @Unique private BlockPos lastInteractedBlockPos = BlockPos.ORIGIN;

    /**
     * Handles logic (shovels + hoes) for preventing (and eventually resuming) block replacement following successful
     * interaction.
     */
    @Inject(
        method = "interactBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V"),
        cancellable = true)
    public void onShovelInteract(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult,
                                 CallbackInfoReturnable<ActionResult> cir)
    {
        var handItem = player.getStackInHand(hand).getItem();
        var exClient = (ExtensionMinecraftClient) client;
        var targetBlockPos = hitResult.getBlockPos();

        // We also consider hoes as well to ensure that--with a hoe equipped in one hand and a shovel equipped in the
        // other--we don't have tools instantly overwrite the use-action (i.e. dirt->farmland + farmland->dirt) of one
        // another.
        if (!(handItem instanceof ShovelItem || handItem instanceof HoeItem)) return;

        // Withold dirt->path replacement after flattening farmland unless the player has flattened a different block,
        // swapped items, or has released right-click.
        if (lastInteractedBlockPos.equals(targetBlockPos) && !exClient.isUseInputAllowed()) {
            cir.setReturnValue(ActionResult.PASS); cir.cancel();
        } else {
            exClient.onUseTool();
        }
        lastInteractedBlockPos = targetBlockPos;
    }
}
