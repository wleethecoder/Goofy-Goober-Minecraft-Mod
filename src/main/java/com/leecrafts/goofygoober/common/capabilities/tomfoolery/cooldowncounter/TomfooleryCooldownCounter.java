package com.leecrafts.goofygoober.common.capabilities.tomfoolery.cooldowncounter;

public class TomfooleryCooldownCounter implements ITomfooleryCooldownCounter {

    public int counter;
    public final int limit;

    public TomfooleryCooldownCounter() {
//        this.limit = 2400; // 2 minutes
        this.limit = 300; // 15 seconds
        this.counter = limit;
    }

    @Override
    public void incrementCounter() { this.counter++; }

    @Override
    public void resetCounter() { this.counter = 0; }

}
