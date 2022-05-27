package com.leecrafts.goofygoober.common.capabilities.skedaddle;

public class Skedaddle implements ISkedaddle {

    public int skedaddleChargeCounter;
    public final int skedaddleChargeLimit;

    public boolean skedaddleCharging;
    public boolean skedaddleTakeoff;

    public final int skedaddleDuration;

    public boolean wPressed;

    public Skedaddle() {
        this.skedaddleChargeCounter = 0;
        this.skedaddleChargeLimit = 20;

        this.skedaddleCharging = false;
        this.skedaddleTakeoff = false;

        this.skedaddleDuration = 20 * 20;

        this.wPressed = false;
    }

    public void incrementCounter() {
        this.skedaddleChargeCounter++;
    }

    public void reset() {
        this.skedaddleTakeoff = false;
        this.skedaddleCharging = false;
        this.skedaddleChargeCounter = 0;
    }

}
