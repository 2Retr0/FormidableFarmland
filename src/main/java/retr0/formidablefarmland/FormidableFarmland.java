package retr0.formidablefarmland;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags.SHOVELS;

public class FormidableFarmland implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "formidablefarmland";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier SYNC_SHOVEL_STATE = new Identifier(MOD_ID, "sync_shovel_state");

	@Override
	public void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(SYNC_SHOVEL_STATE,
			(server, player, handler, buf, responseSender) -> server.execute(() -> {
				var handStack = player.getStackInHand(player.preferredHand);
				if (handStack.getItem() instanceof ShovelItem shovelItem) {
					shovelItem.finishUsing(handStack, player.world, player);
				}
			}));
	}
}
