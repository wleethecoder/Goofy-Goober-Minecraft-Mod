package com.wenhanlee.goofygoober.events;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.capabilities.fat.Fat;
import com.wenhanlee.goofygoober.effects.ModEffects;
import com.wenhanlee.goofygoober.packets.PacketHandler;
import com.wenhanlee.goofygoober.packets.mobSize.ClientboundMobSizeUpdatePacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
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
        System.out.println("console: startTracking");
        Player player = (Player) event.getTarget();
        ServerPlayer target = (ServerPlayer) event.getPlayer();
        if (player != null && !player.level.isClientSide()) {
            System.out.println("startTracking: not null");
            player.getCapability(ModCapabilities.FAT_CAPABILITY).ifPresent(iFat -> {
                Fat fat = (Fat) iFat;
                fat.setFat(player.getActiveEffectsMap() != null && player.hasEffect(ModEffects.FAT.get()));
                CompoundTag nbt = new CompoundTag();
                nbt.putBoolean("fat", fat.getFat());
                PacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> target),
                        new ClientboundMobSizeUpdatePacket(nbt)
                );
                System.out.println("startTracking: sent");
            });
        }
    }

    @SubscribeEvent
    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        System.out.println("console: playerLoggedIn");
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        if (player != null && !player.level.isClientSide()) {
            System.out.println("playerLoggedIn: not null");
            player.getCapability(ModCapabilities.FAT_CAPABILITY).ifPresent(iFat -> {
                Fat fat = (Fat) iFat;
                iFat.sync(fat, player);
                System.out.println("playerLoggedIn: sent");
            });
        }
    }

    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        System.out.println("console: playerRespawn");
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        if (player != null && !player.level.isClientSide()) {
            System.out.println("playerRespawn: not null");
            player.getCapability(ModCapabilities.FAT_CAPABILITY).ifPresent(iFat -> {
                Fat fat = (Fat) iFat;
                iFat.sync(fat, player);
                System.out.println("playerRespawn: sent");
            });
        }
    }

    @SubscribeEvent
    public static void playerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        System.out.println("console: playerChangedDimension");
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        if (!player.level.isClientSide()) {
            System.out.println("playerChangedDimension: not null");
            player.getCapability(ModCapabilities.FAT_CAPABILITY).ifPresent(iFat -> {
                Fat fat = (Fat) iFat;
                iFat.sync(fat, player);
                System.out.println("playerChangedDimension: sent");
            });
        }
    }

}
