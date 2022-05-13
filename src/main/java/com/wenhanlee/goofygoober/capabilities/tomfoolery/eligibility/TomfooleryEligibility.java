package com.wenhanlee.goofygoober.capabilities.tomfoolery.eligibility;

public class TomfooleryEligibility implements ITomfooleryEligibility {

    private boolean isEligible;

    public TomfooleryEligibility() { this.isEligible = true; }

    @Override
    public void setEligibility(boolean isEligible) { this.isEligible = isEligible; }

    @Override
    public boolean getEligibility() { return this.isEligible; }

}
