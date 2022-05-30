package com.leecrafts.goofygoober.common.capabilities.skedaddle;

import com.leecrafts.goofygoober.common.events.skedaddle.SkedaddleHelper;
import com.leecrafts.goofygoober.common.packets.PacketHandler;
import com.leecrafts.goofygoober.common.packets.skedaddle.ClientboundSkedaddlePacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class Skedaddle implements ISkedaddle {

    public boolean skedaddleEnabled;

    public int skedaddleChargeCounter;
    public final int skedaddleChargeLimit;

    public boolean skedaddleCharging;
    public boolean skedaddleTakeoff;

    public final int skedaddleDuration;

    public boolean wPressed;

    public boolean shouldAnimateOnClient;

    public MobEffectInstance previousSlownessInstance;
    public MobEffectInstance previousSpeedInstance;

    // players cannot skedaddle in the water
    public boolean alreadyInWater;

    public Skedaddle() {
        this.skedaddleEnabled = false;
        this.skedaddleChargeCounter = 0;
        this.skedaddleChargeLimit = 20;

        this.skedaddleCharging = false;
        this.skedaddleTakeoff = false;

        this.skedaddleDuration = 20 * 20;

        this.wPressed = false;

        this.shouldAnimateOnClient = false;

        this.previousSlownessInstance = null;
        this.previousSpeedInstance = null;

        this.alreadyInWater = false;
    }

    public void incrementCounter() {
        this.skedaddleChargeCounter++;
    }

    public void reset(Player player) {
        SkedaddleHelper.removeSpeed(player, this);
        this.skedaddleTakeoff = false;
        this.skedaddleChargeCounter = 0;
        this.previousSlownessInstance = null;
        this.previousSpeedInstance = null;
        this.sendClientBoundPacket(player, false, false);
    }

    public void sendClientBoundPacket(Player sender, boolean skedaddleCharging, boolean shouldAnimateOnClient) {
        this.skedaddleCharging = skedaddleCharging;
        this.shouldAnimateOnClient = shouldAnimateOnClient;
        PacketHandler.INSTANCE.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender),
                new ClientboundSkedaddlePacket(sender.getUUID(), skedaddleCharging, shouldAnimateOnClient)
        );
    }

}
