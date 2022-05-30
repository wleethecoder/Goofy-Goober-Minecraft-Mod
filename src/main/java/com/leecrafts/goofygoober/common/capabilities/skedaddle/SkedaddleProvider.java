package com.leecrafts.goofygoober.common.capabilities.skedaddle;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkedaddleProvider implements ICapabilitySerializable<CompoundTag> {

    private final Skedaddle skedaddle = new Skedaddle();
    private final LazyOptional<ISkedaddle> skedaddleLazyOptional = LazyOptional.of(() -> skedaddle);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapabilities.SKEDADDLE_CAPABILITY.orEmpty(cap, skedaddleLazyOptional);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (ModCapabilities.SKEDADDLE_CAPABILITY == null) return nbt;
        nbt.putBoolean("skedaddle_enabled", skedaddle.skedaddleEnabled);
        nbt.putInt("skedaddle_charge_counter", skedaddle.skedaddleChargeCounter);
        nbt.putBoolean("skedaddle_charging", skedaddle.skedaddleCharging);
        nbt.putBoolean("skedaddle_takeoff", skedaddle.skedaddleTakeoff);
        nbt.putBoolean("w_pressed", skedaddle.wPressed);
        nbt.putBoolean("skedaddle_should_animate_on_client", skedaddle.shouldAnimateOnClient);
        nbt.putBoolean("in_water", skedaddle.alreadyInWater);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (ModCapabilities.SKEDADDLE_CAPABILITY != null) {
            skedaddle.skedaddleEnabled = nbt.getBoolean("skedaddle_enabled");
            skedaddle.skedaddleChargeCounter = nbt.getInt("skedaddle_charge_counter");
            skedaddle.skedaddleCharging = nbt.getBoolean("skedaddle_charging");
            skedaddle.skedaddleTakeoff = nbt.getBoolean("skedaddle_takeoff");
            skedaddle.wPressed = nbt.getBoolean("w_pressed");
            skedaddle.shouldAnimateOnClient = nbt.getBoolean("skedaddle_should_animate_on_client");
            skedaddle.alreadyInWater = nbt.getBoolean("in_water");
        }
    }

}
