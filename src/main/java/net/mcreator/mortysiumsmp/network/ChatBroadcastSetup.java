package net.mcreator.mortysiumsmp.network;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import net.mcreator.mortysiumsmp.MortysiumsmpMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChatBroadcastSetup {
    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MortysiumsmpMod.addNetworkMessage(
                ChatBroadcastPacket.class,
                ChatBroadcastPacket::buffer,
                ChatBroadcastPacket::new,
                ChatBroadcastPacket::handler
            );
        });
    }
}