package com.wenhanlee.goofygoober.common.events.custommobnoise;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.common.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.common.capabilities.ambient.AmbientCounter;
import com.wenhanlee.goofygoober.common.capabilities.ambient.AmbientCounterProvider;
import com.wenhanlee.goofygoober.common.capabilities.ambient.IAmbientCounter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
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

    @SubscribeEvent
    public static void sleepEvent(EntityEvent.Size event) {
        Entity entity = event.getEntity();
        if (entity.getPose() == Pose.SLEEPING) {
            entity.getCapability(ModCapabilities.AMBIENT_COUNTER_CAPABILITY).ifPresent(iAmbientCounter -> {
                AmbientCounter ambientCounter = (AmbientCounter) iAmbientCounter;
                ambientCounter.rollSleepingNoise();
                entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ambientCounter.sleepingNoise, SoundSource.AMBIENT, 1.0F, 0.9F + CustomMobNoiseHelper.random.nextFloat(0.6F));
                ambientCounter.resetCounter();
                ambientCounter.rollLimit();
            });
        }
    }

}
