package com.leecrafts.goofygoober.client.events;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.client.keys.KeyInit;
import com.leecrafts.goofygoober.client.renderers.SteakRenderer;
import com.leecrafts.goofygoober.client.renderers.models.SteakModel;
import com.leecrafts.goofygoober.common.entities.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) { KeyInit.init(); }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SteakModel.LAYER_LOCATION, SteakModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.STEAK_ENTITY.get(), SteakRenderer::new);
    }

}
