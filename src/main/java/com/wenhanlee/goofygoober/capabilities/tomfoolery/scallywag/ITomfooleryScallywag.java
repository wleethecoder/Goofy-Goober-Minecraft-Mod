package com.wenhanlee.goofygoober.capabilities.tomfoolery.scallywag;

public interface ITomfooleryScallywag {
    void setEligibility(boolean isEligible);
    void setScallywag(boolean isScallywag);
    boolean isEligible();
    boolean isScallywag();

    void incrementCounter();
    void resetCounter();
    void rollLimit();
}
