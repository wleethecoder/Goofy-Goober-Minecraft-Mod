package com.wenhanlee.goofygoober.events;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.effects.ModEffects;
import com.wenhanlee.goofygoober.packets.PacketHandler;
import com.wenhanlee.goofygoober.packets.fat.ClientboundFatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class ForgeCommonEvents {

    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking event) {

        // the (player) entity whose fat status is being tracked
        Entity entity = event.getTarget();

        // the player tracking the player's fat status will be sent packets
        ServerPlayer target = (ServerPlayer) event.getPlayer();
        if (entity instanceof Player player && !player.level.isClientSide()) {
            boolean isFat = player.getActiveEffectsMap() != null && player.hasEffect(ModEffects.FAT.get());
//            System.out.println("[FORGE COMMON] " + player.getDisplayName().getString() + " will tell " + target.getDisplayName().getString() + " that their fat status is " + isFat);
            PacketHandler.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> target),
                    new ClientboundFatPacket(player.getUUID(), isFat)
            );
        }
    }

}
