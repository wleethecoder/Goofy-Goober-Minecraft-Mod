package com.leecrafts.goofygoober.client.events;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.effects.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

// must be client-side only
@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {

    private static boolean needToChangeEyeHeight = false;
    // eyeHeight -> f_19816_
    private static final Field eyeHeightField = ObfuscationReflectionHelper.findField(Entity.class, "f_19816_");;
//    private static final ResourceLocation texture = new ResourceLocation("goofygoober:textures/mobs/steak.png");
    private static boolean steakRendered = false;

    public ModClientEvents() {
        eyeHeightField.setAccessible(true);
    }

    // if a living entity gains the fat effect, then it becomes an absolute unit
    // render size becomes three times as wide
    @SubscribeEvent
    public static void changeMobRenderSize(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity != null && livingEntity.getActiveEffectsMap() != null) {
            EntityDimensions entityDimensions = livingEntity.getDimensions(livingEntity.getPose());
//            System.out.println("[CLIENT] " + livingEntity.getDisplayName().getString() + " (" + livingEntity.getStringUUID() + ")" + " is fat: " + isFat);
            if (livingEntity.hasEffect(ModEffects.FAT.get())) event.getPoseStack().scale(3, 1, 3);
            if (livingEntity.hasEffect(ModEffects.SQUASHED.get())) event.getPoseStack().scale(1, (float) (0.25/entityDimensions.height), 1);
            if (livingEntity.hasEffect(ModEffects.CRASHED.get())) event.getPoseStack().scale((float) (0.25/entityDimensions.width), 1, 1);
            if (livingEntity.hasEffect(ModEffects.SMASHED.get())) event.getPoseStack().scale(1, 1, (float) (0.25/entityDimensions.width));
        }
    }

    // player eye height is 1.62 = 1.8 * 0.9
    @SubscribeEvent
        public static void squashedEyeHeight(TickEvent.ClientTickEvent event) throws IllegalAccessException {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null && localPlayer.getActiveEffectsMap() != null) {
            boolean isSquashed = localPlayer.hasEffect(ModEffects.SQUASHED.get());
            if (!needToChangeEyeHeight) {
                if (isSquashed) {
                    needToChangeEyeHeight = true;
                }
            }
            else {
//                eyeHeightField.setAccessible(true);
                if (isSquashed) {
                    eyeHeightField.set(localPlayer, (float) (0.25*0.9));
                }
                else {
                    eyeHeightField.set(localPlayer, (float) (1.8*0.9));
                    needToChangeEyeHeight = false;
                }
            }

        }
    }

    @SubscribeEvent
    public static void superRun(InputEvent.KeyInputEvent event) {
        System.out.println(event.getKey() + ", " + event.getScanCode() + ", " + event.getAction() + ", " + event.getModifiers());
    }

    @SubscribeEvent
    public static void steak(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            Entity entity = event.getEntity();
//            System.out.println(event.getRenderer());
//            System.out.println(event.getEntity());
            if (!steakRendered) {
                steakRendered = true;
                event.setCanceled(true);
                EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
//                Creeper creeper = new Creeper(EntityType.CREEPER, localPlayer.level);
                Creeper creeper = EntityType.CREEPER.create(localPlayer.clientLevel);
                assert creeper != null;
                entityRenderDispatcher.render(
                        creeper,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        entity.getViewYRot(event.getPartialTick()),
                        event.getPartialTick(),
                        event.getPoseStack(),
                        event.getMultiBufferSource(),
                        event.getPackedLight()
                );
            }
        }
    }

}
