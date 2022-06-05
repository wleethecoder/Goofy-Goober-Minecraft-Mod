package com.leecrafts.goofygoober.common.packets.skedaddle;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.Skedaddle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSkedaddleBumpPacket {

    public final boolean bump;

    public ServerboundSkedaddleBumpPacket(boolean bump) { this.bump = bump; }

    public ServerboundSkedaddleBumpPacket(FriendlyByteBuf buffer) { this(buffer.readBoolean()); }

    public void encode(FriendlyByteBuf buffer) { buffer.writeBoolean(this.bump); }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                sender.getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                    Skedaddle skedaddle = (Skedaddle) iSkedaddle;
//                    skedaddle.wham = skedaddle.takeoff && this.bump;
                    // when player runs into a wall while skedaddling, it takes damage
                    // it cannot skedaddle for 3 seconds
                    if (this.bump && skedaddle.takeoff) {
                        skedaddle.wham = true;
                        skedaddle.reset(sender);
                        sender.hurt(DamageSource.FLY_INTO_WALL, 6);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}