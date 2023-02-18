package com.leecrafts.goofygoober.client.events;

import com.leecrafts.goofygoober.GoofyGoober;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientScreenEvents {

    @SubscribeEvent
    public static void onInitScreenEvent(ScreenEvent.InitScreenEvent.Post event) {
        if (event.getScreen() instanceof SoundOptionsScreen soundOptionsScreen) {
            System.out.println("sound options screen");
            soundOptionsScreen.render(new PoseStack(), 10, 10, 0);
        }
    }

}
