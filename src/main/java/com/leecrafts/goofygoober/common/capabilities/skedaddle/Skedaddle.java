package com.leecrafts.goofygoober.common.capabilities.skedaddle;

import com.leecrafts.goofygoober.common.packets.PacketHandler;
import com.leecrafts.goofygoober.common.packets.skedaddle.ClientboundSkedaddlePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class Skedaddle implements ISkedaddle {

    public int skedaddleChargeCounter;
    public final int skedaddleChargeLimit;

    public boolean skedaddleCharging;
    public boolean skedaddleTakeoff;

    public final int skedaddleDuration;

    public boolean wPressed;

    public boolean shouldAnimateOnClient;

    public Skedaddle() {
        this.skedaddleChargeCounter = 0;
        this.skedaddleChargeLimit = 20;

        this.skedaddleCharging = false;
        this.skedaddleTakeoff = false;

        this.skedaddleDuration = 20 * 20;

        this.wPressed = false;

        this.shouldAnimateOnClient = false;
    }

    public void incrementCounter() {
        this.skedaddleChargeCounter++;
    }

    public void reset(Player player) {
        this.skedaddleTakeoff = false;
        this.skedaddleCharging = false;
        this.skedaddleChargeCounter = 0;
        this.sendPacketToTrackingEntitiesAndSelf(player, false);
    }

    public void sendPacketToTrackingEntitiesAndSelf(Player sender, boolean shouldAnimateOnClient) {
        this.shouldAnimateOnClient = shouldAnimateOnClient;
        PacketHandler.INSTANCE.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> sender),
                new ClientboundSkedaddlePacket(sender.getUUID(), shouldAnimateOnClient)
        );
    }

}
