package com.itskillerluc.alchemicalbrewery.screen;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.container.ElementalInjectorContainer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;

public class ElementalInjectorScreen extends AbstractContainerScreen<ElementalInjectorContainer> {
    private static final ResourceLocation GUI = new ResourceLocation(AlchemicalBrewery.MOD_ID, "textures/gui/elementalinjector_gui.png");

    public ElementalInjectorScreen(ElementalInjectorContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pPoseStack, pMouseX, pMouseY);

    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight   );
        blit(pPoseStack, x + 75+menu.getScaledProgress(), y + 39, 177, 2, 36, 9);
        drawString(pPoseStack, this.font, "Charge:"+menu.getCharge(), x+120, y+5, Color.GRAY.getRGB());

    }
}
