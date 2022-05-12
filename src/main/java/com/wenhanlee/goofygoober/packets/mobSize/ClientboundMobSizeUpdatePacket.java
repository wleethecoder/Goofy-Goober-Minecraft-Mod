package com.wenhanlee.goofygoober.packets.mobSize;

import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.capabilities.fat.Fat;
import com.wenhanlee.goofygoober.capabilities.fat.FatProvider;
import com.wenhanlee.goofygoober.packets.ClientAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
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
//    public final boolean isFat;
    private CompoundTag nbt;
    public ClientboundMobSizeUpdatePacket(CompoundTag nbt) {
//        this.isFat = isFat;
        this.nbt = nbt;
    }

    public ClientboundMobSizeUpdatePacket(FriendlyByteBuf buffer) {
        this(buffer.readNbt());
    }

    public void encode(FriendlyByteBuf buffer) {
//        buffer.writeBoolean(this.isFat);
        buffer.writeNbt(this.nbt);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection().getReceptionSide().isClient() && ctx.get().getDirection().getOriginationSide().isServer()) {
//            final ServerPlayer player = ctx.get().getSender();
                final LocalPlayer player = Minecraft.getInstance().player;
//                UUID uuid = player.getUUID();
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
//                        success.set(ClientAccess.setFat(uuid, this.isFat));
                    player.level.getCapability(ModCapabilities.FAT_CAPABILITY).ifPresent(iFat -> {
                        System.out.println("setFat: capability exists");
                        Fat fat = (Fat) iFat;

                        fat.setFat(nbt.getBoolean("fat"));
                    });
                });
                success.set(true);
                System.out.println("clientbound sender is here");
            }
        });
        ctx.get().setPacketHandled(success.get());
        return success.get();
    }
}
