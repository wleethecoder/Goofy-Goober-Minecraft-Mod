package com.leecrafts.goofygoober.common.capabilities.tomfoolery.cooldowncounter;

public class TomfooleryCooldownCounter implements ITomfooleryCooldownCounter {

    public int counter;
    public final int LIMIT;

    public TomfooleryCooldownCounter() {
        this.LIMIT = 2400; // 2 minutes
//        this.LIMIT = 300; // 15 seconds
        this.counter = LIMIT;
    }

    @Override
    public void incrementCounter() { this.counter++; }

    @Override
    public void resetCounter() { this.counter = 0; }

}
