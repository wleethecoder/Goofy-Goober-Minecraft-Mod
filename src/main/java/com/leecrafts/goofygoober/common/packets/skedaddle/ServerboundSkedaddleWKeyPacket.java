package com.leecrafts.goofygoober.common.packets.skedaddle;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.Skedaddle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSkedaddleWKeyPacket {

    public final boolean wPressed;

    public ServerboundSkedaddleWKeyPacket(boolean wPressed) { this.wPressed = wPressed; }

    public ServerboundSkedaddleWKeyPacket(FriendlyByteBuf buffer) { this(buffer.readBoolean()); }

    public void encode(FriendlyByteBuf buffer) { buffer.writeBoolean(this.wPressed); }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                sender.getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                    Skedaddle skedaddle = (Skedaddle) iSkedaddle;
                    skedaddle.wPressed = this.wPressed;
                    if (!skedaddle.wPressed) skedaddle.reset(sender);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
