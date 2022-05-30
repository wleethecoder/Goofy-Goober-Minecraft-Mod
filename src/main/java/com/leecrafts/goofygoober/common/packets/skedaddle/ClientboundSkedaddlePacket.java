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
    public final boolean skedaddleCharging;
    public final boolean shouldAnimateOnClient;

    public ClientboundSkedaddlePacket(UUID uuid, boolean skedaddleCharging, boolean shouldAnimateOnClient) {
        this.uuid = uuid;
        this.skedaddleCharging = skedaddleCharging;
        this.shouldAnimateOnClient = shouldAnimateOnClient;
    }

    public ClientboundSkedaddlePacket(FriendlyByteBuf buffer) {
        this(buffer.readUUID(), buffer.readBoolean(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.uuid);
        buffer.writeBoolean(this.skedaddleCharging);
        buffer.writeBoolean(this.shouldAnimateOnClient);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientRenderEvents.handleSkedaddlePacket(this.uuid, this.skedaddleCharging, this.shouldAnimateOnClient));
        });
        ctx.get().setPacketHandled(true);
    }

}
