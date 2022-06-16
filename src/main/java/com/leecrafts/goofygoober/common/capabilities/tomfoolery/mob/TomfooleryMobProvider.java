package com.leecrafts.goofygoober.common.capabilities.tomfoolery.mob;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TomfooleryMobProvider implements ICapabilitySerializable<CompoundTag> {

    private final TomfooleryMob tomfooleryMob = new TomfooleryMob();
    private final LazyOptional<ITomfooleryMob> tomfooleryMobLazyOptional = LazyOptional.of(() -> tomfooleryMob);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapabilities.TOMFOOLERY_MOB_CAPABILITY.orEmpty(cap, tomfooleryMobLazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (ModCapabilities.TOMFOOLERY_MOB_CAPABILITY == null) return nbt;
        nbt.putBoolean("is_eligible_to_summon_nearby_mobs", tomfooleryMob.isEligibleToSummonNearbyMobs());
        nbt.putBoolean("summoned", tomfooleryMob.isSummoned());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (ModCapabilities.TOMFOOLERY_MOB_CAPABILITY != null) {
            tomfooleryMob.setEligibility(nbt.getBoolean("is_eligible_to_summon_nearby_mobs"));
            tomfooleryMob.setSummoned(nbt.getBoolean("summoned"));
        }
    }

    public void invalidate() { tomfooleryMobLazyOptional.invalidate(); }

}
