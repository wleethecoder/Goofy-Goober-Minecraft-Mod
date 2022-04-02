package com.wenhanlee.goofygoober;

import com.wenhanlee.goofygoober.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;

import java.util.Random;

public class PlayersAndVillagersTickCounter {
//    private Entity playerOrVillager;
    private int tickCount;
//    private String UUID;
    private SoundEvent sleepingNoise;

    public PlayersAndVillagersTickCounter() {
//        playerOrVillager = entity;
        tickCount = 0;
//        UUID = entity.getStringUUID();
        rollSleepingNoise();
    }

//    public Entity getEntity() { return playerOrVillager; }

    public int getTickCount() { return tickCount; }

    public void incrementTickCount() {
        tickCount++;
        if (tickCount >= 72000) resetTickCount();
    }

    public void resetTickCount() { tickCount = 0; }

//    public String getUUID() { return UUID; }

    public SoundEvent getSleepingNoise() { return sleepingNoise; }

    public void rollSleepingNoise() {
        Random random = new Random();
        int random_int = random.nextInt(2);
        sleepingNoise = ModSounds.SNORE_LOUD.get();
        if (random_int == 1) sleepingNoise = ModSounds.SNORE_MIMIMI.get();
    }

}
