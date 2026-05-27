package net.mcreator.mortysiumsmp.procedures;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.mcreator.mortysiumsmp.network.MortysiumsmpModVariables;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class BlocoColocadoCalorProcedure {
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getHand() != InteractionHand.MAIN_HAND)
			return;
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (MortysiumsmpModVariables.MapVariables.get(world).CalorExtremo == true) {
			if (world instanceof Level _lvl0 && _lvl0.isDay()) {
				if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(ResourceLocation.parse("minecraft:evapora_calor")))) {
					if (entity instanceof LivingEntity _entity)
						_entity.swing(InteractionHand.MAIN_HAND, true);
					if (entity instanceof LivingEntity _entity) {
						ItemStack _setstack = new ItemStack(Items.BUCKET).copy();
						_setstack.setCount(1);
						_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
						if (_entity instanceof Player _player)
							_player.getInventory().setChanged();
					}
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.SMOKE, (x + 0.5), (y + 1), (z + 0.5), 5, 0.01, 0.2, 0.01, 0);
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.LARGE_SMOKE, (x + 0.5), (y + 1), (z + 0.5), 5, 0.01, 0.2, 0.01, 0);
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x + 0.5, y + 1, z + 0.5), ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.parse("block.fire.extinguish")), SoundSource.BLOCKS, (float) 0.1, 1);
						} else {
							_level.playLocalSound((x + 0.5), (y + 1), (z + 0.5), ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation.parse("block.fire.extinguish")), SoundSource.BLOCKS, (float) 0.1, 1, false);
						}
					}
					if (world instanceof Level _level) {
						if (_level.isClientSide()) {
							_level.playLocalSound((x + 0.5), (y + 1), (z + 0.5), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.BLOCKS, (float) 0.1, 1, false);
						}
					}
					if (event != null && event.isCancelable()) {
						event.setCanceled(true);
					}
				} else if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(ResourceLocation.parse("minecraft:evapora_calor")))) {
					if (entity instanceof LivingEntity _entity) {
						ItemStack _setstack = new ItemStack(Items.BUCKET).copy();
						_setstack.setCount(1);
						_entity.setItemInHand(InteractionHand.OFF_HAND, _setstack);
						if (_entity instanceof Player _player)
							_player.getInventory().setChanged();
					}
					if (entity instanceof LivingEntity _entity)
						_entity.swing(InteractionHand.OFF_HAND, true);
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.SMOKE, (x + 0.5), (y + 1), (z + 0.5), 5, 0.01, 0.2, 0.01, 0);
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.LARGE_SMOKE, (x + 0.5), (y + 1), (z + 0.5), 5, 0.01, 0.2, 0.01, 0);
					if (world instanceof Level _level) {
						if (_level.isClientSide()) {
							_level.playLocalSound((x + 0.5), (y + 1), (z + 0.5), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.fire.extinguish")), SoundSource.BLOCKS, (float) 0.1, 1, false);
						}
					}
					if (event != null && event.isCancelable()) {
						event.setCanceled(true);
					}
				}
			}
		}
	}
}