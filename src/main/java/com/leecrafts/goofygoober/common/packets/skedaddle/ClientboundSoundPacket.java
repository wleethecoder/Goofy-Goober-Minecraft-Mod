package com.leecrafts.goofygoober.common.packets.skedaddle;

import com.leecrafts.goofygoober.client.sounds.SoundClientHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

public class ClientboundSoundPacket {

    // playLocalSound(double pX, double pY, double pZ, SoundEvent pSound, SoundSource pCategory, float pVolume, float pPitch, boolean pDistanceDelay)
    public final BlockPos pPos;
//    public final SoundEvent pSound;
    public final String pSound;
    public final float pPitch;

    public ClientboundSoundPacket(BlockPos pPos, String pSound, float pPitch) {
        this.pPos = pPos;
        this.pSound = pSound;
        this.pPitch = pPitch;
    }

    public ClientboundSoundPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos(), buffer.readUtf(), buffer.readFloat());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pPos);
        buffer.writeUtf(this.pSound);
        buffer.writeFloat(this.pPitch);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    SoundClientHelper.handleSoundPacket(this.pPos, this.pSound, this.pPitch));
        });
        ctx.setPacketHandled(true);
    }

}
