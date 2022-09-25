package com.itskillerluc.alchemicalbrewery.screen;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.container.ElementalInjectorContainer;
import com.itskillerluc.alchemicalbrewery.util.Utilities;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static com.itskillerluc.alchemicalbrewery.screen.ElementalExtractorScreen.drawBg;

public class ElementalInjectorScreen extends AbstractContainerScreen<ElementalInjectorContainer> {
    @SuppressWarnings("SpellCheckingInspection")
    private static final ResourceLocation GUI = new ResourceLocation(AlchemicalBrewery.MOD_ID, "textures/gui/elementalinjector_gui.png");

    public ElementalInjectorScreen(ElementalInjectorContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(@NotNull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(@NotNull PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        RenderSystem.setShaderTexture(0, GUI);
        drawBg(pPoseStack, x, y, imageWidth, imageHeight, this);
        blit(pPoseStack, x + 75+menu.getScaledProgress(), y + 39, 177, 2, 36, 9);

        Utilities.drawStringNoShadow(this.font, pPoseStack, "Charge:", x+105f, y+6f, Color.orange.darker().getRGB());

        pPoseStack.pushPose();

        int stringWidth = this.font.width(String.valueOf(menu.getCharge()));
        float scale = 1;
        if(stringWidth>30) {
            scale = 30f / stringWidth;
        }
        pPoseStack.scale(scale, scale, scale);
        Utilities.drawStringNoShadow(this.font, pPoseStack, String.valueOf(menu.getCharge()), (x+143f)/scale, ((y+6f)/scale)+(9-9*scale), Color.orange.darker().getRGB());
        pPoseStack.popPose();
    }
}
