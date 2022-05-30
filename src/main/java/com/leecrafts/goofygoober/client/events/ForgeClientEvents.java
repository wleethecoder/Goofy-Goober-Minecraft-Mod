package com.leecrafts.goofygoober.client.events;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.client.keys.KeyInit;
import com.leecrafts.goofygoober.common.packets.PacketHandler;
import com.leecrafts.goofygoober.common.packets.skedaddle.ServerboundSkedaddleTogglePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {

    private static boolean skedaddleEnabled;
    private static boolean skedaddleToggleKeyAlreadyPressed = false;

    private static void sendServerboundPacket(boolean enabled) {
        skedaddleEnabled = enabled;
        PacketHandler.INSTANCE.sendToServer(new ServerboundSkedaddleTogglePacket(skedaddleEnabled));
    }

    @SubscribeEvent
    public static void initializeSkedaddleDisabledOnLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
        sendServerboundPacket(false);
    }

    @SubscribeEvent
    public static void initializeSkedaddleDisabledOnRespawn(ClientPlayerNetworkEvent.RespawnEvent event) {
        sendServerboundPacket(false);
    }

    @SubscribeEvent
    public static void toggleSkedaddle(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer != null) {
                boolean pressed = KeyInit.toggleSkedaddleKeyMapping.isDown();
                if (pressed && !skedaddleToggleKeyAlreadyPressed) {
                    skedaddleToggleKeyAlreadyPressed = true;
                    sendServerboundPacket(!skedaddleEnabled);
                    String message = skedaddleEnabled ? "Skedaddle enabled" : "Skedaddle disabled";
                    localPlayer.displayClientMessage(new TextComponent(message), true);
                }
                else if (!pressed && skedaddleToggleKeyAlreadyPressed) {
                    skedaddleToggleKeyAlreadyPressed = false;
                }
            }
        }
    }

}
