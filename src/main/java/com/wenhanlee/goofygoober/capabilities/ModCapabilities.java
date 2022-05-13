package com.wenhanlee.goofygoober.capabilities;

import com.wenhanlee.goofygoober.capabilities.ambient.IAmbientCounter;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.counter.ITomfooleryCounter;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.eligibility.ITomfooleryEligibility;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities {

    public static Capability<IAmbientCounter> AMBIENT_COUNTER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static Capability<ITomfooleryCounter> TOMFOOLERY_COUNTER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static Capability<ITomfooleryEligibility> TOMFOOLERY_ELIGIBILITY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

}
