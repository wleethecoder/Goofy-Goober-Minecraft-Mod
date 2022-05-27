package com.leecrafts.goofygoober.common.events.custommobnoise;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.ambient.AmbientCounter;
import com.leecrafts.goofygoober.common.capabilities.ambient.AmbientCounterProvider;
import com.leecrafts.goofygoober.common.capabilities.ambient.IAmbientCounter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
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
        if (event.getObject() instanceof LivingEntity livingEntity && !livingEntity.getCommandSenderWorld().isClientSide()) {
            AmbientCounterProvider ambientCounterProvider = new AmbientCounterProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "ambient_counter"), ambientCounterProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "ambient_counter_limit"), ambientCounterProvider);
            if (!(livingEntity instanceof Player)) {
                event.addListener(ambientCounterProvider::invalidate);
            }
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
        LivingEntity livingEntity = event.getEntityLiving();
        if (!livingEntity.level.isClientSide()) {
            CustomMobNoiseHelper.ambient(livingEntity);
        }
    }

    @SubscribeEvent
    public static void sleepEvent(EntityEvent.Size event) {
        if (event.getEntity() instanceof LivingEntity livingEntity && !livingEntity.level.isClientSide() && livingEntity.getPose() == Pose.SLEEPING) {
            livingEntity.getCapability(ModCapabilities.AMBIENT_COUNTER_CAPABILITY).ifPresent(iAmbientCounter -> {
                AmbientCounter ambientCounter = (AmbientCounter) iAmbientCounter;
                ambientCounter.rollSleepingNoise();
                CustomMobNoiseHelper.snore(livingEntity, ambientCounter.sleepingNoise);
                ambientCounter.resetCounter();
                ambientCounter.rollLimit();
            });
        }
    }

}
