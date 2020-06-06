package me.swirtzly.regeneration.client.gui;

import me.swirtzly.regeneration.RegenConfig;
import me.swirtzly.regeneration.Regeneration;
import me.swirtzly.regeneration.client.gui.parts.ColorSliderWidget;
import me.swirtzly.regeneration.client.gui.parts.ContainerBlank;
import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.common.types.RegenType;
import me.swirtzly.regeneration.network.NetworkDispatcher;
import me.swirtzly.regeneration.network.messages.UpdateColorMessage;
import me.swirtzly.regeneration.util.client.ClientUtil;
import me.swirtzly.regeneration.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;

import java.awt.*;

public class ColorScreen extends ContainerScreen implements Slider.ISlider {

    private static final ResourceLocation background = new ResourceLocation(Regeneration.MODID, "textures/gui/customizer_background.png");

    private static ColorSliderWidget slidePrimaryRed;
    private static ColorSliderWidget slidePrimaryGreen;
    private static ColorSliderWidget slidePrimaryBlue;
    private static ColorSliderWidget slideSecondaryRed;
    private static ColorSliderWidget slideSecondaryGreen;
    private static ColorSliderWidget slideSecondaryBlue;

    private Vec3d initialPrimary, initialSecondary;

    public ColorScreen() {
        super(new ContainerBlank(), null, new TranslationTextComponent("Regeneration"));
        xSize = 176;
        ySize = 186;
    }

