package com.wenhanlee.goofygoober.capabilities.tomfoolery.cooldownCounter;

public class TomfooleryCooldownCounter implements ITomfooleryCooldownCounter {

    public int counter;
    public final int limit;
    public boolean cooldown;

    public TomfooleryCooldownCounter() {
        this.counter = 0;
//        this.limit = 2400; // 2 minutes
        this.limit = 300; // 15 seconds
        this.cooldown = false;
    }

    @Override
    public void incrementCounter() { this.counter++; }

    @Override
    public void resetCounter() { this.counter = 0; }

}
