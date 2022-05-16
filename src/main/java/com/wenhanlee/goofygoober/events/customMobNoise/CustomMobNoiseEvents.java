package com.wenhanlee.goofygoober.events.customMobNoise;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.capabilities.ambient.AmbientCounterProvider;
import com.wenhanlee.goofygoober.capabilities.ambient.IAmbientCounter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID)
public class CustomMobNoiseEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IAmbientCounter.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity && !event.getObject().getCommandSenderWorld().isClientSide()) {
            AmbientCounterProvider ambientCounterProvider = new AmbientCounterProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "ambient_counter"), ambientCounterProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "ambient_counter_limit"), ambientCounterProvider);
            event.addListener(ambientCounterProvider::invalidate);
        }
    }

//    @SubscribeEvent
//    public static void onJoin(EntityJoinWorldEvent event) {
//        customMobNoise = new CustomMobNoise();
//    }

    // sharp scream of pain whenever touching a painful object
    // throws the mob high up in the air
    @SubscribeEvent
    public static void scream(LivingDamageEvent event) {
        if (!event.getEntity().level.isClientSide()) {
            CustomMobNoiseHelper.scream(event);
        }
    }

    // Players and Villagers snore when they sleep
    @SubscribeEvent
    public static void tick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide()) {
            CustomMobNoiseHelper.ambient(entity);
        }
    }

}
