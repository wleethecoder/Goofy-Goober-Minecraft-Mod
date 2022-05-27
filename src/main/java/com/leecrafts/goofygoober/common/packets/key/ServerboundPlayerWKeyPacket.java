package com.leecrafts.goofygoober.common.packets.key;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.Skedaddle;
import com.leecrafts.goofygoober.common.sounds.ModSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class ServerboundPlayerWKeyPacket {

    public final boolean wPressed;

    public ServerboundPlayerWKeyPacket(boolean wPressed) {
        this.wPressed = wPressed;
    }

    public ServerboundPlayerWKeyPacket(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.wPressed);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                sender.getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                    Skedaddle skedaddle = (Skedaddle) iSkedaddle;
                    skedaddle.wPressed = this.wPressed;
                    if (!skedaddle.wPressed) {
                        if (sender.getActiveEffectsMap() != null) sender.removeEffect(MobEffects.MOVEMENT_SPEED);
                        skedaddle.reset();
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
