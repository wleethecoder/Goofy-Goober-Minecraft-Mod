package com.leecrafts.goofygoober.common.packets.skedaddle;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.Skedaddle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ServerboundSkedaddleTogglePacket {

    public final boolean enabled;

    public ServerboundSkedaddleTogglePacket(boolean enabled) { this.enabled = enabled; }

    public ServerboundSkedaddleTogglePacket(FriendlyByteBuf buffer) { this(buffer.readBoolean()); }

    public void encode(FriendlyByteBuf buffer) { buffer.writeBoolean(this.enabled); }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender != null) {
                sender.getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                    Skedaddle skedaddle = (Skedaddle) iSkedaddle;
                    skedaddle.enabled = this.enabled;
                    if (!skedaddle.enabled) skedaddle.reset(sender);
                });
            }
        });
        ctx.setPacketHandled(true);
    }

}
