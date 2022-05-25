package com.leecrafts.goofygoober.client.events;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.effects.ModEffects;
import com.leecrafts.goofygoober.common.entities.ModEntities;
import com.leecrafts.goofygoober.common.entities.SteakEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

// must be client-side only
@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, value = Dist.CLIENT)
public class ClientRenderEvents {

    private static boolean needToChangeEyeHeight = false;
    // eyeHeight -> f_19816_
    private static final Field eyeHeightField = ObfuscationReflectionHelper.findField(Entity.class, "f_19816_");

    private static boolean renderingCustomEntity = false;
    private static SteakEntity steakEntity;

    public ClientRenderEvents() {
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
            if (livingEntity.hasEffect(ModEffects.SQUASHED.get()))
                event.getPoseStack().scale(1, (float) (0.25 / entityDimensions.height), 1);
            if (livingEntity.hasEffect(ModEffects.CRASHED.get()))
                event.getPoseStack().scale((float) (0.25 / entityDimensions.width), 1, 1);
            if (livingEntity.hasEffect(ModEffects.SMASHED.get()))
                event.getPoseStack().scale(1, 1, (float) (0.25 / entityDimensions.width));
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
            } else {
//                eyeHeightField.setAccessible(true);
                if (isSquashed) {
                    eyeHeightField.set(localPlayer, (float) (0.25 * 0.9));
                } else {
                    eyeHeightField.set(localPlayer, (float) (1.8 * 0.9));
                    needToChangeEyeHeight = false;
                }
            }

        }
    }

//    @SubscribeEvent
//    public static void superRun(InputEvent.KeyInputEvent event) {
//        if (event.getKey() == 87 && event.getModifiers() == 2) {
//            System.out.println("running");
//        }
//         System.out.println(event.getKey() + ", " + event.getScanCode() + ", " + event.getAction() + ", " + event.getModifiers());
//    }

    private static void refreshSteakEntity(LocalPlayer localPlayer, LivingEntity livingEntity) {
        if (steakEntity == null || steakEntity.isRemoved()) {
            steakEntity = ModEntities.STEAK_ENTITY.get().create(localPlayer.clientLevel);
        }
        assert steakEntity != null;
        if (!steakEntity.level.dimension().equals(livingEntity.level.dimension())) {
            steakEntity.discard();
        }
    }

//    @SubscribeEvent
//    public static void a(LivingEvent.LivingUpdateEvent event) {
//        if (event.getEntityLiving() instanceof Player && steakEntity != null) System.out.println(steakEntity.isRemoved());
//    }
//
//    @SubscribeEvent
//    public static void steakDead(LivingDeathEvent event) {
//        LocalPlayer localPlayer = Minecraft.getInstance().player;
//        LivingEntity livingEntity = event.getEntityLiving();
//        if (localPlayer != null && localPlayer.getActiveEffectsMap() != null && !localPlayer.is(livingEntity)) {
//            if (localPlayer.hasEffect(ModEffects.HALLUCINATING.get())) {
//                steakEntity.discard();
//            }
//        }
//    }

    @SubscribeEvent
    public static void steak(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        LivingEntity livingEntity = event.getEntity();
        if (localPlayer != null && localPlayer.getActiveEffectsMap() != null && !localPlayer.is(livingEntity)) {
            if (localPlayer.hasEffect(ModEffects.HALLUCINATING.get())) {
                if (!renderingCustomEntity) {
                    try {
                        renderingCustomEntity = true;
                        event.setCanceled(true);
                        refreshSteakEntity(localPlayer, livingEntity);
//                        steakEntity.setYHeadRot(livingEntity.getYHeadRot());
//                        steakEntity.yHeadRotO = livingEntity.yHeadRotO;
                        steakEntity.setYBodyRot(livingEntity.yBodyRot);
                        steakEntity.yBodyRotO = livingEntity.yBodyRotO;
                        steakEntity.setPose(livingEntity.getPose());
                        Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(steakEntity).render(
                                steakEntity,
                                steakEntity.yBodyRot,
                                event.getPartialTick(),
                                event.getPoseStack(),
                                event.getMultiBufferSource(),
                                event.getPackedLight()
                        );
                    } finally {
                        renderingCustomEntity = false;
                    }
                }
            }
        }
    }

}
