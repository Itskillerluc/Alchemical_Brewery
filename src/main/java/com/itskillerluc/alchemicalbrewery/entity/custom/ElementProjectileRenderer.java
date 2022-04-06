package com.itskillerluc.alchemicalbrewery.entity.custom;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
		//pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot()) - 90.0F));
		//pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot()) + 90.0F));
		VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(pEntity)));
		this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		pMatrixStack.popPose();
		super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(ElementProjectileEntity pEntity) 
	{
		return ELEMENT_PROJECTILE_LOCATION;
	}
}
