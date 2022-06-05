package com.leecrafts.goofygoober.common.effects;

import com.leecrafts.goofygoober.GoofyGoober;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, GoofyGoober.MOD_ID);

    public static final RegistryObject<MobEffect> FAT = EFFECTS.register("fat",
            () -> new FatEffect(MobEffectCategory.HARMFUL, 0xffc107));

    public static final RegistryObject<MobEffect> SQUASHED = EFFECTS.register("squashed",
            () -> new SquashedEffect(MobEffectCategory.HARMFUL, 0x404040));

    public static final RegistryObject<MobEffect> CRASHED = EFFECTS.register("crashed",
            () -> new CrashedEffect(MobEffectCategory.HARMFUL, 0xd4d2bc));

    public static final RegistryObject<MobEffect> SMASHED = EFFECTS.register("smashed",
            () -> new SmashedEffect(MobEffectCategory.HARMFUL, 0xd4d2bc));

    public static final RegistryObject<MobEffect> HALLUCINATING = EFFECTS.register("hallucinating",
            () -> new HallucinatingEffect(MobEffectCategory.HARMFUL, 0xbb545c));

    public static void register(IEventBus eventBus) { EFFECTS.register(eventBus); }

}
