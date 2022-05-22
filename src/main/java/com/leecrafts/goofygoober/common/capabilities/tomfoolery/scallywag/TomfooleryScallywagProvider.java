package com.leecrafts.goofygoober.common.capabilities.tomfoolery.scallywag;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TomfooleryScallywagProvider implements ICapabilitySerializable<CompoundTag> {

    private final TomfooleryScallywag tomfooleryScallywag = new TomfooleryScallywag();
    private final LazyOptional<ITomfooleryScallywag> tomfooleryScallywagLazyOptional = LazyOptional.of(() -> tomfooleryScallywag);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapabilities.TOMFOOLERY_SCALLYWAG_CAPABILITY.orEmpty(cap, tomfooleryScallywagLazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        if (ModCapabilities.TOMFOOLERY_SCALLYWAG_CAPABILITY == null) {
            return new CompoundTag();
        }
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("is_eligible_to_cause_tomfoolery", tomfooleryScallywag.isEligible());
        nbt.putBoolean("scallywag", tomfooleryScallywag.isScallywag());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (ModCapabilities.TOMFOOLERY_SCALLYWAG_CAPABILITY != null) {
            tomfooleryScallywag.setEligibility(nbt.getBoolean("is_eligible_to_cause_tomfoolery"));
            tomfooleryScallywag.setScallywag(nbt.getBoolean("scallywag"));
        }
    }

    public void invalidate() { tomfooleryScallywagLazyOptional.invalidate(); }

}
