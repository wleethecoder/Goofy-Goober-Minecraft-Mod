package com.leecrafts.goofygoober.common.capabilities.tomfoolery.player;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TomfooleryPlayerProvider implements ICapabilitySerializable<CompoundTag> {

    private final TomfooleryPlayer tomfooleryPlayer = new TomfooleryPlayer();
    private final LazyOptional<ITomfooleryPlayer> tomfooleryPlayerLazyOptional = LazyOptional.of(() -> tomfooleryPlayer);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapabilities.TOMFOOLERY_PLAYER_CAPABILITY.orEmpty(cap, tomfooleryPlayerLazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (ModCapabilities.TOMFOOLERY_PLAYER_CAPABILITY == null) return nbt;
        nbt.putInt("tomfoolery_cooldown_counter", tomfooleryPlayer.counter);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (ModCapabilities.TOMFOOLERY_PLAYER_CAPABILITY != null) {
            tomfooleryPlayer.counter = nbt.getInt("tomfoolery_cooldown_counter");
        }
    }

//    public void invalidate() { tomfooleryPlayerLazyOptional.invalidate(); }

}
