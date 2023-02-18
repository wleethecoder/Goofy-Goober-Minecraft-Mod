package com.leecrafts.goofygoober.client.sounds;

import com.leecrafts.goofygoober.common.sounds.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;

public class SoundClientHelper {

    private static HashMap<String, SoundEvent> soundEvents;

    private static void initializeIfNull() {
        if (soundEvents == null) {
            soundEvents = new HashMap<>();
            soundEvents.put("scream", ModSounds.SCREAM.get());
            soundEvents.put("mob_skedaddle", ModSounds.SKEDADDLE.get());
            soundEvents.put("player_skedaddle", ModSounds.PLAYER_SKEDADDLE.get());
            soundEvents.put("player_sneak", ModSounds.PLAYER_SNEAK.get());
            soundEvents.put("player_takeoff", ModSounds.PLAYER_TAKEOFF.get());
            soundEvents.put("snore_loud", ModSounds.SNORE_LOUD.get());
            soundEvents.put("snore_mimimi", ModSounds.SNORE_MIMIMI.get());
            soundEvents.put("snore_whistle", ModSounds.SNORE_WHISTLE.get());
            soundEvents.put("player_gorge", ModSounds.PLAYER_GORGE.get());
            soundEvents.put("tomfoolery", ModSounds.TOMFOOLERY.get());
            soundEvents.put("fail", ModSounds.FAIL.get());
            soundEvents.put("impact", ModSounds.IMPACT.get());
            soundEvents.put("doit", ModSounds.DOIT.get());
            soundEvents.put("teeth_chatter", ModSounds.TEETH_CHATTER.get());
        }
    }

    public static void handleSoundPacket(BlockPos pPos, String pSound, float pPitch) {
        ClientLevel clientLevel = Minecraft.getInstance().level;
        if (clientLevel != null) {
            initializeIfNull();
            SoundEvent soundEvent = soundEvents.get(pSound);
            clientLevel.playLocalSound(pPos, soundEvent, SoundSource.NEUTRAL, 1, pPitch, false);
        }
    }

}
