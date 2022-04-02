package com.wenhanlee.goofygoober.capabilities.time;

import com.wenhanlee.goofygoober.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;

import java.util.Random;

public class TimeCounter implements ITimeCounter {
    public int counter;
    public int limit;
    public SoundEvent sleepingNoise;
    public TimeCounter() {
        counter = 0;
        rollLimit();
        rollSleepingNoise();
    }
    @Override
    public void incrementCounter() {
        this.counter++;
        if (counter < 0) this.counter = 200;
    }
    @Override
    public void resetCounter() { this.counter = 0; }
    @Override
    public void rollLimit() {
        Random random = new Random();
        this.limit = random.nextInt(100) + 100;
    }
    @Override
    public void rollSleepingNoise() {
        // adds randomness to how long it takes for the mob to make another noise
        Random random = new Random();
        sleepingNoise = ModSounds.SNORE_LOUD.get();
        if (random.nextInt(2) == 1) sleepingNoise = ModSounds.SNORE_MIMIMI.get();
    }
}
