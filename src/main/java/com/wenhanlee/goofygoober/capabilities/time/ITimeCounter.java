package com.wenhanlee.goofygoober.capabilities.time;

public interface ITimeCounter {
    void incrementCounter();
    void resetCounter();
    void rollLimit();
    void rollSleepingNoise();
}
