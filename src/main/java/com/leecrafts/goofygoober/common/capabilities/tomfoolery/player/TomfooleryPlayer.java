package com.leecrafts.goofygoober.common.capabilities.tomfoolery.player;

public class TomfooleryPlayer implements ITomfooleryPlayer {

    public int counter;
    public final int LIMIT;

    public TomfooleryPlayer() {
//        this.LIMIT = 2400; // 2 minutes
        this.LIMIT = 300; // 15 seconds
        this.counter = LIMIT;
    }

    @Override
    public void incrementCounter() { this.counter++; }

    @Override
    public void resetCounter() { this.counter = 0; }

}
