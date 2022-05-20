package com.wenhanlee.goofygoober.common.events.changemobsize;

public class ChangeMobSizeHelper {

    public static double roundDigits(double num, int digits) {
        double tenPower = Math.pow(10, digits);
        return Math.round(num * tenPower) / tenPower;
    }

}
