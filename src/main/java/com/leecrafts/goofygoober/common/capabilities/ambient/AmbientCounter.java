package com.leecrafts.goofygoober.common.capabilities.ambient;

import com.leecrafts.goofygoober.common.misc.Utilities;
import com.leecrafts.goofygoober.common.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;

public class AmbientCounter implements IAmbientCounter {

    public int counter;
    public int limit;
    public SoundEvent sleepingNoise;

    public AmbientCounter() {
        this.counter = 0;
        this.rollLimit();
        this.rollSleepingNoise();
    }

    @Override
    public void incrementCounter() { this.counter++; }

    @Override
    public void resetCounter() { this.counter = 0; }

    @Override
    public void rollLimit() {
        // adds randomness to how long it takes for the mob to make another noise
        // 2.5 - 5 seconds
        this.limit = 50 + Utilities.random.nextInt(50);
    }

    @Override
    public void rollSleepingNoise() {
        int randInt = Utilities.random.nextInt(3);
        this.sleepingNoise = ModSounds.SNORE_LOUD.get();
        if (randInt == 1) this.sleepingNoise = ModSounds.SNORE_MIMIMI.get();
        if (randInt == 2) this.sleepingNoise = ModSounds.SNORE_WHISTLE.get();
    }

}
