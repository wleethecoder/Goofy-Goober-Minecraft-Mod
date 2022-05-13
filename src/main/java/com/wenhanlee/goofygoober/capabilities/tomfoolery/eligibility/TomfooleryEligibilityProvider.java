package com.wenhanlee.goofygoober.capabilities.tomfoolery.eligibility;

import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TomfooleryEligibilityProvider implements ICapabilitySerializable<CompoundTag> {

    private final TomfooleryEligibility tomfooleryEligibility = new TomfooleryEligibility();
    private final LazyOptional<ITomfooleryEligibility> tomfooleryEligibilityLazyOptional = LazyOptional.of(() -> tomfooleryEligibility);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapabilities.TOMFOOLERY_ELIGIBILITY_CAPABILITY.orEmpty(cap, tomfooleryEligibilityLazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        if (ModCapabilities.TOMFOOLERY_ELIGIBILITY_CAPABILITY == null) {
            return new CompoundTag();
        }
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("is_eligible_to_cause_tomfoolery", tomfooleryEligibility.getEligibility());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (ModCapabilities.TOMFOOLERY_ELIGIBILITY_CAPABILITY != null) {
            tomfooleryEligibility.setEligibility(nbt.getBoolean("is_eligible_to_cause_tomfoolery"));
        }
    }

    public void invalidate() { tomfooleryEligibilityLazyOptional.invalidate(); }

}
