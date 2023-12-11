package com.leecrafts.goofygoober.common.misc;

import com.leecrafts.goofygoober.common.packets.PacketHandler;
import com.leecrafts.goofygoober.common.packets.skedaddle.ClientboundSoundPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;

import java.util.Random;

// Useful, frequently-used helper functions
public class Utilities {

    public static final Random random = new Random();
    public static final float DEFAULT_PITCH_LOW = 0.9F;
    public static final float DEFAULT_PITCH_HIGH = 1.1F;

    private static void playLocalSound(Entity entity, String soundEvent, float pitchLow, float pitchHigh) {
        // this would be reaching across logical sides
//        if (Minecraft.getInstance().level() != null) {
//            Minecraft.getInstance().level().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), soundEvent, soundSource, volume, pitchLow + random.nextFloat(pitchHigh - pitchLow), false);
//        }
//        PacketHandler.INSTANCE.send(
//                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
//                new ClientboundSoundPacket(entity.blockPosition(), soundEvent, pitchLow + random.nextFloat(pitchHigh - pitchLow))
//        );
        PacketHandler.INSTANCE.send(
                new ClientboundSoundPacket(entity.blockPosition(), soundEvent, pitchLow + random.nextFloat(pitchHigh - pitchLow)),
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity)
        );
    }

    private static void playLocalSound(Entity entity, String soundEvent, float pitch) {
//        PacketHandler.INSTANCE.send(
//                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
//                new ClientboundSoundPacket(entity.blockPosition(), soundEvent, pitch)
//        );
        PacketHandler.INSTANCE.send(
                new ClientboundSoundPacket(entity.blockPosition(), soundEvent, pitch),
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity)
        );
    }

    public static void playSound(Entity entity, String soundEvent, float pitchLow, float pitchHigh) {
        playLocalSound(entity, soundEvent, pitchLow, pitchHigh);
    }

    public static void playSound(Entity entity, String soundEvent, float pitch) {
        playLocalSound(entity, soundEvent, pitch);
    }

    public static void playSound(Entity entity, String soundEvent) {
        playLocalSound(entity, soundEvent, DEFAULT_PITCH_LOW, DEFAULT_PITCH_HIGH);
    }

}
