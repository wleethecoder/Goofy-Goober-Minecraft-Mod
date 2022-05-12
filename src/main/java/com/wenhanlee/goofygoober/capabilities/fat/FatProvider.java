package com.wenhanlee.goofygoober.capabilities.fat;

import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FatProvider implements ICapabilitySerializable<CompoundTag> {

    private final Fat fat = new Fat();
    private final LazyOptional<IFat> fatLazyOptional = LazyOptional.of(() -> fat);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        return fatLazyOptional.cast();
        return ModCapabilities.FAT_CAPABILITY.orEmpty(cap, fatLazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        if (ModCapabilities.FAT_CAPABILITY == null) {
            return new CompoundTag();
        }
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("fat", fat.getFat());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (ModCapabilities.FAT_CAPABILITY != null) {
            fat.setFat(nbt.getBoolean("fat"));
        }
    }

    public void invalidate() { fatLazyOptional.invalidate(); }
}
