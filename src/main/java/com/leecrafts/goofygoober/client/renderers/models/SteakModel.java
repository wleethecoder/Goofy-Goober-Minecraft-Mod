// Made with Blockbench 4.2.4
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


package com.leecrafts.goofygoober.client.renderers.models;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.entities.SteakEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

//public class SteakModel<T extends SteakEntity> extends EntityModel<T> {
public class SteakModel<T extends Mob> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(GoofyGoober.MOD_ID, "steak"), "main");
    private final ModelPart half1;
    private final ModelPart half2;

    public SteakModel(ModelPart root) {
        this.half1 = root.getChild("half1");
        this.half2 = root.getChild("half2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition half1 = partdefinition.addOrReplaceChild("half1", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, 24.0F, 3.0F, 0.0F, 1.5708F, 0.0F));

        half1.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 66).addBox(-20.0F, -6.0F, -7.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 31).addBox(-23.0F, -6.0F, -6.0F, 15.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 38).addBox(-25.0F, -6.0F, -5.0F, 19.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 45).addBox(-28.0F, -6.0F, -4.0F, 24.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(-29.0F, -6.0F, -3.0F, 27.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 31).addBox(-30.0F, -6.0F, -2.0F, 29.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 10).addBox(-31.0F, -6.0F, -1.0F, 31.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 66).addBox(-24.0F, -6.0F, 14.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 17).addBox(-26.0F, -6.0F, 13.0F, 9.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 10).addBox(-27.0F, -6.0F, 12.0F, 12.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(62, 24).addBox(-28.0F, -6.0F, 11.0F, 14.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 59).addBox(-30.0F, -6.0F, 10.0F, 17.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 59).addBox(-31.0F, -6.0F, 9.0F, 19.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 52).addBox(-31.0F, -6.0F, 8.0F, 20.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 52).addBox(-32.0F, -6.0F, 7.0F, 22.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(50, 45).addBox(-32.0F, -6.0F, 6.0F, 24.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 24).addBox(-32.0F, -6.0F, 5.0F, 30.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 17).addBox(-32.0F, -6.0F, 4.0F, 31.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-32.0F, -6.0F, 0.0F, 32.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

        PartDefinition half2 = partdefinition.addOrReplaceChild("half2", CubeListBuilder.create(), PartPose.offsetAndRotation(-4.0F, 24.0F, -3.0F, 0.0F, 1.5708F, 0.0F));

        half2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 66).mirror().addBox(12.0F, -6.0F, -7.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(60, 31).mirror().addBox(8.0F, -6.0F, -6.0F, 15.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(56, 38).mirror().addBox(6.0F, -6.0F, -5.0F, 19.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 45).mirror().addBox(4.0F, -6.0F, -4.0F, 24.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 38).mirror().addBox(2.0F, -6.0F, -3.0F, 27.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 31).mirror().addBox(1.0F, -6.0F, -2.0F, 29.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 10).mirror().addBox(0.0F, -6.0F, -1.0F, 31.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(18, 66).mirror().addBox(18.0F, -6.0F, 14.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(64, 17).mirror().addBox(17.0F, -6.0F, 13.0F, 9.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(64, 10).mirror().addBox(15.0F, -6.0F, 12.0F, 12.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(62, 24).mirror().addBox(14.0F, -6.0F, 11.0F, 14.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(40, 59).mirror().addBox(13.0F, -6.0F, 10.0F, 17.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 59).mirror().addBox(12.0F, -6.0F, 9.0F, 19.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(46, 52).mirror().addBox(11.0F, -6.0F, 8.0F, 20.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 52).mirror().addBox(10.0F, -6.0F, 7.0F, 22.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(50, 45).mirror().addBox(8.0F, -6.0F, 6.0F, 24.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 24).mirror().addBox(2.0F, -6.0F, 5.0F, 30.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 17).mirror().addBox(1.0F, -6.0F, 4.0F, 31.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).mirror().addBox(0.0F, -6.0F, 0.0F, 32.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.half1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        this.half2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

}