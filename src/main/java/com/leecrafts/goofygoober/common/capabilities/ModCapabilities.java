package com.leecrafts.goofygoober.common.capabilities;

import com.leecrafts.goofygoober.common.capabilities.ambient.IAmbientCounter;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.ISkedaddle;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.livingentity.ITomfooleryLivingEntity;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.player.ITomfooleryPlayer;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.mob.ITomfooleryMob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities {

    public static final Capability<IAmbientCounter> AMBIENT_COUNTER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<ITomfooleryPlayer> TOMFOOLERY_PLAYER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<ITomfooleryMob> TOMFOOLERY_MOB_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<ITomfooleryLivingEntity> TOMFOOLERY_LIVING_ENTITY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static final Capability<ISkedaddle> SKEDADDLE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

}
