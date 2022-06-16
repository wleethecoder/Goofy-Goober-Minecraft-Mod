package com.leecrafts.goofygoober.common.capabilities.tomfoolery.mob;

public interface ITomfooleryMob {
    void setEligibility(boolean isEligibleToSummonNearbyMobs);
    void setSummoned(boolean isSummoned);
    boolean isEligibleToSummonNearbyMobs();
    boolean isSummoned();

    void incrementCounter();
    void resetCounter();
    void rollLimit();
}
