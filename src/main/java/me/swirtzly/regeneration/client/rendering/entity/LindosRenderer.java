package me.swirtzly.regeneration.client.rendering.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import me.swirtzly.regeneration.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Random;

public class LindosRenderer extends EntityRenderer<Entity> {
	
	private final Vec3d PRIMARY_COLOR = new Vec3d(0.93F, 0.61F, 0.0F);
	private final Vec3d SECONDARY_COLOR = new Vec3d(1F, 0.5F, 0.18F);
	
	public LindosRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void render(Entity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int packedLightIn) {
		for (int j = 0; j < 2; j++) {
			matrixStack.push();
			matrixStack.translate(0,0.20, 0);
			matrixStack.scale(0.9f, 0.9f, 0.9f);
			ItemOverrideRenderer.makeGlowingBall(matrixStack, Minecraft.getInstance(), 0.1f, entityIn.world.getRandom(), PRIMARY_COLOR, SECONDARY_COLOR);
			matrixStack.pop();
		}
	}

	@Nullable
	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
