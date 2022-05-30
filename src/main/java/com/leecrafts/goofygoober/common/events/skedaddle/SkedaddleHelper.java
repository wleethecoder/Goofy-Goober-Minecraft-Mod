package com.leecrafts.goofygoober.common.events.skedaddle;

import com.leecrafts.goofygoober.common.capabilities.skedaddle.Skedaddle;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class SkedaddleHelper {

    public static void waterCheck(Player player, Skedaddle skedaddle) {
        if (player.isInWaterOrBubble() && !skedaddle.alreadyInWater) {
            skedaddle.alreadyInWater = true;
            skedaddle.reset(player);
        }
        else if (!player.isInWaterOrBubble() && skedaddle.alreadyInWater) {
            skedaddle.alreadyInWater = false;
        }
    }

    public static void applySlowness(Player player, Skedaddle skedaddle) {
        if (player.getActiveEffectsMap() != null) {
            if (player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) skedaddle.previousSlownessInstance = new MobEffectInstance(player.getEffect(MobEffects.MOVEMENT_SLOWDOWN));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, skedaddle.skedaddleChargeLimit, 6, false, false, false));
        }
    }

    public static void applySpeed(Player player, Skedaddle skedaddle) {
        if (player.getActiveEffectsMap() != null) {
            if (player.hasEffect(MobEffects.MOVEMENT_SPEED)) skedaddle.previousSpeedInstance = new MobEffectInstance(player.getEffect(MobEffects.MOVEMENT_SPEED));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, skedaddle.skedaddleDuration, 1));
        }
    }

    public static void removeSlowness(Player player, Skedaddle skedaddle) {
        if (player.getActiveEffectsMap() != null) {
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            if (skedaddle.previousSlownessInstance != null) player.addEffect(skedaddle.previousSlownessInstance);
        }
    }

    public static void removeSpeed(Player player, Skedaddle skedaddle) {
        if (player.getActiveEffectsMap() != null && skedaddle.skedaddleTakeoff) {
            player.removeEffect(MobEffects.MOVEMENT_SPEED);
            if (skedaddle.previousSpeedInstance != null) player.addEffect(skedaddle.previousSpeedInstance);
        }
    }

}
