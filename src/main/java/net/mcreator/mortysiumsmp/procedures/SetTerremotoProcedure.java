package net.mcreator.mortysiumsmp.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

import net.mcreator.mortysiumsmp.network.MortysiumsmpModVariables;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.arguments.BoolArgumentType;

public class SetTerremotoProcedure {
	public static void execute(LevelAccessor world, CommandContext<CommandSourceStack> arguments) {
		if (BoolArgumentType.getBool(arguments, "value") == true) {
			if (MortysiumsmpModVariables.MapVariables.get(world).Tornado == false && MortysiumsmpModVariables.MapVariables.get(world).FrioExtremo == false && MortysiumsmpModVariables.MapVariables.get(world).CalorExtremo == false
					&& MortysiumsmpModVariables.MapVariables.get(world).Terremotos == false && MortysiumsmpModVariables.MapVariables.get(world).ChuvaAcida == false) {
				MortysiumsmpModVariables.MapVariables.get(world).Terremotos = true;
				MortysiumsmpModVariables.MapVariables.get(world).syncData(world);
				final String _success1 = "Iniciado evento clim\u00E1tico: Terremoto";
				arguments.getSource().sendSuccess(() -> Component.literal(_success1), true);
			} else {
				arguments.getSource().sendFailure(Component.literal("N\u00E3o foi poss\u00EDvel ativar o evento \"Terremoto\". Possivelmente h\u00E1 outro evento ativo!"));
			}
		} else {
			MortysiumsmpModVariables.MapVariables.get(world).Terremotos = false;
			MortysiumsmpModVariables.MapVariables.get(world).syncData(world);
			final String _success3 = "Event clim\u00E1tico Terremoto desabilitado";
			arguments.getSource().sendSuccess(() -> Component.literal(_success3), true);
		}
	}
}