package net.mcreator.mortysiumsmp.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerEvent;

import net.minecraft.world.entity.Entity;

import net.mcreator.mortysiumsmp.network.MortysiumsmpModVariables;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class EntraRemoveChatsProcedure {
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;

		entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
			capability.exibindo_chat = false;
			capability.exibindo_chat2 = false;
			capability.exibindo_chat3 = false;
			capability.message = "";
			capability.message2 = "";
			capability.message3 = "";
			capability.fade_chat = "";
			capability.fade_chat2 = "";
			capability.fade_chat3 = "";
			capability.syncPlayerVariables(entity);
		});
	}
}