package com.leecrafts.goofygoober.common.capabilities.ambient;

public interface IAmbientCounter {
    void incrementCounter();
    void resetCounter();
    void rollLimit();
    void rollSleepingNoise();
}
