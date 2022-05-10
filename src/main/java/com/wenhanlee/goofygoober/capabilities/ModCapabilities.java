package com.wenhanlee.goofygoober.capabilities;

import com.wenhanlee.goofygoober.capabilities.fat.IFat;
import com.wenhanlee.goofygoober.capabilities.time.ITimeCounter;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities {

    public static Capability<ITimeCounter> TIME_COUNTER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static Capability<IFat> FAT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

}
