package com.leecrafts.goofygoober.client.renderers;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.client.renderers.models.SteakModel;
import com.leecrafts.goofygoober.common.entities.ModEntities;
import com.leecrafts.goofygoober.common.entities.SteakEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

//public class SteakRenderer<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, M> {
public class SteakRenderer<T extends SteakEntity> extends MobRenderer<T, SteakModel<T>> {
//public class SteakRenderer<T extends Mob> extends MobRenderer<T, EntityModel<T>> {

    public static final ResourceLocation TEXTURE =
            new ResourceLocation(GoofyGoober.MOD_ID, "textures/entities/steak.png");

    public SteakRenderer(EntityRendererProvider.Context context) {
        super(context, new SteakModel<>(context.bakeLayer(SteakModel.LAYER_LOCATION)), 0.75F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T pEntity) { return TEXTURE; }

    @Override
    public void render(@NotNull T pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

}