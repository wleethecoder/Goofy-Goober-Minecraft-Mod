package com.leecrafts.goofygoober.common.packets.skedaddle;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.Skedaddle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ServerboundSkedaddleBumpPacket {

    public final boolean bump;

    public ServerboundSkedaddleBumpPacket(boolean bump) { this.bump = bump; }

    public ServerboundSkedaddleBumpPacket(FriendlyByteBuf buffer) { this(buffer.readBoolean()); }

    public void encode(FriendlyByteBuf buffer) { buffer.writeBoolean(this.bump); }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender != null) {
                sender.getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                    Skedaddle skedaddle = (Skedaddle) iSkedaddle;
//                    skedaddle.wham = skedaddle.takeoff && this.bump;
                    // when player runs into a wall while skedaddling, it takes damage
                    // it cannot skedaddle for 3 seconds
                    if (this.bump && skedaddle.takeoff) {
                        skedaddle.reset(sender);
                        skedaddle.wham = true;
                        sender.hurt(sender.damageSources().flyIntoWall(), 6);
                    }
                });
            }
        });
        ctx.setPacketHandled(true);
    }

}