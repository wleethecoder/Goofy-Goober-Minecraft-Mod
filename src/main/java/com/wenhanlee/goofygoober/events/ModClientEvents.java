package com.wenhanlee.goofygoober.events;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.effects.ModEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {
    // must be client-side only

//    @SubscribeEvent
//    public static void fat(RenderPlayerEvent.Pre event) {
//        Player player = event.getPlayer();
//        if (player != null) {
//            boolean isFat = player.getActiveEffectsMap() != null && player.hasEffect(ModEffects.FAT.get());
//            System.out.println("[CLIENT] " + player.getDisplayName().getString() + "(" + player.getStringUUID() + ")" + " is fat: " + isFat);
//            if (isFat) event.getPoseStack().scale(3.0F, 1.0F, 3.0F);
//        }
//    }
}
