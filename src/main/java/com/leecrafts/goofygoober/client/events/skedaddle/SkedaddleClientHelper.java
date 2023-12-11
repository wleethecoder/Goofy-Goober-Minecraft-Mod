package com.leecrafts.goofygoober.client.events.skedaddle;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.Skedaddle;
import com.leecrafts.goofygoober.common.packets.PacketHandler;
import com.leecrafts.goofygoober.common.packets.skedaddle.ServerboundSkedaddleTogglePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public class SkedaddleClientHelper {

    public static void sendServerboundPacket(boolean enabled) {
        SkedaddleClientEvents.skedaddleEnabled = enabled;
//        PacketHandler.INSTANCE.sendToServer(new ServerboundSkedaddleTogglePacket(SkedaddleClientEvents.skedaddleEnabled));
        PacketHandler.INSTANCE.send(new ServerboundSkedaddleTogglePacket(SkedaddleClientEvents.skedaddleEnabled), PacketDistributor.SERVER.noArg());
    }

    public static void handleSkedaddlePacket(UUID uuid, boolean charging, boolean shouldAnimateOnClient) {
        LocalPlayer thisPlayer = Minecraft.getInstance().player;
        if (thisPlayer != null) {
            Player trackedPlayer = thisPlayer.level().getPlayerByUUID(uuid);
            if (trackedPlayer != null) {
                trackedPlayer.getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                    Skedaddle skedaddle = (Skedaddle) iSkedaddle;
                    skedaddle.shouldAnimateOnClient = shouldAnimateOnClient;
                    skedaddle.charging = charging;
                });
            }
        }
    }

}
