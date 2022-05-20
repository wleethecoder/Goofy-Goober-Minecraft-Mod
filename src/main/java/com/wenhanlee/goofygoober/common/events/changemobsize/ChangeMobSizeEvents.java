package com.wenhanlee.goofygoober.common.events.changemobsize;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.common.effects.ModEffects;
import com.wenhanlee.goofygoober.common.sounds.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID)
public class ChangeMobSizeEvents {

    private static Field dimensionsField;

    // player eats all the food at once
    // can become comically fat, changing the player's appearance and giving it slowness and resistance
    @SubscribeEvent
    public static void eat(LivingEntityUseItemEvent.Finish event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player && !player.level.isClientSide()) {
            if (event.getItem().isEdible()) {
                ItemStack handItem = player.getMainHandItem();
                if (player.getOffhandItem() == event.getItem()) {
                    handItem = player.getOffhandItem();
                }
                int count = handItem.getCount();
                if (count > 1 && handItem.getItem().getFoodProperties() != null) {
                    // play sound
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.PLAYER_GORGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

                    // fill hunger bar, capped at 20
                    int nutrition = handItem.getItem().getFoodProperties().getNutrition();
                    int totalNutrition = nutrition * count;
                    int newPlayerNutritionRaw = totalNutrition + player.getFoodData().getFoodLevel();
                    int newPlayerNutrition = Math.min(newPlayerNutritionRaw, 20);
//                    player.getFoodData().setFoodLevel(totalNutrition + player.getFoodData().getFoodLevel());
                    player.getFoodData().setFoodLevel(newPlayerNutrition);

                    // increase player saturation
                    float saturation = handItem.getItem().getFoodProperties().getSaturationModifier() * 2 * nutrition;
                    // saturation effect would not work in this case because every tick of its duration it instantly adds saturation, making it broken
                    float totalSaturation = saturation * count;
                    float newPlayerSaturation = totalSaturation + player.getFoodData().getSaturationLevel();
                    player.getFoodData().setSaturation(newPlayerSaturation);
                    float saturationMultiplier = totalSaturation / 200;
//                    int saturationAmplifier = (int) totalSaturation - 1;

                    // add slowness and resistance
                    // resistance amplifier is capped at 3
                    int slownessAmplifier = Math.round(saturationMultiplier) - 1;
                    int resistanceAmplifier = Math.min(slownessAmplifier, 3);
                    int duration = (int) (saturationMultiplier * 60 * 20);
//                    player.addEffect(new MobEffectInstance(MobEffects.SATURATION, duration, saturationAmplifier, true, false));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, slownessAmplifier));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, resistanceAmplifier));
                    player.addEffect(new MobEffectInstance(ModEffects.FAT.get(), duration));

                    // remove all food items from hand
                    handItem.setCount(0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void squash(LivingHurtEvent event) {
        DamageSource damageSource = event.getSource();
        LivingEntity livingEntity = event.getEntityLiving();
        if (damageSource.getDirectEntity() instanceof IronGolem) {
            livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().subtract(0, 1.5, 0));
            livingEntity.addEffect(new MobEffectInstance(ModEffects.SQUASHED.get(), 30 * 20));
        }

        if (damageSource == DamageSource.ANVIL) {
            livingEntity.addEffect(new MobEffectInstance(ModEffects.SQUASHED.get(), 10 * 20));
        }
    }

    @SubscribeEvent
    public static void squashFall(LivingFallEvent event) {
        float distance = event.getDistance();
        if (distance >= 20F) {
            event.getEntityLiving().addEffect(new MobEffectInstance(ModEffects.SQUASHED.get(), (int) (distance / 2 * 20)));
        }
    }

    @SubscribeEvent
    public static void changeMobSize(LivingEvent.LivingUpdateEvent event) throws IllegalAccessException {
        LivingEntity livingEntity = event.getEntityLiving();
        if (livingEntity.getActiveEffectsMap() != null) {

            EntityDimensions entityDimensions = livingEntity.getDimensions(livingEntity.getPose());

            boolean isFat = livingEntity.hasEffect(ModEffects.FAT.get());
            boolean isSquashed = livingEntity.hasEffect(ModEffects.SQUASHED.get());

            // When the fat/squashed effect wears off, the living entity still maintains its changed AABB.
            // To solve this, refresh dimensions if the living entity does not have the fat/squashed effect AND its AABB has not changed back to normal.
            double aabbWidth = ChangeMobSizeHelper.roundDigits(livingEntity.getBoundingBox().getXsize(), 4);
            double aabbHeight = ChangeMobSizeHelper.roundDigits(livingEntity.getBoundingBox().getYsize(), 2);
            double fatWidth = ChangeMobSizeHelper.roundDigits(3 * entityDimensions.width, 4);
            double squashedHeight = 0.25;
            boolean needToRevertFatChange = !isFat && aabbWidth == fatWidth;
            boolean needToRevertSquashedChange = !isSquashed && aabbHeight == squashedHeight;
            if (needToRevertFatChange || needToRevertSquashedChange) {
                livingEntity.refreshDimensions();
            }

            // change dimensions of living entity using reflections
            if (isFat || isSquashed) {
                if (dimensionsField == null) {
                    dimensionsField = ObfuscationReflectionHelper.findField(Entity.class, "dimensions");
                    dimensionsField.setAccessible(true);
                }
                if (isFat) entityDimensions = entityDimensions.scale(3.0F, 1.0F);
                if (isSquashed) entityDimensions = entityDimensions.scale(1.0F, (float) (0.25/entityDimensions.height));
                dimensionsField.set(livingEntity, entityDimensions);
                EntityDimensions newEntityDimensions = (EntityDimensions) dimensionsField.get(livingEntity);
                livingEntity.setBoundingBox(newEntityDimensions.makeBoundingBox(
                        livingEntity.getX(),
                        livingEntity.getY(),
                        livingEntity.getZ()
                ));
            }

        }
    }

}
