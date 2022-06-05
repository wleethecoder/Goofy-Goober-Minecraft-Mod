package com.leecrafts.goofygoober.common.events.skedaddle;

import com.leecrafts.goofygoober.common.capabilities.skedaddle.Skedaddle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class SkedaddleHelper {

    public static void waterCheck(Player player, Skedaddle skedaddle) {
        if (player.isInWaterOrBubble() && !skedaddle.inWater) {
            skedaddle.inWater = true;
            skedaddle.reset(player);
        }
        else if (!player.isInWaterOrBubble() && skedaddle.inWater) {
            skedaddle.inWater = false;
        }
    }

    public static void applySlowness(Player player, Skedaddle skedaddle) {
        if (player.getActiveEffectsMap() != null) {
            if (player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
                skedaddle.previousSlownessInstance = new MobEffectInstance(player.getEffect(MobEffects.MOVEMENT_SLOWDOWN));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, skedaddle.CHARGE_LIMIT, 6, false, false, false));
        }
    }

    public static void applySpeed(Player player, Skedaddle skedaddle) {
        if (player.getActiveEffectsMap() != null) {
            if (player.hasEffect(MobEffects.MOVEMENT_SPEED))
                skedaddle.previousSpeedInstance = new MobEffectInstance(player.getEffect(MobEffects.MOVEMENT_SPEED));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, skedaddle.TAKEOFF_DURATION, 1));
        }
    }

    public static void removeSlowness(Player player, Skedaddle skedaddle) {
        if (player.getActiveEffectsMap() != null) {
            player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            if (skedaddle.previousSlownessInstance != null) player.addEffect(skedaddle.previousSlownessInstance);
        }
    }

    public static void removeSpeed(Player player, Skedaddle skedaddle) {
        if (player.getActiveEffectsMap() != null) {
            player.removeEffect(MobEffects.MOVEMENT_SPEED);
            if (skedaddle.previousSpeedInstance != null) player.addEffect(skedaddle.previousSpeedInstance);
        }
    }

    public static void dustParticles(Player player) {
        ServerLevel serverLevel = (ServerLevel) player.level;
        serverLevel.sendParticles(
                ParticleTypes.CAMPFIRE_COSY_SMOKE,
                player.getX(),
                player.getY() + 0.2,
                player.getZ(),
                2,
                0.3,
                0.1,
                0.3,
                0
        );
        if (player.isOnGround()) {
            BlockPos blockPos = player.blockPosition();
            BlockState blockState = player.level.getBlockState(blockPos);
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, blockState).setPos(blockPos),
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    2,
                    0,
                    0,
                    0,
                    0.15
            );
        }
    }

}
