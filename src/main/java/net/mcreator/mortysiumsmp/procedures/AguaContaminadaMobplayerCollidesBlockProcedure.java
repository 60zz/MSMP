package net.mcreator.mortysiumsmp.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.mortysiumsmp.init.MortysiumsmpModMobEffects;

public class AguaContaminadaMobplayerCollidesBlockProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(MortysiumsmpModMobEffects.IRRADIATED.get(), 1200, 0));
	}
}