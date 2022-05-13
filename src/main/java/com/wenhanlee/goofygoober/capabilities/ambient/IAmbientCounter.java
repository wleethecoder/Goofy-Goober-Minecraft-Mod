package com.wenhanlee.goofygoober.capabilities.ambient;

public interface IAmbientCounter {
    void incrementCounter();
    void resetCounter();
    void rollLimit();
    void rollSleepingNoise();
}
