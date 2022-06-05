package com.leecrafts.goofygoober.client.events.steak;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.effects.ModEffects;
import com.leecrafts.goofygoober.common.entities.SteakEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SteakClientEvents {

    private static boolean renderingSteakEntity = false;
    public static SteakEntity steakEntity;

    @SubscribeEvent
    public static void steak(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        LivingEntity livingEntity = event.getEntity();
        if (localPlayer != null && localPlayer.getActiveEffectsMap() != null && !localPlayer.is(livingEntity)) {
            if (localPlayer.hasEffect(ModEffects.HALLUCINATING.get())) {
                if (!renderingSteakEntity) {
                    try {
                        renderingSteakEntity = true;
                        event.setCanceled(true);
                        SteakClientHelper.refreshSteakEntity(localPlayer, livingEntity);
//                        steakEntity.setYHeadRot(livingEntity.getYHeadRot());
//                        steakEntity.yHeadRotO = livingEntity.yHeadRotO;
                        steakEntity.setYBodyRot(livingEntity.yBodyRot);
                        steakEntity.yBodyRotO = livingEntity.yBodyRotO;
                        steakEntity.setPose(livingEntity.getPose());
                        Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(steakEntity).render(
                                steakEntity,
                                steakEntity.yBodyRot,
                                event.getPartialTick(),
                                event.getPoseStack(),
                                event.getMultiBufferSource(),
                                event.getPackedLight()
                        );
                    } finally {
                        renderingSteakEntity = false;
                    }
                }
            }
        }
    }

}
