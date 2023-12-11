package com.leecrafts.goofygoober.common.capabilities.skedaddle;

import com.leecrafts.goofygoober.common.events.skedaddle.SkedaddleHelper;
import com.leecrafts.goofygoober.common.packets.PacketHandler;
import com.leecrafts.goofygoober.common.packets.skedaddle.ClientboundSkedaddlePacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class Skedaddle implements ISkedaddle {

    public boolean enabled;

    public int counter;
    public final int CHARGE_LIMIT;
    public final int WHAM_COOLDOWN_LIMIT;
    public final int PLAYER_SNEAK_AMBIENT_DURATION;

    public boolean charging;
    public boolean takeoff;
    public boolean finished;
    public boolean deviousWalk;

    public final int TAKEOFF_DURATION;

    public boolean wPressed;

    public boolean shouldAnimateOnClient;

    public MobEffectInstance previousSlownessInstance;
    public MobEffectInstance previousSpeedInstance;

    // players cannot skedaddle in the water
    public boolean inWater;

    public boolean wham;

    public Skedaddle() {
        this.enabled = false;
        this.counter = 0;
        this.CHARGE_LIMIT = 20;
        this.WHAM_COOLDOWN_LIMIT = 3 * 20;
        this.PLAYER_SNEAK_AMBIENT_DURATION = 9;

        this.charging = false;
        this.takeoff = false;
        this.finished = false;
        this.deviousWalk = false;

        this.TAKEOFF_DURATION = 20 * 20;

        this.wPressed = false;

        this.shouldAnimateOnClient = false;

        this.previousSlownessInstance = null;
        this.previousSpeedInstance = null;

        this.inWater = false;

        this.wham = false;
    }

    public void incrementCounter() {
        this.counter++;
    }

    public void reset(Player player) {
        if (this.charging) SkedaddleHelper.removeSlowness(player, this);
        if (this.takeoff) SkedaddleHelper.removeSpeed(player, this);

        if (!this.wham) this.counter = 0;
        this.takeoff = false;
        this.finished = false;
        this.deviousWalk = false;

        this.previousSlownessInstance = null;
        this.previousSpeedInstance = null;

        this.sendClientboundPacket(player, false, false);
    }

    public void sendClientboundPacket(Player sender, boolean charging, boolean shouldAnimateOnClient) {
        this.charging = charging;
        this.shouldAnimateOnClient = shouldAnimateOnClient;
//        PacketHandler.INSTANCE.send(
//                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender),
//                new ClientboundSkedaddlePacket(sender.getUUID(), charging, shouldAnimateOnClient)
//        );
        PacketHandler.INSTANCE.send(
                new ClientboundSkedaddlePacket(sender.getUUID(), charging, shouldAnimateOnClient),
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(sender)
        );
    }

}
