package com.leecrafts.goofygoober.common.events.custommobnoise;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.ambient.AmbientCounter;
import com.leecrafts.goofygoober.common.misc.Utilities;
import com.leecrafts.goofygoober.common.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.HashMap;
import java.util.Optional;

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

    public static void scream(LivingEntity livingEntity, String source) {
        initializeIfNull();
        if (damageSourceKnockback.containsKey(source)) {
            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(0, damageSourceKnockback.get(source), 0));
            Utilities.playSound(livingEntity, "scream");
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
                if (livingEntity instanceof Mob mob) panic(mob);

                // ambient snoring noise
                // for players and villagers
                if (livingEntity.isSleeping()) snore(livingEntity, ambientCounter.sleepingNoise);

                // ambient freezing noise
                if (livingEntity.isFreezing()) Utilities.playSound(livingEntity, "teeth_chatter");

                ambientCounter.resetCounter();
                ambientCounter.rollLimit();
            }
        });
    }

    public static void panic(Mob mob) {
        if (mob instanceof Villager villager) {
            Optional<Activity> activity = villager.getBrain().getActiveNonCoreActivity();
            if (activity.isPresent() && activity.get() == Activity.PANIC) Utilities.playSound(villager, "mob_skedaddle");
        }
        else if (mob instanceof Animal animal) {
            if (animal.goalSelector.getRunningGoals().anyMatch(wrappedGoal ->
                    wrappedGoal.getGoal().getClass().equals(PanicGoal.class))) {
                Utilities.playSound(animal, "mob_skedaddle");
            }
        }
    }

    public static void snore(LivingEntity livingEntity, String sleepingNoise) {
        float pitchHigh = 1.5F;
        if (sleepingNoise.equals("snore_mimimi")) pitchHigh = Utilities.DEFAULT_PITCH_HIGH;
        Utilities.playSound(livingEntity, sleepingNoise, Utilities.DEFAULT_PITCH_LOW, pitchHigh);
    }
}
