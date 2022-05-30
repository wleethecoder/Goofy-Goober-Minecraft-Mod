package com.leecrafts.goofygoober.client.events;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.packets.PacketHandler;
import com.leecrafts.goofygoober.common.packets.key.ServerboundPlayerWKeyPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, value = Dist.CLIENT)
public class ClientKeyEvents {

    @SubscribeEvent
    public static void startOrStopSkedaddle(InputEvent.KeyInputEvent event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            if (event.getKey() == InputConstants.KEY_W && event.getAction() == GLFW.GLFW_PRESS) {
                PacketHandler.INSTANCE.sendToServer(new ServerboundPlayerWKeyPacket(true));
            }
            else if (event.getKey() == InputConstants.KEY_W && event.getAction() == GLFW.GLFW_RELEASE) {
                PacketHandler.INSTANCE.sendToServer(new ServerboundPlayerWKeyPacket(false));
            }
        }
    }

}
