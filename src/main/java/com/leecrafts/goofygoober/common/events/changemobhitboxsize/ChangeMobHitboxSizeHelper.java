package com.leecrafts.goofygoober.common.events.changemobhitboxsize;

public class ChangeMobHitboxSizeHelper {

    public static double roundDigits(double num, int digits) {
        double tenPower = Math.pow(10, digits);
        return Math.round(num * tenPower) / tenPower;
    }

}
