package com.itskillerluc.alchemicalbrewery.entity.custom;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElementProjectileRenderer extends EntityRenderer<ElementProjectileEntity> {
    public static final ResourceLocation ELEMENT_PROJECTILE_LOCATION = new ResourceLocation(AlchemicalBrewery.MOD_ID, "textures/entity/elementprojectile.png");
    private final ElementProjectileModel model;

    public ElementProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ElementProjectileModel(context.bakeLayer(ElementProjectileModel.LAYER_LOCATION));
    }

    public void render(ElementProjectileEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(pBuffer, this.model.renderType(this.getTextureLocation(pEntity)), false, true);
        this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(ElementProjectileEntity pEntity)
    {
        return ELEMENT_PROJECTILE_LOCATION;
    }
}