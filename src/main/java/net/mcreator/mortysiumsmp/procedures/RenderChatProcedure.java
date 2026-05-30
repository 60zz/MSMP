package net.mcreator.mortysiumsmp.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;

import net.mcreator.mortysiumsmp.MortysiumsmpMod;
import net.mcreator.mortysiumsmp.network.ChatBroadcastPacket;
import net.mcreator.mortysiumsmp.network.MortysiumsmpModVariables;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Mod.EventBusSubscriber
public class RenderChatProcedure {

    private static final int MESSAGE_CHUNK_SIZE = 42;
    private static final int CLEAR_DELAY_TICKS  = 160;

    // Contador por jogador — garante IDs únicos sem depender de nanoTime ou strings sincronizadas
    private static final Map<UUID, AtomicLong> COUNTERS = new ConcurrentHashMap<>();

    private static long nextId(UUID uuid) {
        return COUNTERS.computeIfAbsent(uuid, k -> new AtomicLong(0)).incrementAndGet();
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        execute(event, event.getPlayer().level(), event.getPlayer(), event.getRawText());
    }

    public static void execute(LevelAccessor world, Entity entity, String text) {
        execute(null, world, entity, text);
    }

    private static void execute(@Nullable Event event, LevelAccessor world, Entity entity, String text) {
        if (entity == null || text == null) return;

        for (String part : splitMessage(text)) {
            enqueueChatPart(entity, part);
        }

        if (!(entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                .orElseGet(MortysiumsmpModVariables.PlayerVariables::new)).chat_global) {
            if (event != null && event.isCancelable()) {
                event.setCanceled(true);
            }
        }
    }

    private static java.util.List<String> splitMessage(String text) {
        String normalized = text.trim().replaceAll("\\s+", " ");
        java.util.List<String> parts = new java.util.ArrayList<>();
        if (normalized.isEmpty()) return parts;

        int start = 0;
        while (start < normalized.length()) {
            int end = Math.min(start + MESSAGE_CHUNK_SIZE, normalized.length());
            if (end < normalized.length()) {
                int breakAt = normalized.lastIndexOf(' ', end);
                if (breakAt > start) end = breakAt;
            }
            String part = normalized.substring(start, end).trim();
            if (!part.isEmpty()) parts.add(" " + part + " ");
            start = end;
            while (start < normalized.length() && normalized.charAt(start) == ' ') start++;
        }
        return parts;
    }

    private static void enqueueChatPart(Entity entity, String part) {
        // ID gerado ANTES do shift, único por jogador, sem depender de tempo
        final long msgId = nextId(entity.getUUID());

        entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(cap -> {
            shiftMessagesDown(cap);
            cap.message       = part;
            cap.exibindo_chat = true;
            // Guardamos o ID numérico como string no fade_chat — só usado internamente no servidor
            cap.fade_chat     = String.valueOf(msgId);
            cap.syncPlayerVariables(entity);
        });
        broadcast(entity);

        MortysiumsmpMod.queueServerWork(CLEAR_DELAY_TICKS, () -> {
            entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(cap -> {
                String idStr = String.valueOf(msgId);
                int slot = findSlotById(cap, idStr);
                if (slot != -1) {
                    clearSlot(cap, slot);
                    cap.syncPlayerVariables(entity);
                    broadcast(entity);
                }
            });
        });
    }

    private static void shiftMessagesDown(MortysiumsmpModVariables.PlayerVariables cap) {
        cap.message3       = cap.message2;
        cap.exibindo_chat3 = cap.exibindo_chat2;
        cap.fade_chat3     = cap.fade_chat2;

        cap.message2       = cap.message;
        cap.exibindo_chat2 = cap.exibindo_chat;
        cap.fade_chat2     = cap.fade_chat;
    }

    private static int findSlotById(MortysiumsmpModVariables.PlayerVariables cap, String idStr) {
        if (idStr.equals(cap.fade_chat))  return 1;
        if (idStr.equals(cap.fade_chat2)) return 2;
        if (idStr.equals(cap.fade_chat3)) return 3;
        return -1;
    }

    private static void clearSlot(MortysiumsmpModVariables.PlayerVariables cap, int slot) {
        switch (slot) {
            case 1 -> { cap.exibindo_chat  = false; cap.message  = ""; cap.fade_chat  = ""; }
            case 2 -> { cap.exibindo_chat2 = false; cap.message2 = ""; cap.fade_chat2 = ""; }
            case 3 -> { cap.exibindo_chat3 = false; cap.message3 = ""; cap.fade_chat3 = ""; }
        }
    }

    private static void broadcast(Entity entity) {
        if (entity instanceof ServerPlayer sp) ChatBroadcastPacket.broadcast(sp);
    }
}