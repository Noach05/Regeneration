package me.swirtzly.regeneration.client.rendering.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.util.common.PlayerUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;

public class HandsLayer extends LayerRenderer {

    private final IEntityRenderer livingEntityRenderer;

    public HandsLayer(IEntityRenderer livingEntityRendererIn) {
		super(livingEntityRendererIn);
		this.livingEntityRenderer = livingEntityRendererIn;
	}

	@Override
	public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLightIn, Entity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		matrixStack.push();
		LivingEntity entitylivingbaseIn = (LivingEntity) entity;
		RegenCap.get(entitylivingbaseIn).ifPresent((data) -> {
			if (this.livingEntityRenderer.getEntityModel().isChild) {
				matrixStack.translate(0.0F, 0.75F, 0.0F);
				matrixStack.scale(0.5F, 0.5F, 0.5F);
			}
			if (data.areHandsGlowing()) {
				renderHand(entitylivingbaseIn, HandSide.LEFT, EnumHandRenderType.GRACE, matrixStack);
				renderHand(entitylivingbaseIn, HandSide.RIGHT, EnumHandRenderType.GRACE, matrixStack);
			}

			if (data.getState() == PlayerUtil.RegenState.REGENERATING || data.isSyncingToJar()) {
				renderHand(entitylivingbaseIn, HandSide.LEFT, EnumHandRenderType.REGEN, matrixStack);
				renderHand(entitylivingbaseIn, HandSide.RIGHT, EnumHandRenderType.REGEN, matrixStack);
			}
		});

		matrixStack.pop();
	}

	private void renderHand(LivingEntity player, HandSide handSide, EnumHandRenderType type, MatrixStack stack) {
		stack.push();

        RegenCap.get(player).ifPresent((data) -> {
			if (player.isShiftKeyDown()) {
				stack.translate(0.0F, 0.2F, 0.0F);
			}
			// Forge: moved this call down, fixes incorrect offset while sneaking.
			this.translateToHand(handSide);
			boolean flag = handSide == HandSide.LEFT;
			stack.translate((float) (flag ? -1 : 1) / 25.0F, 0.125F, -0.625F);
			stack.translate(0, -0.050, 0.6);

            if (type == EnumHandRenderType.GRACE) {
				RegenerationLayer.renderGlowingHands(player, data, 1.5F, handSide);
			}

            if (type == EnumHandRenderType.REGEN) {
				data.getType().create().getRenderer().renderHand(player, handSide, (LivingRenderer) livingEntityRenderer);
			}

        });

        stack.pop();
	}
	
	protected void translateToHand(HandSide handSide) {
		((BipedModel) this.livingEntityRenderer.getEntityModel()).postRenderArm(0.0625F, handSide);
	}

    public boolean shouldCombineTextures() {
		return false;
	}
	
	public enum EnumHandRenderType {
		REGEN, GRACE
	}
}
