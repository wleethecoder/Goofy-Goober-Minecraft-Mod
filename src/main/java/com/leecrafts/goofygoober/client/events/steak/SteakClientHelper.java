package com.leecrafts.goofygoober.client.events.steak;

import com.leecrafts.goofygoober.common.entities.ModEntities;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;

public class SteakClientHelper {

    public static void refreshSteakEntity(LocalPlayer localPlayer, LivingEntity livingEntity) {
        if (SteakClientEvents.steakEntity == null || SteakClientEvents.steakEntity.level() != livingEntity.level()) {
            SteakClientEvents.steakEntity = ModEntities.STEAK_ENTITY.get().create(localPlayer.clientLevel);
        }
    }

}
