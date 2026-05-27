/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mortysiumsmp.init;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.core.registries.Registries;

import net.mcreator.mortysiumsmp.MortysiumsmpMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MortysiumsmpModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MortysiumsmpMod.MODID);

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			tabData.accept(MortysiumsmpModItems.AGUA_CONTAMINADA_BUCKET.get());
			tabData.accept(MortysiumsmpModItems.CASTLEVANIA.get());
			tabData.accept(MortysiumsmpModItems.FORBIDDEN_FRUIT.get());
			tabData.accept(MortysiumsmpModItems.BAILE.get());
			tabData.accept(MortysiumsmpModItems.COELHA.get());
			tabData.accept(MortysiumsmpModItems.YANG.get());
		}
	}
}