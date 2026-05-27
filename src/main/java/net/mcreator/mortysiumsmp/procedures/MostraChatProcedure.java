package net.mcreator.mortysiumsmp.procedures;

import org.joml.Vector3f;
import org.joml.Matrix4f;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.client.model.data.ModelData;
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

import javax.annotation.Nullable;

import java.util.Map;
import java.util.HashMap;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class MostraChatProcedure {
	private static final int FADE_DURATION_MS = 2000;
	private static final float CHAT_SCALE = 0.025f;
	private static final float BASE_Y_OFFSET = 0.3f;
	private static final float LINE_SPACING = 0.3f;
	private static RenderLevelStageEvent provider = null;
	private static Map<EntityType, Entity> data = new HashMap<>();

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

	private static int computeFadeAlpha(String fadeStartText, long now) {
		if (fadeStartText == null || fadeStartText.isEmpty()) return 255;
		try {
			long fadeStart = Long.parseLong(fadeStartText);
			long elapsed = Math.max(0L, now - fadeStart);
			float progress = Math.min(1f, elapsed / (float) FADE_DURATION_MS);
			return Math.round(255f * (1f - progress));
		} catch (NumberFormatException ignored) {
			return 255;
		}
	}

	private static void renderBackground(String text, double x, double y, double z, float yaw, float pitch, float scale, int bgColor) {
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		Vec3 camPos = provider.getCamera().getPosition();
		PoseStack poseStack = provider.getPoseStack();
		poseStack.pushPose();
		poseStack.translate(x - camPos.x(), y - camPos.y(), z - camPos.z());
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XN.rotationDegrees(pitch));
		poseStack.scale(scale, -scale, 1.0F);
		poseStack.translate((font.width(text) - 1) * -0.5F, (font.lineHeight - 1) * -0.5F, 0.0F);
		Matrix4f matrix = poseStack.last().pose();
		font.drawInBatch(text, 0.0F, 0.0F, 0, false, matrix, bufferSource, Font.DisplayMode.SEE_THROUGH, bgColor, LightTexture.FULL_BRIGHT);
		poseStack.popPose();
	}

	private static void renderTexts(String text, double x, double y, double z, float yaw, float pitch, float scale, int textColor) {
		renderText(text, x, y, z, yaw, pitch, scale, textColor);
	}

	private static void renderText(String text, double x, double y, double z, float yaw, float pitch, float scale, int textColor) {
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		Vec3 camPos = provider.getCamera().getPosition();
		PoseStack poseStack = provider.getPoseStack();
		poseStack.pushPose();
		poseStack.translate(x - camPos.x(), y - camPos.y(), z - camPos.z());
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XN.rotationDegrees(pitch));
		poseStack.scale(scale, -scale, 1.0F);
		poseStack.translate((font.width(text) - 1) * -0.5F, (font.lineHeight - 1) * -0.5F, -0.02F);
		Matrix4f matrix = poseStack.last().pose();
		font.drawInBatch(text, 0.0F, 0.0F, textColor, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		poseStack.popPose();
	}

	private static void renderBorderLayer(String text, double x, double y, double z, float yaw, float pitch, float scale, int borderColor, float offX, float offY) {
		Minecraft minecraft = Minecraft.getInstance();
		Font font = minecraft.font;
		MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		Vec3 camPos = provider.getCamera().getPosition();
		PoseStack poseStack = provider.getPoseStack();
		poseStack.pushPose();
		poseStack.translate(x - camPos.x(), y - camPos.y(), z - camPos.z());
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XN.rotationDegrees(pitch));
		poseStack.scale(scale, -scale, 1.0F);
		poseStack.translate((font.width(text) - 1) * -0.5F + offX, (font.lineHeight - 1) * -0.5F + offY, -0.015F);
		Matrix4f matrix = poseStack.last().pose();
		font.drawInBatch(text, 0.0F, 0.0F, 0, false, matrix, bufferSource, Font.DisplayMode.SEE_THROUGH, borderColor, LightTexture.FULL_BRIGHT);
		poseStack.popPose();
	}

	private static void renderBubble(String msg, double px, double py, double pz, float yaw, float pitch, int textRgb, int bgRgb, int alpha) {
		if (msg == null || msg.isBlank() || alpha <= 0) return;
		int border = withAlpha(textRgb, alpha);
		int background = withAlpha(bgRgb, alpha);
		int text = withAlpha(textRgb, alpha);
		renderBorderLayer(msg, px, py, pz, yaw, pitch, CHAT_SCALE, border, -1, 0);
		renderBorderLayer(msg, px, py, pz, yaw, pitch, CHAT_SCALE, border, 1, 0);
		renderBorderLayer(msg, px, py, pz, yaw, pitch, CHAT_SCALE, border, 0, -1);
		renderBorderLayer(msg, px, py, pz, yaw, pitch, CHAT_SCALE, border, 0, 1);
		renderBackground(msg, px, py, pz, yaw, pitch, CHAT_SCALE, background);
		renderTexts(msg, px, py, pz, yaw, pitch, CHAT_SCALE, text);
	}


	@SubscribeEvent
	public static void renderModels(RenderLevelStageEvent event) {
		provider = event;
		if (provider.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) return;

		for (Map.Entry<java.util.UUID, MortysiumsmpModVariables.PlayerVariables> entry : ChatBroadcastPacket.CLIENT_CHAT_DATA.entrySet()) {
			java.util.UUID uuid = entry.getKey();
			MortysiumsmpModVariables.PlayerVariables cap = entry.getValue();
			net.minecraft.world.entity.player.Player player = level.getPlayerByUUID(uuid);
			if (player == null) continue;
			Vec3 pos = player.getPosition(provider.getPartialTick());
			execute(pos.x(), pos.y(), pos.z(), player, cap);
		}
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
	}

	public static void execute(double x, double y, double z, Entity entity) {
		MortysiumsmpModVariables.PlayerVariables cap = ChatBroadcastPacket.CLIENT_CHAT_DATA.getOrDefault(entity.getUUID(),
			entity.getCapability(MortysiumsmpModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElseGet(MortysiumsmpModVariables.PlayerVariables::new));
		execute(x, y, z, entity, cap);
	}

	private static void execute(double x, double y, double z, Entity entity, MortysiumsmpModVariables.PlayerVariables cap) {
		if (entity == null) return;

		int textRgb = colorFrom(cap.R1, cap.G1, cap.B1);
		int bgRgb = colorFrom(cap.R2, cap.G2, cap.B2);

		float partialTick = provider.getPartialTick();
		Vec3 entityPos = entity.getPosition(partialTick);
		double px = entityPos.x();
		double pz = entityPos.z();

		Vec3 camPos = provider.getCamera().getPosition();
		double dx = camPos.x() - px;
		double dz = camPos.z() - pz;
		float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
		float pitch = provider.getCamera().getXRot();
		long now = System.currentTimeMillis();

		int alpha1 = computeFadeAlpha(cap.fade_chat, now);
		int alpha2 = computeFadeAlpha(cap.fade_chat2, now);
		int alpha3 = computeFadeAlpha(cap.fade_chat3, now);

		int activeCount = (cap.exibindo_chat ? 1 : 0) + (cap.exibindo_chat2 ? 1 : 0) + (cap.exibindo_chat3 ? 1 : 0);
		int index = 0;

		if (cap.exibindo_chat) {
			double py = entityPos.y() + entity.getBbHeight() + BASE_Y_OFFSET + ((activeCount - 1 - index) * LINE_SPACING);
			renderBubble(cap.message, px, py, pz, yaw, pitch, textRgb, bgRgb, alpha1);
			index++;
		}

		if (cap.exibindo_chat2) {
			double py = entityPos.y() + entity.getBbHeight() + BASE_Y_OFFSET + ((activeCount - 1 - index) * LINE_SPACING);
			renderBubble(cap.message2, px, py, pz, yaw, pitch, textRgb, bgRgb, alpha2);
			index++;
		}

		if (cap.exibindo_chat3) {
			double py = entityPos.y() + entity.getBbHeight() + BASE_Y_OFFSET + ((activeCount - 1 - index) * LINE_SPACING);
			renderBubble(cap.message3, px, py, pz, yaw, pitch, textRgb, bgRgb, alpha3);
		}

		Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
	}

	// --- Métodos de render de bloco / entidade / item / linha sem alteração ---

	public static void renderBlock(BlockState blockState, double x, double y, double z, float yaw, float pitch, float roll, float scale, boolean glowing) {
		BlockPos blockPos = BlockPos.containing(x, y, z);
		Vec3 pos = provider.getCamera().getPosition();
		int packedLight = glowing ? LightTexture.FULL_BRIGHT : LevelRenderer.getLightColor(Minecraft.getInstance().level, blockPos);
		PoseStack poseStack = provider.getPoseStack();
		poseStack.pushPose();
		poseStack.translate(x - pos.x(), y - pos.y(), z - pos.z());
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
		poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
		poseStack.scale(scale, scale, scale);
		poseStack.translate(-0.5F, -0.5F, -0.5F);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		renderBlockModel(blockState, blockPos, poseStack, packedLight);
		renderBlockEntity(blockState, blockPos, poseStack, packedLight);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}

	private static void renderBlockEntity(BlockState blockState, BlockPos blockPos, PoseStack poseStack, int packedLight) {
		if (blockState.getBlock() instanceof EntityBlock entityBlock) {
			Minecraft minecraft = Minecraft.getInstance();
			ClientLevel level = minecraft.level;
			BlockEntity blockEntity = entityBlock.newBlockEntity(blockPos, blockState);
			if (blockEntity != null) {
				BlockEntityRenderer blockEntityRenderer = minecraft.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
				if (blockEntityRenderer != null) {
					MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
					blockEntity.setLevel(level);
					blockEntityRenderer.render(blockEntity, 0.0F, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
				}
			}
		}
	}

	private static void renderBlockModel(BlockState blockState, BlockPos blockPos, PoseStack poseStack, int packedLight) {
		if (blockState.getRenderShape() == RenderShape.MODEL) {
			Minecraft minecraft = Minecraft.getInstance();
			ClientLevel level = minecraft.level;
			MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
			BlockRenderDispatcher dispatcher = minecraft.getBlockRenderer();
			ModelBlockRenderer renderer = dispatcher.getModelRenderer();
			BakedModel bakedModel = dispatcher.getBlockModel(blockState);
			ModelData modelData = bakedModel.getModelData(level, blockPos, blockState, ModelData.builder().build());
			PoseStack.Pose pose = poseStack.last();
			int color = minecraft.getBlockColors().getColor(blockState, level, blockPos);
			float red   = (color >> 16 & 255) / 255.0F;
			float green = (color >>  8 & 255) / 255.0F;
			float blue  = (color       & 255) / 255.0F;
			for (RenderType renderType : bakedModel.getRenderTypes(blockState, RandomSource.create(42L), modelData)) {
				renderer.renderModel(pose, bufferSource.getBuffer(Sheets.translucentCullBlockSheet()),
						blockState, bakedModel, red, green, blue, packedLight, OverlayTexture.NO_OVERLAY, modelData, renderType);
			}
		}
	}

	public static void renderEntity(EntityType type, double x, double y, double z, float yaw, float pitch, float roll, float scale, boolean glowing) {
		if (type == null) return;
		Entity entity;
		ClientLevel level = Minecraft.getInstance().level;
		if (data.containsKey(type)) {
			entity = data.get(type);
			if (entity.level() != level) { entity = type.create(level); data.put(type, entity); }
		} else {
			entity = type.create(level);
			data.put(type, entity);
		}
		renderEntity(entity, 0.0F, x, y, z, yaw, pitch, roll, scale,
				glowing ? LightTexture.FULL_BRIGHT : LevelRenderer.getLightColor(level, BlockPos.containing(x, y, z)));
	}

	public static void renderEntity(Entity entity, double x, double y, double z, float yaw, float pitch, float roll, float scale, boolean glowing) {
		float partialTick = provider.getPartialTick();
		int packedLight = glowing ? LightTexture.FULL_BRIGHT
				: Minecraft.getInstance().getEntityRenderDispatcher().getPackedLightCoords(entity, partialTick);
		renderEntity(entity, partialTick, x, y, z, yaw, pitch, roll, scale, packedLight);
	}

	private static void renderEntity(Entity entity, float partialTick, double x, double y, double z, float yaw, float pitch, float roll, float scale, int packedLight) {
		if (entity == null) return;
		Minecraft minecraft = Minecraft.getInstance();
		MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		EntityRenderer renderer = minecraft.getEntityRenderDispatcher().getRenderer(entity);
		Vec3 pos = provider.getCamera().getPosition();
		float offset = (entity.getBbHeight() / 2.0F) * scale;
		PoseStack poseStack = provider.getPoseStack();
		poseStack.pushPose();
		poseStack.translate(x - pos.x(), y + offset - pos.y(), z - pos.z());
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
		poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
		poseStack.translate(0.0F, -offset, 0.0F);
		poseStack.scale(scale, scale, scale);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		renderer.render(entity, entity.getViewYRot(partialTick), partialTick, poseStack, bufferSource, packedLight);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}

	public static void renderItem(ItemStack itemStack, double x, double y, double z, float yaw, float pitch, float roll, float scale, boolean flipping, boolean glowing) {
		Minecraft minecraft = Minecraft.getInstance();
		MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		ItemRenderer renderer = minecraft.getItemRenderer();
		Vec3 pos = provider.getCamera().getPosition();
		int packedLight = glowing ? LightTexture.FULL_BRIGHT
				: LevelRenderer.getLightColor(minecraft.level, BlockPos.containing(x, y, z));
		PoseStack poseStack = provider.getPoseStack();
		poseStack.pushPose();
		poseStack.translate(x - pos.x(), y - pos.y(), z - pos.z());
		poseStack.mulPose(com.mojang.math.Axis.YN.rotationDegrees(yaw));
		poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
		poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(roll));
		poseStack.scale(scale, scale, scale);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		renderer.renderStatic(null, itemStack, ItemDisplayContext.FIXED, flipping, poseStack,
				bufferSource, minecraft.level, packedLight, OverlayTexture.NO_OVERLAY, 0);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		poseStack.popPose();
	}

	public static void renderLine(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
		MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		Vec3 pos = provider.getCamera().getPosition();
		Vector3f normal = new Vec3(x2 - x1, y2 - y1, z2 - z1).normalize().toVector3f();
		Matrix4f matrix4f = provider.getPoseStack().last().pose();
		VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());
		vertexConsumer.vertex(matrix4f, (float)(x1-pos.x()), (float)(y1-pos.y()), (float)(z1-pos.z())).color(color).normal(normal.x(), normal.y(), normal.z()).endVertex();
		vertexConsumer.vertex(matrix4f, (float)(x2-pos.x()), (float)(y2-pos.y()), (float)(z2-pos.z())).color(color).normal(normal.x(), normal.y(), normal.z()).endVertex();
	}
}