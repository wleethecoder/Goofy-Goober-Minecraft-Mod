package com.leecrafts.goofygoober.common.capabilities.skedaddle;

import net.minecraft.world.entity.player.Player;

public interface ISkedaddle {
    void incrementCounter();
    void reset(Player player);
    void sendPacketToTrackingEntitiesAndSelf(Player player, boolean shouldAnimateOnClient);
}
