package com.wenhanlee.goofygoober.capabilities.fat;

public class Fat implements IFat {
    private boolean isFat;
    public Fat() {
        this.isFat = false;
    }

    @Override
    public void setFat(boolean isFat) {
        this.isFat = isFat;
    }

    @Override
    public boolean getFat() {
        return this.isFat;
    }
}
