package com.itskillerluc.alchemicalbrewery.entity.custom;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings (I hope it works for 1.18.2)
// Paste this class into your mod and generate all required imports


import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class ElementProjectileModel<T extends ElementProjectileEntity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(AlchemicalBrewery.MOD_ID, "elementprojectile.png"), "main");
	private final ModelPart bb_main;
	private static final int offset = -5;

	public ElementProjectileModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -10F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, -1.0F-offset+13f, 0.0F));

		PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, -1.0F-offset-13, 0.0F, -1.5708F, -0.3927F, 1.5708F));

		PartDefinition cube_r2 = bb_main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, -1.0F-offset-13, 0.0F, 0.0F, -1.5708F, 0.0F));

		PartDefinition cube_r3 = bb_main.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, -1.0F-offset-13, 0.0F, -1.5708F, -1.1781F, 1.5708F));

		PartDefinition cube_r4 = bb_main.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, -1.0F-offset-13, 0.0F, -1.5708F, -0.7854F, 1.5708F));

		PartDefinition cube_r5 = bb_main.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, -1.0F-offset-13, 0.0F, 0.0F, 0.0F, 1.1781F));

		PartDefinition cube_r6 = bb_main.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, -1.0F-offset-13, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition cube_r7 = bb_main.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.0F, -1.0F-offset-13, 0.0F, 0.0F, 0.0F, 0.3927F));

		return LayerDefinition.create(meshdefinition, 16, 16);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bb_main.render(poseStack, buffer, packedLight, packedOverlay);
	}
}