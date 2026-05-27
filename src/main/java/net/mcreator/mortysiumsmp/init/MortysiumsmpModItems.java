/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mortysiumsmp.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

import net.mcreator.mortysiumsmp.item.YangItem;
import net.mcreator.mortysiumsmp.item.ForbiddenFruitItem;
import net.mcreator.mortysiumsmp.item.CoelhaItem;
import net.mcreator.mortysiumsmp.item.CastlevaniaItem;
import net.mcreator.mortysiumsmp.item.BaileItem;
import net.mcreator.mortysiumsmp.item.AguaContaminadaItem;
import net.mcreator.mortysiumsmp.MortysiumsmpMod;

public class MortysiumsmpModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MortysiumsmpMod.MODID);
	public static final RegistryObject<Item> AGUA_CONTAMINADA_BUCKET = REGISTRY.register("agua_contaminada_bucket", () -> new AguaContaminadaItem());
	public static final RegistryObject<Item> CASTLEVANIA = REGISTRY.register("castlevania", () -> new CastlevaniaItem());
	public static final RegistryObject<Item> FORBIDDEN_FRUIT = REGISTRY.register("forbidden_fruit", () -> new ForbiddenFruitItem());
	public static final RegistryObject<Item> BAILE = REGISTRY.register("baile", () -> new BaileItem());
	public static final RegistryObject<Item> COELHA = REGISTRY.register("coelha", () -> new CoelhaItem());
	public static final RegistryObject<Item> YANG = REGISTRY.register("yang", () -> new YangItem());
	// Start of user code block custom items
	// End of user code block custom items
}