package com.wenhanlee.goofygoober.events;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.effects.ModEffects;
import com.wenhanlee.goofygoober.packets.PacketHandler;
import com.wenhanlee.goofygoober.packets.mobSize.ServerboundMobSizeUpdatePacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {
    private ForgeClientEvents() {
    }

    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
//        final var player = Minecraft.getInstance().player;
//        if (player != null) {
//            boolean isFat = player.getActiveEffectsMap() != null && player.hasEffect(ModEffects.FAT.get());
//            System.out.println("this client player is fat? " + isFat);
//            PacketHandler.INSTANCE.sendToServer(new ServerboundMobSizeUpdatePacket(isFat));
//        }
    }
}
