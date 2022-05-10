package com.wenhanlee.goofygoober.packets;

import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.capabilities.fat.Fat;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

@SuppressWarnings("resource")
public class ClientAccess {

    public static boolean setFat(UUID uuid, boolean isFat) {
        final Entity entity = Minecraft.getInstance().level.getPlayerByUUID(uuid);
        if (entity instanceof Player player) {
            player.getCapability(ModCapabilities.FAT_CAPABILITY).ifPresent(iFat -> {
                System.out.println("setFat: capability exists");
                Fat fat = (Fat) iFat;
                fat.setFat(isFat);
            });
            System.out.println("setFat successful");
            return true;
        }
        System.out.println("setFat unsuccessful.");
        return false;
    }

}
