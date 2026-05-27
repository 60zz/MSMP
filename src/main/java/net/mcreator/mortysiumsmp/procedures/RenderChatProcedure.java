package net.mcreator.mortysiumsmp.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.ServerChatEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;

import net.mcreator.mortysiumsmp.network.MortysiumsmpModVariables;
import net.mcreator.mortysiumsmp.network.ChatBroadcastPacket;
import net.mcreator.mortysiumsmp.MortysiumsmpMod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class RenderChatProcedure {
	@SubscribeEvent
	public static void onChat(ServerChatEvent event) {
		execute(event, event.getPlayer().level(), event.getPlayer(), event.getRawText());
	}

	public static void execute(LevelAccessor world, Entity entity, String text) {
		execute(null, world, entity, text);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, String text) {
		if (entity == null || text == null)
			return;

		java.util.List<String> partes = new java.util.ArrayList<>();
		String restante = text;
		while (restante.length() > 33) {
			partes.add(restante.substring(0, 33));
			restante = restante.substring(33);
		}
		if (!restante.isEmpty()) partes.add(restante);

		for (String parte : partes) {
			enviarParte(entity, parte);
		}

		if ((entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElseGet(MortysiumsmpModVariables.PlayerVariables::new)).chat_global == false) {
			if (event != null && event.isCancelable()) {
				event.setCanceled(true);
			}
		}
	}

	private static void enviarParte(Entity entity, String text) {
		if ((entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElseGet(MortysiumsmpModVariables.PlayerVariables::new)).exibindo_chat == false) {
			entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.message = " " + text + " ";
				capability.exibindo_chat = true;
				capability.fade_chat = "";
				capability.syncPlayerVariables(entity);
			});
			// broadcast já usa PacketDistributor.ALL — envia para todos
			if (entity instanceof ServerPlayer sp) ChatBroadcastPacket.broadcast(sp);

			MortysiumsmpMod.queueServerWork(160, () -> {
				if ((" " + text + " ").equals((entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElseGet(MortysiumsmpModVariables.PlayerVariables::new)).message)) {
					entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.fade_chat = String.valueOf(System.currentTimeMillis());
						capability.syncPlayerVariables(entity);
					});
					if (entity instanceof ServerPlayer sp) ChatBroadcastPacket.broadcast(sp);
				}
			});
			MortysiumsmpMod.queueServerWork(200, () -> {
				if ((" " + text + " ").equals((entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElseGet(MortysiumsmpModVariables.PlayerVariables::new)).message)) {
					entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.exibindo_chat = false;
						capability.message = "";
						capability.fade_chat = "";
						capability.syncPlayerVariables(entity);
					});
					if (entity instanceof ServerPlayer sp) ChatBroadcastPacket.broadcast(sp);
				}
			});
		} else if ((entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElseGet(MortysiumsmpModVariables.PlayerVariables::new)).exibindo_chat2 == false) {
			entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.message2 = " " + text + " ";
				capability.exibindo_chat2 = true;
				capability.fade_chat2 = "";
				capability.syncPlayerVariables(entity);
			});
			if (entity instanceof ServerPlayer sp) ChatBroadcastPacket.broadcast(sp);

			MortysiumsmpMod.queueServerWork(160, () -> {
				if ((" " + text + " ").equals((entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElseGet(MortysiumsmpModVariables.PlayerVariables::new)).message2)) {
					entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.fade_chat2 = String.valueOf(System.currentTimeMillis());
						capability.syncPlayerVariables(entity);
					});
					if (entity instanceof ServerPlayer sp) ChatBroadcastPacket.broadcast(sp);
				}
			});
			MortysiumsmpMod.queueServerWork(200, () -> {
				if ((" " + text + " ").equals((entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElseGet(MortysiumsmpModVariables.PlayerVariables::new)).message2)) {
					entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.exibindo_chat2 = false;
						capability.message2 = "";
						capability.fade_chat2 = "";
						capability.syncPlayerVariables(entity);
					});
					if (entity instanceof ServerPlayer sp) ChatBroadcastPacket.broadcast(sp);
				}
			});
		} else if ((entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElseGet(MortysiumsmpModVariables.PlayerVariables::new)).exibindo_chat3 == false) {
			entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.message3 = " " + text + " ";
				capability.exibindo_chat3 = true;
				capability.fade_chat3 = "";
				capability.syncPlayerVariables(entity);
			});
			if (entity instanceof ServerPlayer sp) ChatBroadcastPacket.broadcast(sp);

			MortysiumsmpMod.queueServerWork(160, () -> {
				if ((" " + text + " ").equals((entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElseGet(MortysiumsmpModVariables.PlayerVariables::new)).message3)) {
					entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.fade_chat3 = String.valueOf(System.currentTimeMillis());
						capability.syncPlayerVariables(entity);
					});
					if (entity instanceof ServerPlayer sp) ChatBroadcastPacket.broadcast(sp);
				}
			});
			MortysiumsmpMod.queueServerWork(200, () -> {
				if ((" " + text + " ").equals((entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElseGet(MortysiumsmpModVariables.PlayerVariables::new)).message3)) {
					entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
						capability.exibindo_chat3 = false;
						capability.message3 = "";
						capability.fade_chat3 = "";
						capability.syncPlayerVariables(entity);
					});
					if (entity instanceof ServerPlayer sp) ChatBroadcastPacket.broadcast(sp);
				}
			});
		}
	}
}