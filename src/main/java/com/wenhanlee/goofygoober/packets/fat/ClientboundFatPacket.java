package com.wenhanlee.goofygoober.packets.fat;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientboundFatPacket {

    private final UUID uuid;
    private final boolean isFat;

    public ClientboundFatPacket(UUID uuid, boolean isFat) {
        this.uuid = uuid;
        this.isFat = isFat;
    }

    public ClientboundFatPacket(FriendlyByteBuf buffer) {
        this(buffer.readUUID(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeBoolean(isFat);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientboundFatPacketHandler.handlePacket(this.uuid, this.isFat));
        });
        ctx.get().setPacketHandled(true);
    }

}
