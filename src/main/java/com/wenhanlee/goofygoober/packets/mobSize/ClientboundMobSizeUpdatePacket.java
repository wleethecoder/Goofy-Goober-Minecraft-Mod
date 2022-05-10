package com.wenhanlee.goofygoober.packets.mobSize;

import com.wenhanlee.goofygoober.packets.ClientAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ClientboundMobSizeUpdatePacket {
    public final boolean isFat;
    public ClientboundMobSizeUpdatePacket(boolean isFat) {
        this.isFat = isFat;
    }

    public ClientboundMobSizeUpdatePacket(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.isFat);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                UUID uuid = player.getUUID();
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> success.set(ClientAccess.setFat(uuid, this.isFat)));
                System.out.println("clientbound sender is here");
            }
            else {
                System.out.println("clientbound sender is null");
            }
        });
        ctx.get().setPacketHandled(success.get());
        return success.get();
    }
}
