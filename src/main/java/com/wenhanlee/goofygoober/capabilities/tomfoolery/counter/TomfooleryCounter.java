package com.wenhanlee.goofygoober.capabilities.tomfoolery.counter;

public class TomfooleryCounter implements ITomfooleryCounter {

    public int counter;
    public final int limit;
    public boolean cooldown;

    public TomfooleryCounter() {
        this.counter = 0;
//        this.limit = 2400;
        this.limit = 300;
        this.cooldown = false;
    }

    @Override
    public void incrementCounter() { this.counter++; }

    @Override
    public void resetCounter() { this.counter = 0; }

}
