package com.leecrafts.goofygoober.common.capabilities;

import com.leecrafts.goofygoober.common.capabilities.ambient.IAmbientCounter;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.ISkedaddle;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.cooldowncounter.ITomfooleryCooldownCounter;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.scallywag.ITomfooleryScallywag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities {

    public static final Capability<IAmbientCounter> AMBIENT_COUNTER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<ITomfooleryCooldownCounter> TOMFOOLERY_COOLDOWN_COUNTER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<ITomfooleryScallywag> TOMFOOLERY_SCALLYWAG_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<ISkedaddle> SKEDADDLE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

}
