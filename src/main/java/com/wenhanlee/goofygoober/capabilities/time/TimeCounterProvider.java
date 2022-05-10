package com.wenhanlee.goofygoober.capabilities.time;

import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimeCounterProvider implements ICapabilitySerializable<CompoundTag> {

    private final TimeCounter timeCounter = new TimeCounter();
    private final LazyOptional<ITimeCounter> timeCounterLazyOptional = LazyOptional.of(() -> timeCounter);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        return timeCounterLazyOptional.cast();
        return ModCapabilities.TIME_COUNTER_CAPABILITY.orEmpty(cap, timeCounterLazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        if (ModCapabilities.TIME_COUNTER_CAPABILITY == null) {
            return new CompoundTag();
        }
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("counter", timeCounter.counter);
        nbt.putInt("limit", timeCounter.limit);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (ModCapabilities.TIME_COUNTER_CAPABILITY != null) {
            timeCounter.counter = nbt.getInt("counter");
            timeCounter.limit = nbt.getInt("limit");
        }
    }

    public void invalidate() { timeCounterLazyOptional.invalidate(); }
}
