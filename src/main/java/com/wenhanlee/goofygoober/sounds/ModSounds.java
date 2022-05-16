package com.wenhanlee.goofygoober.sounds;

import com.wenhanlee.goofygoober.GoofyGoober;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GoofyGoober.MOD_ID);

    public static final RegistryObject<SoundEvent> SCREAM =
            registerSoundEvent("scream");

    public static final RegistryObject<SoundEvent> SKEDADDLE =
            registerSoundEvent("skedaddle");

    public static final RegistryObject<SoundEvent> SNORE_LOUD =
            registerSoundEvent("snore_loud");

    public static final RegistryObject<SoundEvent> SNORE_MIMIMI =
            registerSoundEvent("snore_mimimi");

    public static final RegistryObject<SoundEvent> SNORE_WHISTLE =
            registerSoundEvent("snore_whistle");

    public static final RegistryObject<SoundEvent> PLAYER_GORGE =
            registerSoundEvent("player_gorge");

    public static final RegistryObject<SoundEvent> TOMFOOLERY =
            registerSoundEvent("tomfoolery");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(GoofyGoober.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }

}