    @Override
    public void init() {
        super.init();

        int cx = (width - xSize) / 2;
        int cy = (height - ySize) / 2;

        RegenCap.get(minecraft.player).ifPresent((data) -> {
            initialPrimary = data.getPrimaryColor();
            initialSecondary = data.getSecondaryColor();
        });

        float primaryRed = (float) initialPrimary.x, primaryGreen = (float) initialPrimary.y, primaryBlue = (float) initialPrimary.z;
        float secondaryRed = (float) initialSecondary.x, secondaryGreen = (float) initialSecondary.y, secondaryBlue = (float) initialSecondary.z;

        final int btnW = 60, btnH = 18;
        final int sliderW = 70, sliderH = 20;

        // Reset Style Button
        this.addButton(new Button(cx + 25, cy + 125, btnW, btnH, new TranslationTextComponent("regeneration.gui.undo").getFormattedText(), button -> {
            slidePrimaryRed.setValue(initialPrimary.x);
            slidePrimaryGreen.setValue(initialPrimary.y);
            slidePrimaryBlue.setValue(initialPrimary.z);

            slideSecondaryRed.setValue(initialSecondary.x);
            slideSecondaryGreen.setValue(initialSecondary.y);
            slideSecondaryBlue.setValue(initialSecondary.z);
        }));

        // Reset Skin Button
        this.addButton(new Button(cx + 25, cy + 145, btnW, btnH, new TranslationTextComponent("regeneration.gui.reset_skin").getFormattedText(), p_onPress_1_ -> ClientUtil.sendSkinResetPacket()));

        // Customize Button
        this.addButton(new Button(cx + 90, cy + 145, btnW, btnH, new TranslationTextComponent("regeneration.gui.close").getFormattedText(), button -> Minecraft.getInstance().displayGuiScreen(null)));

        // Default Button
        this.addButton(new Button(cx + 90, cy + 125, btnW, btnH, new TranslationTextComponent("regeneration.gui.default").getFormattedText(), button -> {
            RegenCap.get(Minecraft.getInstance().player).ifPresent((data) -> {
                RegenType regenType = data.getType().create();
                slidePrimaryRed.setValue(regenType.getDefaultPrimaryColor().x);
                slidePrimaryGreen.setValue(regenType.getDefaultPrimaryColor().y);
                slidePrimaryBlue.setValue(regenType.getDefaultPrimaryColor().z);

                slideSecondaryRed.setValue(regenType.getDefaultSecondaryColor().x);
                slideSecondaryGreen.setValue(regenType.getDefaultSecondaryColor().y);
                slideSecondaryBlue.setValue(regenType.getDefaultSecondaryColor().z);
            });

            onChangeSliderValue(null);
        }));

        slidePrimaryRed = new ColorSliderWidget(cx + 10, cy + 65, sliderW, sliderH, new TranslationTextComponent("regeneration.gui.red").getFormattedText(), "", 0, 1, primaryRed, true, true, button -> {

        }, this);
        slidePrimaryGreen = new ColorSliderWidget(cx + 10, cy + 84, sliderW, sliderH, new TranslationTextComponent("regeneration.gui.green").getFormattedText(), "", 0, 1, primaryGreen, true, true, p_onPress_1_ -> {

        }, this);
        slidePrimaryBlue = new ColorSliderWidget(cx + 10, cy + 103, sliderW, sliderH, new TranslationTextComponent("regeneration.gui.blue").getFormattedText(), "", 0, 1, primaryBlue, true, true, p_onPress_1_ -> {

        }, this);
        slideSecondaryRed = new ColorSliderWidget(cx + 96, cy + 65, sliderW, sliderH, new TranslationTextComponent("regeneration.gui.red").getFormattedText(), "", 0, 1, secondaryRed, true, true, p_onPress_1_ -> {

        }, this);
        slideSecondaryGreen = new ColorSliderWidget(cx + 96, cy + 84, sliderW, sliderH, new TranslationTextComponent("regeneration.gui.green").getFormattedText(), "", 0, 1, secondaryGreen, true, true, p_onPress_1_ -> {

        }, this);
        slideSecondaryBlue = new ColorSliderWidget(cx + 96, cy + 103, sliderW, sliderH, new TranslationTextComponent("regeneration.gui.blue").getFormattedText(), "", 0, 1, secondaryBlue, true, true, p_onPress_1_ -> {

        }, this);

        addButton(slidePrimaryRed);
        addButton(slidePrimaryGreen);
        addButton(slidePrimaryBlue);

        addButton(slideSecondaryRed);
        addButton(slideSecondaryGreen);
        addButton(slideSecondaryBlue);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.renderBackground();
        Minecraft.getInstance().getTextureManager().bindTexture(background);
        blit(guiLeft, guiTop, 0, 0, xSize, ySize);

        int cx = (width - xSize) / 2;
        int cy = (height - ySize) / 2;

        RenderUtil.drawRect(cx + 10, cy + 44, cx + 81, cy + 61, 0.1F, 0.1F, 0.1F, 1);
        RenderUtil.drawRect(cx + 11, cy + 45, cx + 80, cy + 60, (float) slidePrimaryRed.getValue(), (float) slidePrimaryGreen.getValue(), (float) slidePrimaryBlue.getValue(), 1);

        RenderUtil.drawRect(cx + 95, cy + 44, cx + 166, cy + 61, 0.1F, 0.1F, 0.1F, 1);
        RenderUtil.drawRect(cx + 96, cy + 45, cx + 165, cy + 60, (float) slideSecondaryRed.getValue(), (float) slideSecondaryGreen.getValue(), (float) slideSecondaryBlue.getValue(), 1);

        Vec3d primaryColor = new Vec3d((float) slidePrimaryRed.getValue(), (float) slidePrimaryGreen.getValue(), (float) slidePrimaryBlue.getValue()), secondaryColor = new Vec3d((float) slideSecondaryRed.getValue(), (float) slideSecondaryGreen.getValue(), (float) slideSecondaryBlue.getValue());

        RegenCap.get(minecraft.player).ifPresent((cap) -> {
            String str = new TranslationTextComponent("regeneration.gui.primary").getFormattedText();
            int length = minecraft.fontRenderer.getStringWidth(str);
            font.drawString(str, cx + 45 - length / 2, cy + 49, RenderUtil.calculateColorBrightness(primaryColor) > 0.179 ? 0x0 : 0xFFFFFF);

            str = new TranslationTextComponent("regeneration.gui.secondary").getFormattedText();
            length = minecraft.fontRenderer.getStringWidth(str);
            font.drawString(str, cx + 131 - length / 2, cy + 49, RenderUtil.calculateColorBrightness(secondaryColor) > 0.179 ? 0x0 : 0xFFFFFF);

            if (RegenConfig.COMMON.infiniteRegeneration.get())
                str = new TranslationTextComponent("regeneration.gui.infinite_regenerations").getFormattedText();
            else
                str = new TranslationTextComponent("regeneration.gui.remaining_regens.status").getFormattedText() + " " + cap.getRegenerationsLeft();
            length = minecraft.fontRenderer.getStringWidth(str);
            font.drawString(str, cx + 86 - length / 2, cy + 21, Color.DARK_GRAY.getRGB());
        });
    }

    @Override
    public void onChangeSliderValue(Slider slider) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putFloat("PrimaryRed", (float) slidePrimaryRed.getValue());
        nbt.putFloat("PrimaryGreen", (float) slidePrimaryGreen.getValue());
        nbt.putFloat("PrimaryBlue", (float) slidePrimaryBlue.getValue());

        nbt.putFloat("SecondaryRed", (float) slideSecondaryRed.getValue());
        nbt.putFloat("SecondaryGreen", (float) slideSecondaryGreen.getValue());
        nbt.putFloat("SecondaryBlue", (float) slideSecondaryBlue.getValue());
        NetworkDispatcher.sendToServer(new UpdateColorMessage(nbt));
    }
}
