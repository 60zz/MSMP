package net.mcreator.mortysiumsmp.procedures;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.mcreator.mortysiumsmp.network.MortysiumsmpModVariables;

import java.util.Random;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class SnowParticleHandlerProcedure {

    private static final Random RAND = new Random();
    private static final int SPREAD = 16;
    private static final int PARTICLES_PER_TICK = 30;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        Player player = mc.player;

        if (level == null || player == null) return;
        if (mc.isPaused()) return;

        if (!MortysiumsmpModVariables.MapVariables.get(level).FrioExtremo) return;

        if (level.getRainLevel(1.0F) <= 0.0F) return;

        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        for (int i = 0; i < PARTICLES_PER_TICK; i++) {
            double spawnX = px + (RAND.nextDouble() - 0.5) * SPREAD;
            double spawnY = py + RAND.nextDouble() * 10 + 5;
            double spawnZ = pz + (RAND.nextDouble() - 0.5) * SPREAD;

            level.addParticle(
                ParticleTypes.SNOWFLAKE,
                spawnX, spawnY, spawnZ,
                0.0, -0.3, 0.0
            );
        }
    }
}