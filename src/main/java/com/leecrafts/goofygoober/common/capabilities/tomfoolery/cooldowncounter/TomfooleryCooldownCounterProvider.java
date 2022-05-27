package com.leecrafts.goofygoober.common.capabilities.tomfoolery.cooldowncounter;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TomfooleryCooldownCounterProvider implements ICapabilitySerializable<CompoundTag> {

    private final TomfooleryCooldownCounter tomfooleryCooldownCounter = new TomfooleryCooldownCounter();
    private final LazyOptional<ITomfooleryCooldownCounter> tomfooleryCooldownCounterLazyOptional = LazyOptional.of(() -> tomfooleryCooldownCounter);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapabilities.TOMFOOLERY_COOLDOWN_COUNTER_CAPABILITY.orEmpty(cap, tomfooleryCooldownCounterLazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (ModCapabilities.TOMFOOLERY_COOLDOWN_COUNTER_CAPABILITY == null) return nbt;
        nbt.putInt("tomfoolery_cooldown_counter", tomfooleryCooldownCounter.counter);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (ModCapabilities.TOMFOOLERY_COOLDOWN_COUNTER_CAPABILITY != null) {
            tomfooleryCooldownCounter.counter = nbt.getInt("tomfoolery_cooldown_counter");
        }
    }

//    public void invalidate() { tomfooleryCooldownCounterLazyOptional.invalidate(); }

}
