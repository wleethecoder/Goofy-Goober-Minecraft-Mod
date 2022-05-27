package com.leecrafts.goofygoober.common.misc;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

// Useful, frequently-used helper functions
public class Utilities {

    public static final Random random = new Random();
    public static final float DEFAULT_PITCH_LOW = 0.9F;
    public static final float DEFAULT_PITCH_HIGH = 1.1F;

//    public static void playSound(Entity entity, SoundEvent soundEvent, SoundSource soundSource, float volume, float pitchLow, float pitchHigh) {
//        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, soundSource, volume, pitchLow + random.nextFloat(pitchHigh - pitchLow));
//    }

    public static void playSound(Entity entity, SoundEvent soundEvent, SoundSource soundSource) {
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, soundSource, 1, DEFAULT_PITCH_LOW + random.nextFloat(DEFAULT_PITCH_HIGH - DEFAULT_PITCH_LOW));
    }

    public static void playSound(Entity entity, SoundEvent soundEvent, float volume, float pitchLow, float pitchHigh) {
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, entity.getSoundSource(), volume, pitchLow + random.nextFloat(pitchHigh - pitchLow));
    }

    public static void playSound(Entity entity, SoundEvent soundEvent, float volume) {
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, entity.getSoundSource(), volume, DEFAULT_PITCH_LOW + random.nextFloat(DEFAULT_PITCH_HIGH - DEFAULT_PITCH_LOW));
    }

    public static void playSound(Entity entity, SoundEvent soundEvent) {
        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, entity.getSoundSource(), 1, DEFAULT_PITCH_LOW + random.nextFloat(DEFAULT_PITCH_HIGH - DEFAULT_PITCH_LOW));
    }

}
