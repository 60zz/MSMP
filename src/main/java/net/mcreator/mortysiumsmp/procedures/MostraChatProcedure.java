package net.mcreator.mortysiumsmp.procedures;

import org.joml.Vector3f;
import org.joml.Matrix4f;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.gui.Font;
import net.minecraft.client.Minecraft;

import net.mcreator.mortysiumsmp.network.MortysiumsmpModVariables;
import net.mcreator.mortysiumsmp.network.ChatBroadcastPacket;

import net.minecraftforge.client.model.data.ModelData;

import java.util.Map;
import java.util.HashMap;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class MostraChatProcedure {

    private static final float CHAT_SCALE    = 0.025f;
    private static final float BASE_Y_OFFSET = 0.75f;
    private static final float LINE_SPACING  = 0.3f;

    private static RenderLevelStageEvent provider = null;
    private static final Map<EntityType<?>, Entity> data = new HashMap<>();

    private static int withAlpha(int rgb, int alpha) {
        int clamped = Math.max(0, Math.min(255, alpha));
        return (clamped << 24) | (rgb & 0x00FFFFFF);
    }

    private static int colorFrom(double r, double g, double b) {
        int ri = Math.max(0, Math.min(255, (int) r));
        int gi = Math.max(0, Math.min(255, (int) g));
        int bi = Math.max(0, Math.min(255, (int) b));
        return (ri << 16) | (gi << 8) | bi;
    }

    private static void applyBillboardPose(PoseStack ps, double wx, double wy, double wz, float scale) {
        Vec3 cam = provider.getCamera().getPosition();
        ps.translate(wx - cam.x(), wy - cam.y(), wz - cam.z());
        ps.mulPose(provider.getCamera().rotation());
        ps.scale(-scale, -scale, scale);
    }

    private static void renderLayer(
            PoseStack ps, Font font, MultiBufferSource buf,
            String text, double wx, double wy, double wz,
            float scale, int fgColor, float dx, float dy, float zOffset,
            Font.DisplayMode mode, int bgColor) {

        ps.pushPose();
        applyBillboardPose(ps, wx, wy, wz, scale);
        float cx = (font.width(text) - 1) * -0.5f + dx;
        float cy = (font.lineHeight - 1) * -0.5f + dy;
        ps.translate(cx, cy, zOffset);
        font.drawInBatch(text, 0f, 0f, fgColor, false,
                ps.last().pose(), buf, mode, bgColor, LightTexture.FULL_BRIGHT);
        ps.popPose();
    }

    private static void renderBubble(String msg, double px, double py, double pz, int textRgb, int bgRgb) {
        if (msg == null || msg.isBlank()) return;

        Minecraft mc = Minecraft.getInstance();
        Font font    = mc.font;
        MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
        PoseStack ps = provider.getPoseStack();

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int outline = withAlpha(textRgb, 255);
        int bg      = withAlpha(bgRgb,   255);
        int fg      = withAlpha(textRgb, 255);

        float[] offsets = {-1, 0,  1, 0,  0, -1,  0, 1};
        for (int i = 0; i < offsets.length; i += 2) {
            renderLayer(ps, font, buf, msg, px, py, pz,
                    CHAT_SCALE, 0, offsets[i], offsets[i + 1], 0.015f,
                    Font.DisplayMode.NORMAL, outline);
        }
        buf.endBatch();

        renderLayer(ps, font, buf, msg, px, py, pz,
                CHAT_SCALE, 0, 0f, 0f, 0.008f, Font.DisplayMode.NORMAL, bg);
        buf.endBatch();

        renderLayer(ps, font, buf, msg, px, py, pz,
                CHAT_SCALE, fg, 0f, 0f, 0f, Font.DisplayMode.NORMAL, 0);
        buf.endBatch();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    @SubscribeEvent
    public static void renderModels(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        provider = event;

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) return;

        for (Map.Entry<java.util.UUID, MortysiumsmpModVariables.PlayerVariables> entry
                : ChatBroadcastPacket.CLIENT_CHAT_DATA.entrySet()) {

            java.util.UUID uuid = entry.getKey();
            MortysiumsmpModVariables.PlayerVariables cap = entry.getValue();

            net.minecraft.world.entity.player.Player player = level.getPlayerByUUID(uuid);
            if (player == null) continue;

            // Se for o jogador local (host singleplayer/LAN), lê direto da capability
            // para garantir que o estado atualizado pelo servidor seja usado
            if (mc.player != null && mc.player.getUUID().equals(uuid)) {
                MortysiumsmpModVariables.PlayerVariables localCap = mc.player
                        .getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                        .orElse(null);
                if (localCap != null) cap = localCap;
            }

            Vec3 pos = player.getPosition(event.getPartialTick());
            execute(pos.x(), pos.y(), pos.z(), player, cap);
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }

    public static void execute(double x, double y, double z, Entity entity) {
        if (entity == null) return;
        MortysiumsmpModVariables.PlayerVariables cap =
                ChatBroadcastPacket.CLIENT_CHAT_DATA.getOrDefault(
                        entity.getUUID(),
                        entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null)
                              .orElseGet(MortysiumsmpModVariables.PlayerVariables::new));
        execute(x, y, z, entity, cap);
    }

    private static void execute(
            double x, double y, double z,
            Entity entity,
            MortysiumsmpModVariables.PlayerVariables cap) {

        if (entity == null || provider == null) return;

        int textRgb = colorFrom(cap.R1, cap.G1, cap.B1);
        int bgRgb   = colorFrom(cap.R2, cap.G2, cap.B2);

        float partialTick = provider.getPartialTick();
        Vec3 entityPos    = entity.getPosition(partialTick);
        double px    = entityPos.x();
        double pz    = entityPos.z();
        double baseY = entityPos.y() + entity.getBbHeight() + BASE_Y_OFFSET;

        int index = 0;

        if (cap.exibindo_chat && cap.message != null && !cap.message.isBlank()) {
            renderBubble(cap.message, px, baseY + index * LINE_SPACING, pz, textRgb, bgRgb);
            index++;
        }

        if (cap.exibindo_chat2 && cap.message2 != null && !cap.message2.isBlank()) {
            renderBubble(cap.message2, px, baseY + index * LINE_SPACING, pz, textRgb, bgRgb);
            index++;
        }

        if (cap.exibindo_chat3 && cap.message3 != null && !cap.message3.isBlank()) {
            renderBubble(cap.message3, px, baseY + index * LINE_SPACING, pz, textRgb, bgRgb);
        }
    }

    public static void renderBlock(BlockState blockState, double x, double y, double z,
                                   float yaw, float pitch, float roll, float scale, boolean glowing) {
        BlockPos blockPos = BlockPos.containing(x, y, z);
        Vec3 pos          = provider.getCamera().getPosition();
        int packedLight   = glowing ? LightTexture.FULL_BRIGHT
                                    : LevelRenderer.getLightColor(Minecraft.getInstance().level, blockPos);
        PoseStack poseStack = provider.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(x - pos.x(), y - pos.y(), z - pos.z());
        poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
        poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        renderBlockModel(blockState, blockPos, poseStack, packedLight);
        renderBlockEntity(blockState, blockPos, poseStack, packedLight);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        poseStack.popPose();
    }

    private static void renderBlockEntity(BlockState blockState, BlockPos blockPos,
                                          PoseStack poseStack, int packedLight) {
        if (blockState.getBlock() instanceof EntityBlock entityBlock) {
            Minecraft mc      = Minecraft.getInstance();
            ClientLevel level = mc.level;
            BlockEntity be    = entityBlock.newBlockEntity(blockPos, blockState);
            if (be != null) {
                BlockEntityRenderer<?> ber = mc.getBlockEntityRenderDispatcher().getRenderer(be);
                if (ber != null) {
                    MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
                    be.setLevel(level);
                    ((BlockEntityRenderer) ber).render(be, 0f, poseStack, buf, packedLight, OverlayTexture.NO_OVERLAY);
                }
            }
        }
    }

    private static void renderBlockModel(BlockState blockState, BlockPos blockPos,
                                         PoseStack poseStack, int packedLight) {
        if (blockState.getRenderShape() == RenderShape.MODEL) {
            Minecraft mc      = Minecraft.getInstance();
            ClientLevel level = mc.level;
            MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
            BlockRenderDispatcher dispatcher   = mc.getBlockRenderer();
            ModelBlockRenderer renderer        = dispatcher.getModelRenderer();
            BakedModel bakedModel              = dispatcher.getBlockModel(blockState);
            ModelData modelData = bakedModel.getModelData(level, blockPos, blockState, ModelData.builder().build());
            PoseStack.Pose pose = poseStack.last();
            int color   = mc.getBlockColors().getColor(blockState, level, blockPos);
            float red   = (color >> 16 & 255) / 255f;
            float green = (color >>  8 & 255) / 255f;
            float blue  = (color       & 255) / 255f;
            for (RenderType rt : bakedModel.getRenderTypes(blockState, RandomSource.create(42L), modelData)) {
                renderer.renderModel(pose, buf.getBuffer(Sheets.translucentCullBlockSheet()),
                        blockState, bakedModel, red, green, blue, packedLight, OverlayTexture.NO_OVERLAY,
                        modelData, rt);
            }
        }
    }

    public static void renderEntity(EntityType<?> type, double x, double y, double z,
                                    float yaw, float pitch, float roll, float scale, boolean glowing) {
        if (type == null) return;
        ClientLevel level = Minecraft.getInstance().level;
        Entity entity = data.computeIfAbsent(type, t -> t.create(level));
        if (entity != null && entity.level() != level) {
            entity = type.create(level);
            data.put(type, entity);
        }
        int packedLight = glowing ? LightTexture.FULL_BRIGHT
                                  : LevelRenderer.getLightColor(level, BlockPos.containing(x, y, z));
        renderEntityInternal(entity, 0f, x, y, z, yaw, pitch, roll, scale, packedLight);
    }

    public static void renderEntity(Entity entity, double x, double y, double z,
                                    float yaw, float pitch, float roll, float scale, boolean glowing) {
        float pt = provider.getPartialTick();
        int packedLight = glowing ? LightTexture.FULL_BRIGHT
                : Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(entity, pt);
        renderEntityInternal(entity, pt, x, y, z, yaw, pitch, roll, scale, packedLight);
    }

    private static void renderEntityInternal(Entity entity, float partialTick,
                                             double x, double y, double z,
                                             float yaw, float pitch, float roll,
                                             float scale, int packedLight) {
        if (entity == null) return;
        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
        EntityRenderer<?> renderer = mc.getEntityRenderDispatcher().getRenderer(entity);
        Vec3 pos = provider.getCamera().getPosition();
        float offset = (entity.getBbHeight() / 2f) * scale;
        PoseStack ps = provider.getPoseStack();
        ps.pushPose();
        ps.translate(x - pos.x(), y + offset - pos.y(), z - pos.z());
        ps.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
        ps.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
        ps.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
        ps.translate(0f, -offset, 0f);
        ps.scale(scale, scale, scale);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        ((EntityRenderer) renderer).render(entity, entity.getViewYRot(partialTick), partialTick, ps, buf, packedLight);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        ps.popPose();
    }

    public static void renderItem(ItemStack itemStack, double x, double y, double z,
                                  float yaw, float pitch, float roll, float scale,
                                  boolean flipping, boolean glowing) {
        Minecraft mc = Minecraft.getInstance();
        MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();
        ItemRenderer renderer = mc.getItemRenderer();
        Vec3 pos = provider.getCamera().getPosition();
        int packedLight = glowing ? LightTexture.FULL_BRIGHT
                : LevelRenderer.getLightColor(mc.level, BlockPos.containing(x, y, z));
        PoseStack ps = provider.getPoseStack();
        ps.pushPose();
        ps.translate(x - pos.x(), y - pos.y(), z - pos.z());
        ps.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
        ps.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
        ps.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
        ps.scale(scale, scale, scale);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        renderer.renderStatic(null, itemStack, ItemDisplayContext.FIXED, flipping,
                ps, buf, mc.level, packedLight, OverlayTexture.NO_OVERLAY, 0);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        ps.popPose();
    }

    public static void renderLine(double x1, double y1, double z1,
                                  double x2, double y2, double z2, int color) {
        MultiBufferSource.BufferSource buf = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 pos = provider.getCamera().getPosition();
        Vector3f normal = new Vec3(x2 - x1, y2 - y1, z2 - z1).normalize().toVector3f();
        Matrix4f m = provider.getPoseStack().last().pose();
        VertexConsumer vc = buf.getBuffer(RenderType.lines());
        vc.vertex(m, (float)(x1 - pos.x()), (float)(y1 - pos.y()), (float)(z1 - pos.z()))
          .color(color).normal(normal.x(), normal.y(), normal.z()).endVertex();
        vc.vertex(m, (float)(x2 - pos.x()), (float)(y2 - pos.y()), (float)(z2 - pos.z()))
          .color(color).normal(normal.x(), normal.y(), normal.z()).endVertex();
    }
}