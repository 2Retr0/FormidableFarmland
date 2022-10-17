package retr0.formidablefarmland;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormidableFarmland implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	public static EnderChestState enderChestState;

	@Override
	public void onInitialize() {
		/**
		 * Send a packet to the server, have the handler on the server modify the stacks and make sure the slots are marked dirty.
		 */


		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		/**
		 * Loose overview is
		 * 1. get the persistent state manager from a ServerWorld
		 * 2. it has PersistentStateManager.getOrCreate() which takes
		 *         an id
		 *         a Function that converts NBT to a PersistentState
		 *         a Supplier that creates a PersistentState from nothing
		 * 3. Create a subclass of PersistentState. This stores your data in fields and saves it via writeNbt
		 * 4. When you want your data, call PersistentStateManager.getOrCreate(). If no state with the given id exists,
		 *    it creates it with the Supplier argument. Otherwise it reads it from NBT with the Function argument.
		 *
		 * As Matti mentions, you can also use PersistentStateManager.getOrCreate() in ServerWorldEvents.LOAD to ensure
		 * the state exists, then use the PersistentStateManager.get() elsewhere
		 */

		LOGGER.info("Hello Fabric world!");
		ServerWorldEvents.LOAD.register(((server, world) -> {
			if (world.getRegistryKey() != World.OVERWORLD) return;

			enderChestState = server.getOverworld().getPersistentStateManager().getOrCreate(EnderChestState.createState()::readNbt, EnderChestState::createState, "inventory_test");

			enderChestState.getInventory().addListener(inventory -> {
				LOGGER.info("BEEB!");
				enderChestState.markDirty();
			});

			LOGGER.info(enderChestState.getInventory().toString());
		}));

		// ServerPlayerEntity#openHandledScreen   then, when we close...
		//     -> ServerPlayerEntity#closeScreenHandler
		//     -> GenericContainerScreenHandler#close
		//     -> EnderChestInventory#onClose
		//            * Checks the inventory's activeBlockEntity, but it is never set.
		// We should instead close the player's EnderChestInventory's activeBlockEntity and set it to null!
	}
}
