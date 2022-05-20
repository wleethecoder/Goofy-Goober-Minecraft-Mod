package com.wenhanlee.goofygoober.common.capabilities.ambient;

import net.minecraft.sounds.SoundEvent;

public interface IAmbientCounter {
    void incrementCounter();
    void resetCounter();
    void rollLimit();
    void rollSleepingNoise();
}
