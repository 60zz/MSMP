package net.mcreator.mortysiumsmp.item;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;

public class YangItem extends RecordItem {
	public YangItem() {
		super(0, () -> ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.parse("mortysiumsmp:yang")), new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 5020);
	}
}