package net.mcreator.mortysiumsmp.network;

import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;

import net.mcreator.mortysiumsmp.MortysiumsmpMod;
import net.mcreator.mortysiumsmp.network.MortysiumsmpModVariables;
import net.mcreator.mortysiumsmp.network.MortysiumsmpModVariables.PlayerVariables;

import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ChatBroadcastPacket {

    // Mapa estático client-side: UUID do jogador → suas variáveis de chat
    // O MostraChatProcedure lê daqui em vez da capability
    public static final Map<UUID, PlayerVariables> CLIENT_CHAT_DATA = new ConcurrentHashMap<>();

    private final UUID ownerUUID;
    private final String message, message2, message3;
    private final boolean exibindo_chat, exibindo_chat2, exibindo_chat3;
    private final String fade_chat, fade_chat2, fade_chat3;
    private final double R1, G1, B1, R2, G2, B2;

    public ChatBroadcastPacket(UUID ownerUUID, PlayerVariables vars) {
        this.ownerUUID = ownerUUID;
        this.message   = vars.message;
        this.message2  = vars.message2;
        this.message3  = vars.message3;
        this.exibindo_chat  = vars.exibindo_chat;
        this.exibindo_chat2 = vars.exibindo_chat2;
        this.exibindo_chat3 = vars.exibindo_chat3;
        this.fade_chat  = vars.fade_chat;
        this.fade_chat2 = vars.fade_chat2;
        this.fade_chat3 = vars.fade_chat3;
        this.R1 = vars.R1; this.G1 = vars.G1; this.B1 = vars.B1;
        this.R2 = vars.R2; this.G2 = vars.G2; this.B2 = vars.B2;
    }

    public ChatBroadcastPacket(FriendlyByteBuf buffer) {
        this.ownerUUID  = buffer.readUUID();
        this.message    = buffer.readUtf();
        this.message2   = buffer.readUtf();
        this.message3   = buffer.readUtf();
        this.exibindo_chat  = buffer.readBoolean();
        this.exibindo_chat2 = buffer.readBoolean();
        this.exibindo_chat3 = buffer.readBoolean();
        this.fade_chat  = buffer.readUtf();
        this.fade_chat2 = buffer.readUtf();
        this.fade_chat3 = buffer.readUtf();
        this.R1 = buffer.readDouble(); this.G1 = buffer.readDouble(); this.B1 = buffer.readDouble();
        this.R2 = buffer.readDouble(); this.G2 = buffer.readDouble(); this.B2 = buffer.readDouble();
    }

    public static void buffer(ChatBroadcastPacket msg, FriendlyByteBuf buffer) {
        buffer.writeUUID(msg.ownerUUID);
        buffer.writeUtf(msg.message);
        buffer.writeUtf(msg.message2);
        buffer.writeUtf(msg.message3);
        buffer.writeBoolean(msg.exibindo_chat);
        buffer.writeBoolean(msg.exibindo_chat2);
        buffer.writeBoolean(msg.exibindo_chat3);
        buffer.writeUtf(msg.fade_chat);
        buffer.writeUtf(msg.fade_chat2);
        buffer.writeUtf(msg.fade_chat3);
        buffer.writeDouble(msg.R1); buffer.writeDouble(msg.G1); buffer.writeDouble(msg.B1);
        buffer.writeDouble(msg.R2); buffer.writeDouble(msg.G2); buffer.writeDouble(msg.B2);
    }

    public static void handler(ChatBroadcastPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (!context.getDirection().getReceptionSide().isServer()) {
                // Grava no mapa estático — não depende de level.players()
                PlayerVariables vars = new PlayerVariables();
                vars.message        = msg.message;
                vars.message2       = msg.message2;
                vars.message3       = msg.message3;
                vars.exibindo_chat  = msg.exibindo_chat;
                vars.exibindo_chat2 = msg.exibindo_chat2;
                vars.exibindo_chat3 = msg.exibindo_chat3;
                vars.fade_chat      = msg.fade_chat;
                vars.fade_chat2     = msg.fade_chat2;
                vars.fade_chat3     = msg.fade_chat3;
                vars.R1 = msg.R1; vars.G1 = msg.G1; vars.B1 = msg.B1;
                vars.R2 = msg.R2; vars.G2 = msg.G2; vars.B2 = msg.B2;
                CLIENT_CHAT_DATA.put(msg.ownerUUID, vars);
            }
        });
        context.setPacketHandled(true);
    }

    public static void broadcast(ServerPlayer serverPlayer) {
        serverPlayer.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(vars -> {
            ChatBroadcastPacket packet = new ChatBroadcastPacket(serverPlayer.getUUID(), vars);
            MortysiumsmpMod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), packet);
        });
    }
}