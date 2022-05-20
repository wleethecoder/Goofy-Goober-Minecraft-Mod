package com.wenhanlee.goofygoober.common.capabilities.ambient;

import com.wenhanlee.goofygoober.common.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AmbientCounterProvider implements ICapabilitySerializable<CompoundTag> {

    private final AmbientCounter ambientCounter = new AmbientCounter();
    private final LazyOptional<IAmbientCounter> ambientCounterLazyOptional = LazyOptional.of(() -> ambientCounter);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        return ambientCounterLazyOptional.cast();
        return ModCapabilities.AMBIENT_COUNTER_CAPABILITY.orEmpty(cap, ambientCounterLazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        if (ModCapabilities.AMBIENT_COUNTER_CAPABILITY == null) {
            return new CompoundTag();
        }
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("ambient_counter", ambientCounter.counter);
        nbt.putInt("ambient_counter_limit", ambientCounter.limit);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (ModCapabilities.AMBIENT_COUNTER_CAPABILITY != null) {
            ambientCounter.counter = nbt.getInt("ambient_counter");
            ambientCounter.limit = nbt.getInt("ambient_counter_limit");
        }
    }

    public void invalidate() { ambientCounterLazyOptional.invalidate(); }

}
