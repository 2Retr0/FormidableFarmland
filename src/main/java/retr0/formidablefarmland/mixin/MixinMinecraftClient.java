package retr0.formidablefarmland.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import retr0.formidablefarmland.extension.ExtensionMinecraftClient;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements ExtensionMinecraftClient {
    @Shadow @Final public GameOptions options;

    @Unique private boolean isUseInputAllowed = true;

    /**
     * Detects when the player has released right-click (i.e. after right-clicking to use a shovel/hoe).
     */
    @Inject(method = "handleInputEvents", at = @At("TAIL"))
    public void handleUseButton(CallbackInfo ci) {
        if (!isUseInputAllowed && !options.useKey.isPressed()) isUseInputAllowed = true;
    }



    /**
     * Detects when the player has swapped to a different hotbar slot.
     */
    @Inject(
        method = "handleInputEvents",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;"))
    public void handleHotbarSwap(CallbackInfo ci) {
        isUseInputAllowed = true;
    }

    @Override public boolean isUseInputAllowed() { return isUseInputAllowed; }

    @Override public void onUseTool() { isUseInputAllowed = false; }
}
