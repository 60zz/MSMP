package net.mcreator.mortysiumsmp.item;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;

public class CoelhaItem extends RecordItem {
	public CoelhaItem() {
		super(0, () -> ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.parse("mortysiumsmp:coelha")), new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 6780);
	}
}