package com.leecrafts.goofygoober.common.packets.skedaddle;

import com.leecrafts.goofygoober.client.events.skedaddle.SkedaddleClientHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.UUID;

public class ClientboundSkedaddlePacket {

    public final UUID uuid;
    public final boolean charging;
    public final boolean shouldAnimateOnClient;

    public ClientboundSkedaddlePacket(UUID uuid, boolean charging, boolean shouldAnimateOnClient) {
        this.uuid = uuid;
        this.charging = charging;
        this.shouldAnimateOnClient = shouldAnimateOnClient;
    }

    public ClientboundSkedaddlePacket(FriendlyByteBuf buffer) {
        this(buffer.readUUID(), buffer.readBoolean(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.uuid);
        buffer.writeBoolean(this.charging);
        buffer.writeBoolean(this.shouldAnimateOnClient);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    SkedaddleClientHelper.handleSkedaddlePacket(this.uuid, this.charging, this.shouldAnimateOnClient));
        });
        ctx.setPacketHandled(true);
    }

}
