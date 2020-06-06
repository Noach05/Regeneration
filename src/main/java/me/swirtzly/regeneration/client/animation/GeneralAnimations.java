package me.swirtzly.regeneration.client.animation;

import com.mojang.blaze3d.platform.GlStateManager;
import me.swirtzly.animateme.AnimationManager;
import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.common.item.FobWatchItem;
import me.swirtzly.regeneration.util.common.PlayerUtil;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;

public class GeneralAnimations implements AnimationManager.IAnimate {

    public static void copyAnglesToWear(BipedModel modelBiped) {
        // if (modelBiped instanceof PlayerModel) {
        // PlayerModel playerModel = (PlayerModel) modelBiped;
        // ClientUtil.copyAnglesToWear(playerModel);
        // }
    }

    public static void makeZombieArms(BipedModel modelBiped) {
        modelBiped.bipedRightArm.rotateAngleY = -0.1F + modelBiped.bipedHead.rotateAngleY - 0.4F;
        modelBiped.bipedLeftArm.rotateAngleY = 0.1F + modelBiped.bipedHead.rotateAngleY;
        modelBiped.bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + modelBiped.bipedHead.rotateAngleX;
        modelBiped.bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + modelBiped.bipedHead.rotateAngleX;
    }

    @Override
    public void preRenderCallBack(LivingRenderer renderer, LivingEntity entity) {
            RegenCap.get(entity).ifPresent((data) -> {
                if (!(renderer.getEntityModel() instanceof BipedModel)) return;
                BipedModel modelPlayer = (BipedModel) renderer.getEntityModel();
                if (data.hasDroppedHand() && data.getState() == PlayerUtil.RegenState.POST) {
                    modelPlayer.bipedRightArm.showModel = data.getCutoffHand() != HandSide.RIGHT;
                    modelPlayer.bipedLeftArm.showModel = data.getCutoffHand() != HandSide.LEFT;
                } else {
                    modelPlayer.bipedLeftArm.showModel = true;
                    modelPlayer.bipedRightArm.showModel = true;
                }


                if (data.getState() == PlayerUtil.RegenState.POST && PlayerUtil.isAboveZeroGrid(entity)) {
                    GlStateManager.rotatef(15, 1, 0, 0);
                }

            });


    }

    @Override
    public void preAnimation(BipedModel model, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void postAnimation(BipedModel modelBiped, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = entity.getHeldItemMainhand();
        ItemStack offStack = entity.getHeldItemOffhand();

            // ==============FOB WATCH & JAR START==============
            boolean isOpen;

            // MAINHAND
            if (stack.getItem() instanceof FobWatchItem) {
                isOpen = FobWatchItem.getOpen(stack) == 1;
                if (isOpen) {
                    makeZombieArms(modelBiped);
                    copyAnglesToWear(modelBiped);
                }
            }

            // OFFHAND
            if (offStack.getItem() instanceof FobWatchItem) {
                isOpen = FobWatchItem.getOpen(stack) == 1;
                if (isOpen) {
                    makeZombieArms(modelBiped);
                    copyAnglesToWear(modelBiped);
                }
            }
            // ==============FOB WATCH END==============

        RegenCap.get(entity).ifPresent((data) -> {
                // JAR SYNCING
                if (data.isSyncingToJar()) {
                    makeZombieArms(modelBiped);
                    modelBiped.bipedHead.rotateAngleX = (float) Math.toRadians(45);
                    copyAnglesToWear(modelBiped);
                }

            if (data.getState() == PlayerUtil.RegenState.POST && PlayerUtil.isAboveZeroGrid(entity)) {
                modelBiped.bipedHead.rotateAngleX = (float) Math.toRadians(0);
                modelBiped.bipedHead.rotateAngleY = (float) Math.toRadians(0);
                modelBiped.bipedHead.rotateAngleZ = (float) Math.toRadians(0);
            }
            });
    }


    @Override
    public boolean useVanilla() {
        return false;
    }
}
