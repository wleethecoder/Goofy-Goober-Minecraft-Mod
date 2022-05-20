package com.wenhanlee.goofygoober.client.events;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.common.effects.ModEffects;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// must be client-side only
@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {

    static boolean needToChangeEyeHeight = false;

    // if a living entity gains the fat effect, then it becomes an absolute unit
    // render size becomes three times as wide
    @SubscribeEvent
    public static void entitySizeChange(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null && livingEntity.getActiveEffectsMap() != null) {
//            System.out.println("[CLIENT] " + livingEntity.getDisplayName().getString() + " (" + livingEntity.getStringUUID() + ")" + " is fat: " + isFat);
            if (livingEntity.hasEffect(ModEffects.FAT.get())) event.getPoseStack().scale(3.0F, 1.0F, 3.0F);
            if (livingEntity.hasEffect(ModEffects.SQUASHED.get())) event.getPoseStack().scale(1.0F, 0.1389F, 1.0F);
        }
    }

//    @SubscribeEvent
//    public static void squashedEyeHeight(EntityViewRenderEvent event) throws InvocationTargetException, IllegalAccessException {
//        LocalPlayer localPlayer = Minecraft.getInstance().player;
//        if (localPlayer.getActiveEffectsMap() != null && localPlayer.hasEffect(ModEffects.SQUASHED.get())) {
//            // TODO this might not work due to glitchy fog and particles
//            // TODO update reach distance
//            Camera camera = event.getCamera();
//            Method method = ObfuscationReflectionHelper.findMethod(Camera.class, "move", double.class, double.class, double.class);
//            method.setAccessible(true);
//            method.invoke(camera, 0, 0.9*(1.8-0.25), 0);
//        }
//    }

    // player eye height is 1.62 = 1.8 * 0.9
    @SubscribeEvent
        public static void squashedEyeHeight(TickEvent.ClientTickEvent event) throws NoSuchFieldException, IllegalAccessException {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null && localPlayer.getActiveEffectsMap() != null) {
            // TODO make this less CPU intensive, if possible
            boolean isFat = localPlayer.hasEffect(ModEffects.SQUASHED.get());
            if (!needToChangeEyeHeight) {
                if (isFat) {
                    needToChangeEyeHeight = true;
                }
            }
            else {
                Field field = Entity.class.getDeclaredField("eyeHeight");
                field.setAccessible(true);
                if (isFat) {
                    field.set(localPlayer, (float) (0.25*0.9));
                }
                else {
                    field.set(localPlayer, (float) (1.8*0.9));
                    needToChangeEyeHeight = false;
                }
            }

//            Field field = Entity.class.getDeclaredField("eyeHeight");
//            field.setAccessible(true);
//            if (localPlayer.hasEffect(ModEffects.SQUASHED.get())) {
//                field.set(localPlayer, (float) (0.25*0.9));
//            }
//            else {
//                field.set(localPlayer, (float) (1.8*0.9));
//            }
        }
    }

}
