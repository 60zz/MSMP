/*
 * MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mortysiumsmp.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fluids.FluidType;

import net.mcreator.mortysiumsmp.fluid.types.AguaContaminadaFluidType;
import net.mcreator.mortysiumsmp.MortysiumsmpMod;

public class MortysiumsmpModFluidTypes {
	public static final DeferredRegister<FluidType> REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MortysiumsmpMod.MODID);
	public static final RegistryObject<FluidType> AGUA_CONTAMINADA_TYPE = REGISTRY.register("agua_contaminada", () -> new AguaContaminadaFluidType());
}