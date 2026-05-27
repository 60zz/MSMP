/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mortysiumsmp.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.mcreator.mortysiumsmp.block.AguaContaminadaBlock;
import net.mcreator.mortysiumsmp.MortysiumsmpMod;

public class MortysiumsmpModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MortysiumsmpMod.MODID);
	public static final RegistryObject<Block> AGUA_CONTAMINADA = REGISTRY.register("agua_contaminada", AguaContaminadaBlock::new);
	// Start of user code block custom blocks
	// End of user code block custom blocks
}