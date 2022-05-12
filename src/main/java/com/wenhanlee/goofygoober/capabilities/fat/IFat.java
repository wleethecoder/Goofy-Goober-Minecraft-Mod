package com.wenhanlee.goofygoober.capabilities.fat;

import net.minecraft.server.level.ServerPlayer;

public interface IFat {
    void setFat(boolean isFat);
    boolean getFat();
    void sync(Fat fat, ServerPlayer player);
}
