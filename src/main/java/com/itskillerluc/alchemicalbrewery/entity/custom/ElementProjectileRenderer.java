package com.itskillerluc.alchemicalbrewery.entity.custom;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ElementProjectileRenderer extends EntityRenderer<ElementProjectileEntity> 
{
	public static final ResourceLocation ELEMENT_PROJECTILE_LOCATION = new ResourceLocation(AlchemicalBrewery.MOD_ID, "textures/entity/elementprojectile.png");
	private final ElementProjectileModel<ElementProjectileEntity> model;

	public ElementProjectileRenderer(EntityRendererProvider.Context context) 
	{
		super(context);
		this.model = new ElementProjectileModel<>(context.bakeLayer(ElementProjectileModel.LAYER_LOCATION));
	}

	public void render(ElementProjectileEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) 
	{
		pMatrixStack.pushPose();
		VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(pBuffer, RenderType.entityTranslucent(this.getTextureLocation(pEntity)), false, true);
		this.model.renderToBuffer(pMatrixStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, Integer.decode("0X"+String.format("%06X",pEntity.getColor()).substring(0, 2))*0.00392156862f,Integer.decode("0X"+String.format("%06X",pEntity.getColor()).substring(2, 4))*0.00392156862f,Integer.decode("0X"+String.format("%06X",pEntity.getColor()).substring(4, 6))*0.00392156862f, 0.5F);
		pMatrixStack.popPose();
		super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(ElementProjectileEntity pEntity) 
	{
		return ELEMENT_PROJECTILE_LOCATION;
	}
}
