package com.leecrafts.goofygoober.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class HallucinatingEffect extends MobEffect {

    public HallucinatingEffect(MobEffectCategory mobEffectCategory, int color) { super(mobEffectCategory, color); }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) { return true; }

}
