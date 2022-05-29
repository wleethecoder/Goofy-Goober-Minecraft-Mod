package com.leecrafts.goofygoober.common.packets.skedaddle;

import com.leecrafts.goofygoober.client.events.ClientRenderEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientboundSkedaddlePacket {

    public final UUID uuid;
    public final boolean shouldAnimateOnClient;

    public ClientboundSkedaddlePacket(UUID uuid, boolean shouldAnimateOnClient) {
        this.uuid = uuid;
        this.shouldAnimateOnClient = shouldAnimateOnClient;
    }

    public ClientboundSkedaddlePacket(FriendlyByteBuf buffer) {
        this(buffer.readUUID(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.uuid);
        buffer.writeBoolean(this.shouldAnimateOnClient);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientRenderEvents.handleSkedaddlePacket(this.uuid, this.shouldAnimateOnClient));
        });
        ctx.get().setPacketHandled(true);
    }

}
