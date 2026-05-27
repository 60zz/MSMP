package net.mcreator.mortysiumsmp.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.mcreator.mortysiumsmp.network.MortysiumsmpModVariables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class WeatherSnowMixin {

    @Inject(
        method = "renderSnowAndRain",
        at = @At("HEAD"),
        cancellable = true
    )
    private void forceSnowRender(
            LightTexture lightTexture, float partialTick,
            double camX, double camY, double camZ,
            CallbackInfo ci) {

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;

        if (level == null) return;

        if (!MortysiumsmpModVariables.MapVariables.get(level).FrioExtremo) return;

        float rainLevel = level.getRainLevel(partialTick);
        if (rainLevel <= 0.0F) return;

        ci.cancel();
    }
}