package net.mcreator.mortysiumsmp.network;

import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.client.Minecraft;

import net.mcreator.mortysiumsmp.MortysiumsmpMod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MortysiumsmpModVariables {
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		MortysiumsmpMod.addNetworkMessage(SavedDataSyncMessage.class, SavedDataSyncMessage::buffer, SavedDataSyncMessage::new, SavedDataSyncMessage::handler);
		MortysiumsmpMod.addNetworkMessage(PlayerVariablesSyncMessage.class, PlayerVariablesSyncMessage::buffer, PlayerVariablesSyncMessage::new, PlayerVariablesSyncMessage::handler);
	}

	@SubscribeEvent
	public static void init(RegisterCapabilitiesEvent event) {
		event.register(PlayerVariables.class);
	}

	@Mod.EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.getEntity().level().isClientSide())
				((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElseGet(PlayerVariables::new)).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
			if (!event.getEntity().level().isClientSide())
				((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElseGet(PlayerVariables::new)).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (!event.getEntity().level().isClientSide())
				((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElseGet(PlayerVariables::new)).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void clonePlayer(PlayerEvent.Clone event) {
			event.getOriginal().revive();
			PlayerVariables original = ((PlayerVariables) event.getOriginal().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElseGet(PlayerVariables::new));
			PlayerVariables clone = ((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElseGet(PlayerVariables::new));
			clone.R2 = original.R2;
			clone.R1 = original.R1;
			clone.message3 = original.message3;
			clone.message2 = original.message2;
			clone.message = original.message;
			clone.G2 = original.G2;
			clone.G1 = original.G1;
			clone.fade_chat3 = original.fade_chat3;
			clone.fade_chat2 = original.fade_chat2;
			clone.fade_chat = original.fade_chat;
			clone.exibindo_chat3 = original.exibindo_chat3;
			clone.exibindo_chat2 = original.exibindo_chat2;
			clone.exibindo_chat = original.exibindo_chat;
			clone.chat_global = original.chat_global;
			clone.B2 = original.B2;
			clone.B1 = original.B1;
			if (!event.isWasDeath()) {
			}
		}

		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (!event.getEntity().level().isClientSide()) {
				SavedData mapdata = MapVariables.get(event.getEntity().level());
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (mapdata != null)
					MortysiumsmpMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(0, mapdata));
				if (worlddata != null)
					MortysiumsmpMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (!event.getEntity().level().isClientSide()) {
				SavedData worlddata = WorldVariables.get(event.getEntity().level());
				if (worlddata != null)
					MortysiumsmpMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worlddata));
			}
		}
	}

	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "mortysiumsmp_worldvars";

		public static WorldVariables load(CompoundTag tag) {
			WorldVariables data = new WorldVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level level && !level.isClientSide())
				MortysiumsmpMod.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(level::dimension), new SavedDataSyncMessage(1, this));
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				return level.getDataStorage().computeIfAbsent(e -> WorldVariables.load(e), WorldVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "mortysiumsmp_mapvars";
		public boolean Tornado = false;
		public boolean Terremotos = false;
		public boolean FrioExtremo = false;
		public boolean ChuvaAcida = false;
		public boolean CalorExtremo = false;

		public static MapVariables load(CompoundTag tag) {
			MapVariables data = new MapVariables();
			data.read(tag);
			return data;
		}

		public void read(CompoundTag nbt) {
			Tornado = nbt.getBoolean("Tornado");
			Terremotos = nbt.getBoolean("Terremotos");
			FrioExtremo = nbt.getBoolean("FrioExtremo");
			ChuvaAcida = nbt.getBoolean("ChuvaAcida");
			CalorExtremo = nbt.getBoolean("CalorExtremo");
		}

		@Override
		public CompoundTag save(CompoundTag nbt) {
			nbt.putBoolean("Tornado", Tornado);
			nbt.putBoolean("Terremotos", Terremotos);
			nbt.putBoolean("FrioExtremo", FrioExtremo);
			nbt.putBoolean("ChuvaAcida", ChuvaAcida);
			nbt.putBoolean("CalorExtremo", CalorExtremo);
			return nbt;
		}

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level && !world.isClientSide())
				MortysiumsmpMod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SavedDataSyncMessage(0, this));
		}

		static MapVariables clientSide = new MapVariables();

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevelAccessor serverLevelAcc) {
				return serverLevelAcc.getLevel().getServer().getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(e -> MapVariables.load(e), MapVariables::new, DATA_NAME);
			} else {
				return clientSide;
			}
		}
	}

	public static class SavedDataSyncMessage {
		private final int type;
		private SavedData data;

		public SavedDataSyncMessage(FriendlyByteBuf buffer) {
			this.type = buffer.readInt();
			CompoundTag nbt = buffer.readNbt();
			if (nbt != null) {
				this.data = this.type == 0 ? new MapVariables() : new WorldVariables();
				if (this.data instanceof MapVariables mapVariables)
					mapVariables.read(nbt);
				else if (this.data instanceof WorldVariables worldVariables)
					worldVariables.read(nbt);
			}
		}

		public SavedDataSyncMessage(int type, SavedData data) {
			this.type = type;
			this.data = data;
		}

		public static void buffer(SavedDataSyncMessage message, FriendlyByteBuf buffer) {
			buffer.writeInt(message.type);
			if (message.data != null)
				buffer.writeNbt(message.data.save(new CompoundTag()));
		}

		public static void handler(SavedDataSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer() && message.data != null) {
					if (message.type == 0)
						MapVariables.clientSide = (MapVariables) message.data;
					else
						WorldVariables.clientSide = (WorldVariables) message.data;
				}
			});
			context.setPacketHandled(true);
		}
	}

	public static final Capability<PlayerVariables> PLAYER_VARIABLES_CAPABILITY = CapabilityManager.get(new CapabilityToken<PlayerVariables>() {
	});

	@Mod.EventBusSubscriber
	private static class PlayerVariablesProvider implements ICapabilitySerializable<Tag> {
		@SubscribeEvent
		public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
			if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer))
				event.addCapability(ResourceLocation.fromNamespaceAndPath("mortysiumsmp", "player_variables"), new PlayerVariablesProvider());
		}

		private final PlayerVariables playerVariables = new PlayerVariables();
		private final LazyOptional<PlayerVariables> instance = LazyOptional.of(() -> playerVariables);

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return cap == PLAYER_VARIABLES_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}

		@Override
		public Tag serializeNBT() {
			return playerVariables.writeNBT();
		}

		@Override
		public void deserializeNBT(Tag nbt) {
			playerVariables.readNBT(nbt);
		}
	}

	public static class PlayerVariables {
		public double R2 = 0.0;
		public double R1 = 255.0;
		public String message3 = "";
		public String message2 = "";
		public String message = "";
		public double G2 = 0.0;
		public double G1 = 255.0;
		public String fade_chat3 = "";
		public String fade_chat2 = "";
		public String fade_chat = "";
		public boolean exibindo_chat3 = false;
		public boolean exibindo_chat2 = false;
		public boolean exibindo_chat = false;
		public boolean chat_global = false;
		public double B2 = 0.0;
		public double B1 = 0.0;

		public void syncPlayerVariables(Entity entity) {
			if (entity instanceof ServerPlayer serverPlayer)
				MortysiumsmpMod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PlayerVariablesSyncMessage(this));
		}

		public Tag writeNBT() {
			CompoundTag nbt = new CompoundTag();
			nbt.putDouble("R2", R2);
			nbt.putDouble("R1", R1);
			nbt.putString("message3", message3);
			nbt.putString("message2", message2);
			nbt.putString("message", message);
			nbt.putDouble("G2", G2);
			nbt.putDouble("G1", G1);
			nbt.putString("fade_chat3", fade_chat3);
			nbt.putString("fade_chat2", fade_chat2);
			nbt.putString("fade_chat", fade_chat);
			nbt.putBoolean("exibindo_chat3", exibindo_chat3);
			nbt.putBoolean("exibindo_chat2", exibindo_chat2);
			nbt.putBoolean("exibindo_chat", exibindo_chat);
			nbt.putBoolean("chat_global", chat_global);
			nbt.putDouble("B2", B2);
			nbt.putDouble("B1", B1);
			return nbt;
		}

		public void readNBT(Tag tag) {
			CompoundTag nbt = (CompoundTag) tag;
			R2 = nbt.getDouble("R2");
			R1 = nbt.getDouble("R1");
			message3 = nbt.getString("message3");
			message2 = nbt.getString("message2");
			message = nbt.getString("message");
			G2 = nbt.getDouble("G2");
			G1 = nbt.getDouble("G1");
			fade_chat3 = nbt.getString("fade_chat3");
			fade_chat2 = nbt.getString("fade_chat2");
			fade_chat = nbt.getString("fade_chat");
			exibindo_chat3 = nbt.getBoolean("exibindo_chat3");
			exibindo_chat2 = nbt.getBoolean("exibindo_chat2");
			exibindo_chat = nbt.getBoolean("exibindo_chat");
			chat_global = nbt.getBoolean("chat_global");
			B2 = nbt.getDouble("B2");
			B1 = nbt.getDouble("B1");
		}
	}

	public static class PlayerVariablesSyncMessage {
		private final PlayerVariables data;

		public PlayerVariablesSyncMessage(FriendlyByteBuf buffer) {
			this.data = new PlayerVariables();
			this.data.readNBT(buffer.readNbt());
		}

		public PlayerVariablesSyncMessage(PlayerVariables data) {
			this.data = data;
		}

		public static void buffer(PlayerVariablesSyncMessage message, FriendlyByteBuf buffer) {
			buffer.writeNbt((CompoundTag) message.data.writeNBT());
		}

		public static void handler(PlayerVariablesSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer()) {
					PlayerVariables variables = ((PlayerVariables) Minecraft.getInstance().player.getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElseGet(PlayerVariables::new));
					variables.R2 = message.data.R2;
					variables.R1 = message.data.R1;
					variables.message3 = message.data.message3;
					variables.message2 = message.data.message2;
					variables.message = message.data.message;
					variables.G2 = message.data.G2;
					variables.G1 = message.data.G1;
					variables.fade_chat3 = message.data.fade_chat3;
					variables.fade_chat2 = message.data.fade_chat2;
					variables.fade_chat = message.data.fade_chat;
					variables.exibindo_chat3 = message.data.exibindo_chat3;
					variables.exibindo_chat2 = message.data.exibindo_chat2;
					variables.exibindo_chat = message.data.exibindo_chat;
					variables.chat_global = message.data.chat_global;
					variables.B2 = message.data.B2;
					variables.B1 = message.data.B1;
				}
			});
			context.setPacketHandled(true);
		}
	}
}