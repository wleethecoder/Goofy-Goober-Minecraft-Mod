package com.leecrafts.goofygoober.common.capabilities.tomfoolery.mob;

import com.leecrafts.goofygoober.common.misc.Utilities;

public class TomfooleryMob implements ITomfooleryMob {

    private boolean isEligibleToSummonNearbyMobs;
    private boolean isSummoned;
    public int counter;
    public int limit;

    public TomfooleryMob() {
        this.isEligibleToSummonNearbyMobs = true;
        this.counter = 0;
        this.rollLimit();
    }

    @Override
    public void setEligibility(boolean isEligibleToSummonNearbyMobs) { this.isEligibleToSummonNearbyMobs = isEligibleToSummonNearbyMobs; }

    @Override
    public void setSummoned(boolean isSummoned) { this.isSummoned = isSummoned; }

    @Override
    public boolean isEligibleToSummonNearbyMobs() { return this.isEligibleToSummonNearbyMobs; }

    @Override
    public boolean isSummoned() { return this.isSummoned; }

    @Override
    public void incrementCounter() { this.counter++; }

    @Override
    public void resetCounter() { this.counter = 0; }

    @Override
    public void rollLimit() {
        // every 0.5 - 1 second, a goofy noise plays
        this.limit = 10 + Utilities.random.nextInt(10);
    }

}
