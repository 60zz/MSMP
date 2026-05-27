package net.mcreator.mortysiumsmp.item;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BucketItem;

import net.mcreator.mortysiumsmp.init.MortysiumsmpModFluids;

public class AguaContaminadaItem extends BucketItem {
	public AguaContaminadaItem() {
		super(MortysiumsmpModFluids.AGUA_CONTAMINADA, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)

		);
	}
}