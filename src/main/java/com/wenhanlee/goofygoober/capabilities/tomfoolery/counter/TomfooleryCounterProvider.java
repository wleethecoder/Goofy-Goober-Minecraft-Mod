package com.wenhanlee.goofygoober.capabilities.tomfoolery.counter;

import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TomfooleryCounterProvider implements ICapabilitySerializable<CompoundTag> {

    private final TomfooleryCounter tomfooleryCounter = new TomfooleryCounter();
    private final LazyOptional<ITomfooleryCounter> tomfooleryCounterLazyOptional = LazyOptional.of(() -> tomfooleryCounter);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapabilities.TOMFOOLERY_COUNTER_CAPABILITY.orEmpty(cap, tomfooleryCounterLazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        if (ModCapabilities.TOMFOOLERY_COUNTER_CAPABILITY == null) {
            return new CompoundTag();
        }
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("tomfoolery_counter", tomfooleryCounter.counter);
        nbt.putBoolean("tomfoolery_cooldown", tomfooleryCounter.cooldown);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (ModCapabilities.TOMFOOLERY_COUNTER_CAPABILITY != null) {
            tomfooleryCounter.counter = nbt.getInt("tomfoolery_counter");
            tomfooleryCounter.cooldown = nbt.getBoolean("tomfoolery_cooldown");
        }
    }

    public void invalidate() { tomfooleryCounterLazyOptional.invalidate(); }

}
