package com.wenhanlee.goofygoober.sounds;

import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.capabilities.time.TimeCounter;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

public class CustomMobNoise {

    static Random random = new Random();
    public HashMap<String, Float> damageSourceKnockback;

    public CustomMobNoise() {
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
//        if (event.getSource() instanceof IndirectEntityDamageSource ieds) {
//            if (ieds.getDirectEntity() != null) {
//                source = ieds.getDirectEntity().getType().toShortString();
//            }
//        }
        if (damageSourceKnockback.get(source) != null) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, damageSourceKnockback.get(source), 0.0D));
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.SCREAM.get(), SoundSource.AMBIENT, 1.0F, 0.9F + random.nextFloat(0.2F));
        }
    }

    // custom ambient noises for players or villagers
    public void ambient(LivingEntity entity) {
        entity.getCapability(ModCapabilities.TIME_COUNTER_CAPABILITY).ifPresent(iTimeCounter -> {
            TimeCounter timeCounter = (TimeCounter) iTimeCounter;
            timeCounter.incrementCounter();
            if (timeCounter.counter >= timeCounter.limit) {

                // ambient panicking noise
                // for non-player mobs that can panic
                // TODO make this work for every mob that can panic, not just villagers
                if (entity instanceof Mob mob) {
                    panic(timeCounter, mob);
                }

                // ambient snoring noise
                // for players and villagers
                snore(timeCounter, entity);

            }
        });
    }

    public void panic(TimeCounter timeCounter, Mob mob) {
        Optional<Activity> activity = mob.getBrain().getActiveNonCoreActivity();
        if (activity.isPresent() && activity.get().getName().equals("panic")) {
            // TODO add more mob panic noises
            mob.playSound(ModSounds.SKEDADDLE.get(), 1.0F, 0.9F + random.nextFloat(0.2F));
            timeCounter.resetCounter();
            timeCounter.rollLimit();
        }
    }

    public void snore(TimeCounter timeCounter, LivingEntity entity) {
        if (entity.isSleeping()) {
            entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), timeCounter.sleepingNoise, SoundSource.AMBIENT, 1.0F, 0.9F + random.nextFloat(0.6F));
            timeCounter.resetCounter();
            timeCounter.rollLimit();
        }
        else {
            timeCounter.rollSleepingNoise();
        }
    }
}
