package com.itskillerluc.alchemicalbrewery.entity.custom;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElementProjectileRenderer extends EntityRenderer<ElementProjectileEntity> 
{
	public static final ResourceLocation ELEMENT_PROJECTILE_LOCATION = new ResourceLocation(AlchemicalBrewery.MOD_ID, "textures/entity/elementprojectile.png");
	private final ElementProjectileModel<ElementProjectileEntity> model;
	private final ItemRenderer itemRenderer;

	public ElementProjectileRenderer(EntityRendererProvider.Context context) 
	{
		super(context);
		this.itemRenderer = context.getItemRenderer();
		this.model = new ElementProjectileModel<>(context.bakeLayer(ElementProjectileModel.LAYER_LOCATION));
	}

	public void render(ElementProjectileEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) 
	{

		if(pEntity.element.itemModel != Items.AIR){
			pMatrixStack.pushPose();
			float f = Mth.rotlerp(pEntity.yRotO, pEntity.getYRot(), pPartialTicks);
			float f1 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
			float f2 = (float)pEntity.tickCount + pPartialTicks;
			pMatrixStack.translate(0.0D, 0.25D, 0.0D);



			pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(f2  * pEntity.random1) * 360f));
			pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(Mth.cos(f2  *pEntity.random2) * 360f));
			pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(f2  *pEntity.random3) * 360.0F));
			this.model.setupAnim(pEntity, 0.0F, 0.0F, 0.0F, f, f1);
			pMatrixStack.scale(0.7f, 0.7f, 0.7f);
			BakedModel bakedmodel = this.itemRenderer.getModel(new ItemStack(pEntity.element.itemModel), pEntity.level, null, pEntity.getId());
			this.itemRenderer.renderStatic((LivingEntity)null,new ItemStack(pEntity.element.itemModel), ItemTransforms.TransformType.FIXED, false, pMatrixStack, pBuffer, pEntity.level, pPackedLight, OverlayTexture.NO_OVERLAY, 1);

			pMatrixStack.popPose();
		}else{
			pMatrixStack.pushPose();
			float f = Mth.rotlerp(pEntity.yRotO, pEntity.getYRot(), pPartialTicks);
			float f1 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
			float f2 = (float)pEntity.tickCount + pPartialTicks;
			pMatrixStack.translate(0.0D, 0.25D, 0.0D);



			pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(f2  * pEntity.random1) * 360f));
			pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(Mth.cos(f2  *pEntity.random2) * 360f));
			pMatrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(f2  *pEntity.random3) * 360.0F));
			this.model.setupAnim(pEntity, 0.0F, 0.0F, 0.0F, f, f1);
			VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(pBuffer, RenderType.tripwire(), false, true);
			this.model.renderToBuffer(pMatrixStack, vertexConsumer, pPackedLight, OverlayTexture.NO_OVERLAY, Integer.decode("0X"+String.format("%06X",pEntity.element.color).substring(0, 2))*0.00392156862f,Integer.decode("0X"+String.format("%06X",pEntity.element.color).substring(2, 4))*0.00392156862f,Integer.decode("0X"+String.format("%06X",pEntity.element.color).substring(4, 6))*0.00392156862f, 0.5F);
			pMatrixStack.popPose();
		}
	}

	@Override
	public ResourceLocation getTextureLocation(ElementProjectileEntity pEntity) 
	{
		return ELEMENT_PROJECTILE_LOCATION;
	}
}
