package net.mcreator.mortysiumsmp.command;

import org.checkerframework.checker.units.qual.s;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.common.util.FakePlayerFactory;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.commands.Commands;

import net.mcreator.mortysiumsmp.procedures.RGBColor2Procedure;
import net.mcreator.mortysiumsmp.procedures.RGBColor1Procedure;

import com.mojang.brigadier.arguments.DoubleArgumentType;

@Mod.EventBusSubscriber
public class SetmessagecolorCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("setmessagecolor").requires(s -> s.hasPermission(4)).then(Commands.literal("Contorno")
				.then(Commands.argument("R", DoubleArgumentType.doubleArg(0, 255)).then(Commands.argument("G", DoubleArgumentType.doubleArg(0, 255)).then(Commands.argument("B", DoubleArgumentType.doubleArg(0, 255)).executes(arguments -> {
					Level world = arguments.getSource().getUnsidedLevel();
					double x = arguments.getSource().getPosition().x();
					double y = arguments.getSource().getPosition().y();
					double z = arguments.getSource().getPosition().z();
					Entity entity = arguments.getSource().getEntity();
					if (entity == null && world instanceof ServerLevel _servLevel)
						entity = FakePlayerFactory.getMinecraft(_servLevel);
					Direction direction = Direction.DOWN;
					if (entity != null)
						direction = entity.getDirection();

					RGBColor2Procedure.execute(arguments, entity);
					return 0;
				}))))).then(Commands.literal("Fundo")
						.then(Commands.argument("R", DoubleArgumentType.doubleArg(0, 255)).then(Commands.argument("G", DoubleArgumentType.doubleArg(0, 255)).then(Commands.argument("B", DoubleArgumentType.doubleArg(0, 255)).executes(arguments -> {
							Level world = arguments.getSource().getUnsidedLevel();
							double x = arguments.getSource().getPosition().x();
							double y = arguments.getSource().getPosition().y();
							double z = arguments.getSource().getPosition().z();
							Entity entity = arguments.getSource().getEntity();
							if (entity == null && world instanceof ServerLevel _servLevel)
								entity = FakePlayerFactory.getMinecraft(_servLevel);
							Direction direction = Direction.DOWN;
							if (entity != null)
								direction = entity.getDirection();

							RGBColor1Procedure.execute(arguments, entity);
							return 0;
						}))))));
	}

}