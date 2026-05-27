/*
 * MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mortysiumsmp.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

import net.mcreator.mortysiumsmp.fluid.AguaContaminadaFluid;
import net.mcreator.mortysiumsmp.MortysiumsmpMod;

public class MortysiumsmpModFluids {
	public static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(ForgeRegistries.FLUIDS, MortysiumsmpMod.MODID);
	public static final RegistryObject<FlowingFluid> AGUA_CONTAMINADA = REGISTRY.register("agua_contaminada", () -> new AguaContaminadaFluid.Source());
	public static final RegistryObject<FlowingFluid> FLOWING_AGUA_CONTAMINADA = REGISTRY.register("flowing_agua_contaminada", () -> new AguaContaminadaFluid.Flowing());

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class FluidsClientSideHandler {
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			ItemBlockRenderTypes.setRenderLayer(AGUA_CONTAMINADA.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(FLOWING_AGUA_CONTAMINADA.get(), RenderType.translucent());
		}
	}
}