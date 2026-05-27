package net.mcreator.mortysiumsmp.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

import net.mcreator.mortysiumsmp.network.MortysiumsmpModVariables;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class CongelaAguaAndandoProcedure {

    private static final int RAIO = 30;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            execute(event, event.player.level(), event.player.getX(), event.player.getY(), event.player.getZ());
        }
    }

    public static void execute(LevelAccessor world, double x, double y, double z) {
        execute(null, world, x, y, z);
    }

    private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof Level level)) return;

        if (!MortysiumsmpModVariables.MapVariables.get(world).FrioExtremo) return;

        // Só congela se estiver chovendo
        if (!level.isRaining()) return;

        BlockPos center = BlockPos.containing(x, y, z);

        for (int dx = -RAIO; dx <= RAIO; dx++) {
            for (int dy = -RAIO; dy <= RAIO; dy++) {
                for (int dz = -RAIO; dz <= RAIO; dz++) {
                    if (dx * dx + dy * dy + dz * dz <= RAIO * RAIO) {
                        BlockPos pos = center.offset(dx, dy, dz);

                        if (world.getBlockState(pos).is(Blocks.WATER)) {
                            world.setBlock(pos, Blocks.FROSTED_ICE.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }
}