package com.wenhanlee.goofygoober.capabilities.fat;

import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.effects.ModEffects;
import com.wenhanlee.goofygoober.packets.PacketHandler;
import com.wenhanlee.goofygoober.packets.mobSize.ClientboundMobSizeUpdatePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class Fat implements IFat {
    private boolean isFat;
    public Fat() {
        this.isFat = false;
    }

    @Override
    public void setFat(boolean isFat) {
        this.isFat = isFat;
    }

    @Override
    public boolean getFat() {
        return this.isFat;
    }

    @Override
    public void sync(Fat fat, ServerPlayer player) {
        fat.setFat(player.getActiveEffectsMap() != null && player.hasEffect(ModEffects.FAT.get()));
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("fat", fat.getFat());
        PacketHandler.INSTANCE.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.noArg(),
                new ClientboundMobSizeUpdatePacket(nbt)
        );
    }
}
