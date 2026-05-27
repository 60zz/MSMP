package net.mcreator.mortysiumsmp.procedures;

import net.minecraft.world.entity.Entity;
import net.minecraft.commands.CommandSourceStack;

import net.mcreator.mortysiumsmp.network.MortysiumsmpModVariables;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.DoubleArgumentType;

public class RGBColor1Procedure {
	public static void execute(CommandContext<CommandSourceStack> arguments, Entity entity) {
		if (entity == null)
			return;
		{
			double _setval = DoubleArgumentType.getDouble(arguments, "R");
			entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.R2 = _setval;
				capability.syncPlayerVariables(entity);
			});
		}
		{
			double _setval = DoubleArgumentType.getDouble(arguments, "G");
			entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.G2 = _setval;
				capability.syncPlayerVariables(entity);
			});
		}
		{
			double _setval = DoubleArgumentType.getDouble(arguments, "B");
			entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
				capability.B2 = _setval;
				capability.syncPlayerVariables(entity);
			});
		}
	}
}