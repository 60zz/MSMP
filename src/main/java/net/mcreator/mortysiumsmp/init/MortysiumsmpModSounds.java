/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mortysiumsmp.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.mortysiumsmp.MortysiumsmpMod;

public class MortysiumsmpModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MortysiumsmpMod.MODID);
	public static final RegistryObject<SoundEvent> CASTLEVANIA_FORRO = REGISTRY.register("castlevania_forro", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mortysiumsmp", "castlevania_forro")));
	public static final RegistryObject<SoundEvent> FORBIDDEN_FRUIT = REGISTRY.register("forbidden_fruit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mortysiumsmp", "forbidden_fruit")));
	public static final RegistryObject<SoundEvent> COELHA = REGISTRY.register("coelha", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mortysiumsmp", "coelha")));
	public static final RegistryObject<SoundEvent> YANG = REGISTRY.register("yang", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mortysiumsmp", "yang")));
	public static final RegistryObject<SoundEvent> BAILE = REGISTRY.register("baile", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mortysiumsmp", "baile")));
}