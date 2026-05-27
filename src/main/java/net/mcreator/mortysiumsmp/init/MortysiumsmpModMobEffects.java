/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mortysiumsmp.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

import net.mcreator.mortysiumsmp.potion.IrradiatedMobEffect;
import net.mcreator.mortysiumsmp.MortysiumsmpMod;

public class MortysiumsmpModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MortysiumsmpMod.MODID);
	public static final RegistryObject<MobEffect> IRRADIATED = REGISTRY.register("irradiated", () -> new IrradiatedMobEffect());
}