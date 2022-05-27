package com.leecrafts.goofygoober.common.events.custommobnoise;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.ambient.AmbientCounter;
import com.leecrafts.goofygoober.common.misc.Utilities;
import com.leecrafts.goofygoober.common.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

public class CustomMobNoiseHelper {

    private static HashMap<String, Float> damageSourceKnockback;

    private static void initializeIfNull() {
        damageSourceKnockback = new HashMap<>();
        damageSourceKnockback.put("hotFloor", 0.75F);
        damageSourceKnockback.put("sweetBerryBush", 1.5F);
        damageSourceKnockback.put("cactus", 1.5F);
        damageSourceKnockback.put("inFire", 2.0F);
        damageSourceKnockback.put("lava", 5.0F);
        damageSourceKnockback.put("stalagmite", 7.5F);
    }

    // don't add @SubscribeEvent here!
    public static void scream(LivingDamageEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();
        String source = event.getSource().getMsgId();
        initializeIfNull();
        if (damageSourceKnockback.get(source) != null) {
            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0, damageSourceKnockback.get(source), 0));
            Utilities.playSound(livingEntity, ModSounds.SCREAM.get());
        }
    }

    // custom ambient noises for players or villagers
    public static void ambient(LivingEntity livingEntity) {
        livingEntity.getCapability(ModCapabilities.AMBIENT_COUNTER_CAPABILITY).ifPresent(iAmbientCounter -> {
            AmbientCounter ambientCounter = (AmbientCounter) iAmbientCounter;
            ambientCounter.incrementCounter();
            if (ambientCounter.counter >= ambientCounter.limit) {

                // ambient panicking noise
                // for non-player mobs that can panic
                // TODO make this work for every mob that can panic, not just villagers
                if (livingEntity instanceof Mob mob) {
                    panic(mob);
                }

                // ambient snoring noise
                // for players and villagers
                if (livingEntity.isSleeping()) snore(livingEntity, ambientCounter.sleepingNoise);

                ambientCounter.resetCounter();
                ambientCounter.rollLimit();
            }
        });
    }

    public static void panic(Mob mob) {
        Optional<Activity> activity = mob.getBrain().getActiveNonCoreActivity();
        if (activity.isPresent() && activity.get().getName().equals("panic")) {
            Utilities.playSound(mob, ModSounds.SKEDADDLE.get());
        }
    }

    public static void snore(LivingEntity livingEntity, SoundEvent sleepingNoise) {
        float pitchHigh = 1.5F;
        if (sleepingNoise == ModSounds.SNORE_MIMIMI.get()) pitchHigh = Utilities.DEFAULT_PITCH_HIGH;
        Utilities.playSound(livingEntity, sleepingNoise, 1, Utilities.DEFAULT_PITCH_LOW, pitchHigh);
    }
}
