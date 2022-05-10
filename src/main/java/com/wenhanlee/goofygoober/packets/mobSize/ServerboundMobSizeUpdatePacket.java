package com.wenhanlee.goofygoober.packets.mobSize;

import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.capabilities.fat.Fat;
import com.wenhanlee.goofygoober.effects.ModEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ServerboundMobSizeUpdatePacket {
    public final boolean isFat;
    public ServerboundMobSizeUpdatePacket(boolean isFat) {
        this.isFat = isFat;
    }

    public ServerboundMobSizeUpdatePacket(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.isFat);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            final ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(ModCapabilities.FAT_CAPABILITY).ifPresent(iFat -> {
                    Fat fat = (Fat) iFat;
//                fat.setFat(player.getActiveEffectsMap() != null && player.hasEffect(ModEffects.FAT.get()));
                    fat.setFat(this.isFat);
                    success.set(true);
//                    System.out.println("serverbound player is here");
                });
            }
            else {
                success.set(false);
//                System.out.println("serverbound player is null");
            }
        });
        ctx.get().setPacketHandled(success.get());
        return success.get();
    }
}
