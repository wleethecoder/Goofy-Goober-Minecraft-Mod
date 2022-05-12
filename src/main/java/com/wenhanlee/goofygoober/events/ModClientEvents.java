package com.wenhanlee.goofygoober.events;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.capabilities.fat.Fat;
import com.wenhanlee.goofygoober.capabilities.fat.IFat;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {
    // must be client-side only
    static LazyOptional<IFat> fatLazyOptional = null;

    @SubscribeEvent
    public static void fat(RenderPlayerEvent.Pre event) {
//        Player player = event.getPlayer();
        final Player player = Minecraft.getInstance().level.getPlayerByUUID(event.getPlayer().getUUID());
        if (player != null) {
            System.out.println("client fat() is present? " + player.getCapability(ModCapabilities.FAT_CAPABILITY).isPresent());
            fatLazyOptional = player.getCapability(ModCapabilities.FAT_CAPABILITY);
            IFat fat = fatLazyOptional.orElse(new Fat());
            System.out.println("is player fat? " + fat.getFat());
            if (fat.getFat()) {
                event.getPoseStack().scale(3.0F, 1.0F, 3.0F);
            }
//            player.getCapability(ModCapabilities.FAT_CAPABILITY).ifPresent(iFat -> {
//                Fat fat = (Fat) iFat;
//
//                System.out.println("is player fat? " + fat.getFat());
//
//                if (fat.getFat()) {
//                    event.getPoseStack().scale(3.0F, 1.0F, 3.0F);
//                }
//            });
        }
    }
}
