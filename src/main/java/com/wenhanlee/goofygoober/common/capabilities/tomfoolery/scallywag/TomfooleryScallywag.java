package com.wenhanlee.goofygoober.common.capabilities.tomfoolery.scallywag;

import java.util.Random;

public class TomfooleryScallywag implements ITomfooleryScallywag {

    private boolean isEligible;
    private boolean isScallywag;
    public int counter;
    public int limit;

    public TomfooleryScallywag() {
        this.isEligible = true;
        this.isScallywag = false;
        this.counter = 0;
        rollLimit();
    }

    @Override
    public void setEligibility(boolean isEligible) { this.isEligible = isEligible; }

    @Override
    public void setScallywag(boolean isScallywag) { this.isScallywag = isScallywag; }

    @Override
    public boolean isEligible() { return this.isEligible; }

    @Override
    public boolean isScallywag() { return this.isScallywag; }

    @Override
    public void incrementCounter() { this.counter++; }

    @Override
    public void resetCounter() { this.counter = 0; }

    @Override
    public void rollLimit() {
        // every 0.5 - 1 second, a goofy noise plays
        Random random = new Random();
        this.limit = 10 + random.nextInt(10);
    }

}
