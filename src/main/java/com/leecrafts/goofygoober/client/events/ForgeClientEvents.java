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

    @SubscribeEvent
    public static void initializeSkedaddleDisabled(ClientPlayerNetworkEvent.LoggedInEvent event) {
        skedaddleEnabled = false;
        PacketHandler.INSTANCE.sendToServer(new ServerboundSkedaddleTogglePacket(false));
    }

    @SubscribeEvent
    public static void toggleSkedaddle(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer != null) {
                boolean pressed = KeyInit.toggleSkedaddleKeyMapping.isDown();
                if (pressed && !skedaddleToggleKeyAlreadyPressed) {
                    skedaddleToggleKeyAlreadyPressed = true;
                    skedaddleEnabled = !skedaddleEnabled;
                    String message = skedaddleEnabled ? "Skedaddle enabled" : "Skedaddle disabled";
                    localPlayer.displayClientMessage(new TextComponent(message), true);
                    PacketHandler.INSTANCE.sendToServer(new ServerboundSkedaddleTogglePacket(skedaddleEnabled));
                }
                else if (!pressed && skedaddleToggleKeyAlreadyPressed) {
                    skedaddleToggleKeyAlreadyPressed = false;
                }
            }
        }
    }

}
