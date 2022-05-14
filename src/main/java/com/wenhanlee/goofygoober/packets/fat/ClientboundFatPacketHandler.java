package com.wenhanlee.goofygoober.packets.fat;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.effects.ModEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, value = Dist.CLIENT)
public class ClientboundFatPacketHandler {

    static HashMap<UUID, Boolean> trackedPlayersFatEffects;

    public static void handlePacket(UUID uuid, boolean isFat) {
        initializeIfNull();
        System.out.println("uuid: " + uuid);
        System.out.println("isFat: " + isFat);

        trackedPlayersFatEffects.put(uuid, isFat);
    }

    @SubscribeEvent
    public static void fat(RenderPlayerEvent.Pre event) {
        Player player = event.getPlayer();
        if (player != null) {
//            boolean isFat = player.getActiveEffectsMap() != null && player.hasEffect(ModEffects.FAT.get());
            System.out.println(trackedPlayersFatEffects == null);
            if (trackedPlayersFatEffects != null) System.out.println(trackedPlayersFatEffects.get(player.getUUID()) == null);
            initializeIfNull();
            boolean isFat = trackedPlayersFatEffects.getOrDefault(player.getUUID(), false);
            isFat = isFat || (player.getActiveEffectsMap() != null && player.hasEffect(ModEffects.FAT.get()));
            System.out.println("[CLIENT] " + player.getDisplayName().getString() + " (" + player.getStringUUID() + ")" + " is fat: " + isFat);
            if (isFat) event.getPoseStack().scale(3.0F, 1.0F, 3.0F);
        }
    }

    private static void initializeIfNull() {
        if (trackedPlayersFatEffects == null) trackedPlayersFatEffects = new HashMap<>();
    }

}
