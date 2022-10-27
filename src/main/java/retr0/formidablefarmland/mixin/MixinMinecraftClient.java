package retr0.formidablefarmland.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ShovelItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import retr0.formidablefarmland.FormidableFarmland;

import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.SHOVELS;
import static retr0.formidablefarmland.FormidableFarmland.LOGGER;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
    @Shadow @Final public GameOptions options;
    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Nullable public ClientWorld world;

    @Unique private boolean isUsedKeyPressed;

    /**
     * Detects when the player has released right-click and—synced with the server—resets the player-held shovel
     * to re-allow dirt→path replacement on any block.
     */
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "handleInputEvents", at = @At("TAIL"))
    public void handleUseButton(CallbackInfo ci) {

        if (!isUsedKeyPressed && options.useKey.isPressed()) {
            isUsedKeyPressed = true;
        } else if (isUsedKeyPressed && !options.useKey.isPressed() && player.preferredHand != null) {
            isUsedKeyPressed = false;
            ClientPlayNetworking.send(FormidableFarmland.SYNC_SHOVEL_STATE, PacketByteBufs.empty());

            var handStack = player.getStackInHand(player.preferredHand);
            if (handStack.getItem() instanceof ShovelItem shovelItem) {
                shovelItem.finishUsing(handStack, world, player);
            }

            handStack.finishUsing(world, player);
        }
    }
}
