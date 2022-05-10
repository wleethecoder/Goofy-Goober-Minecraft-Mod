package com.wenhanlee.goofygoober.effects;

import com.wenhanlee.goofygoober.GoofyGoober;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, GoofyGoober.MOD_ID);

    public static final RegistryObject<MobEffect> FAT = EFFECTS.register("fat",
            () -> new FatEffect(MobEffectCategory.HARMFUL, 0xfff54));

    public static void register(IEventBus eventBus) { EFFECTS.register(eventBus); }
}
