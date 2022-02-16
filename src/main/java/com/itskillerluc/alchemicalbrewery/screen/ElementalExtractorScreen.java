package com.itskillerluc.alchemicalbrewery.screen;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.container.ElementalExtractorContainer;
import com.itskillerluc.alchemicalbrewery.tileentity.ElementalExtractorTile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ElementalExtractorScreen extends AbstractContainerScreen<ElementalExtractorContainer> {
    private final ResourceLocation GUI = new ResourceLocation(AlchemicalBrewery.MOD_ID, "textures/gui/elementalextractor_gui.png");

    public ElementalExtractorScreen(ElementalExtractorContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBg(pPoseStack, pPartialTick, pMouseX, pMouseY);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderComponentHoverEffect(pPoseStack, null, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.clearColor(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bindForSetup(GUI);
        int i = this.getGuiLeft();
        int j = this.getGuiTop();
        this.blit(pPoseStack, i, j, 0, 0, this.getXSize(), this.getYSize());

        if (menu.isBurning()){
            this.blit(pPoseStack, i + 67, j + 51, 180, 4, 17, 14);
        }

    }


}
