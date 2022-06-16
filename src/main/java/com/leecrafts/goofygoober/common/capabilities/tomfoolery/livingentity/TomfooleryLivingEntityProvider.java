package com.leecrafts.goofygoober.common.capabilities.tomfoolery.livingentity;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TomfooleryLivingEntityProvider implements ICapabilitySerializable<CompoundTag> {

    private final TomfooleryLivingEntity tomfooleryLivingEntity = new TomfooleryLivingEntity();
    private final LazyOptional<ITomfooleryLivingEntity> tomfooleryLivingEntityLazyOptional = LazyOptional.of(() -> tomfooleryLivingEntity);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapabilities.TOMFOOLERY_LIVING_ENTITY_CAPABILITY.orEmpty(cap, tomfooleryLivingEntityLazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (ModCapabilities.TOMFOOLERY_LIVING_ENTITY_CAPABILITY == null) return nbt;
        nbt.putInt("num_attackers", tomfooleryLivingEntity.numAttackers);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (ModCapabilities.TOMFOOLERY_LIVING_ENTITY_CAPABILITY != null) {
            tomfooleryLivingEntity.numAttackers = nbt.getInt("num_attackers");
        }
    }

    public void invalidate() { tomfooleryLivingEntityLazyOptional.invalidate(); }

}
