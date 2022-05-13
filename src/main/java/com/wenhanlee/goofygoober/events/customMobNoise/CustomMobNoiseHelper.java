package com.wenhanlee.goofygoober.events.customMobNoise;

import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.capabilities.ambient.AmbientCounter;
import com.wenhanlee.goofygoober.sounds.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

public class CustomMobNoiseHelper {

    static Random random = new Random();
    public HashMap<String, Float> damageSourceKnockback;

    public CustomMobNoiseHelper() {
        initializeDamageSourceKnockback();
    }

    public void initializeDamageSourceKnockback() {
        damageSourceKnockback = new HashMap<>();
        damageSourceKnockback.put("hotFloor", 0.75F);
        damageSourceKnockback.put("sweetBerryBush", 1.5F);
        damageSourceKnockback.put("cactus", 1.5F);
        damageSourceKnockback.put("inFire", 2.0F);
        damageSourceKnockback.put("lava", 5.0F);
        damageSourceKnockback.put("stalagmite", 7.5F);
    }

    // don't add @SubscribeEvent here!
    public void scream(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();
        String source = event.getSource().getMsgId();
        if (damageSourceKnockback.get(source) != null) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, damageSourceKnockback.get(source), 0.0D));
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.SCREAM.get(), SoundSource.AMBIENT, 1.0F, 0.9F + random.nextFloat(0.2F));
        }
    }

    // custom ambient noises for players or villagers
    public void ambient(LivingEntity entity) {
        entity.getCapability(ModCapabilities.AMBIENT_COUNTER_CAPABILITY).ifPresent(iAmbientCounter -> {
            AmbientCounter ambientCounter = (AmbientCounter) iAmbientCounter;
            ambientCounter.incrementCounter();
            if (ambientCounter.counter >= ambientCounter.limit) {

                // ambient panicking noise
                // for non-player mobs that can panic
                // TODO make this work for every mob that can panic, not just villagers
                if (entity instanceof Mob mob) {
                    panic(ambientCounter, mob);
                }

                // ambient snoring noise
                // for players and villagers
                snore(ambientCounter, entity);

            }
        });
    }

    public void panic(AmbientCounter ambientCounter, Mob mob) {
        Optional<Activity> activity = mob.getBrain().getActiveNonCoreActivity();
        if (activity.isPresent() && activity.get().getName().equals("panic")) {
            // TODO add more mob panic noises
            mob.playSound(ModSounds.SKEDADDLE.get(), 1.0F, 0.9F + random.nextFloat(0.2F));
            ambientCounter.resetCounter();
            ambientCounter.rollLimit();
        }
    }

    public void snore(AmbientCounter ambientCounter, LivingEntity entity) {
        if (entity.isSleeping()) {
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ambientCounter.sleepingNoise, SoundSource.AMBIENT, 1.0F, 0.9F + random.nextFloat(0.6F));
            ambientCounter.resetCounter();
            ambientCounter.rollLimit();
        }
        else {
            ambientCounter.rollSleepingNoise();
        }
    }
}
