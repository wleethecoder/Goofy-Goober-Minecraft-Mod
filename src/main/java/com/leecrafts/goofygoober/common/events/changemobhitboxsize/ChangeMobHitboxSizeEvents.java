package com.leecrafts.goofygoober.common.events.changemobhitboxsize;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.effects.ModEffects;
import com.leecrafts.goofygoober.common.misc.Utilities;
import com.leecrafts.goofygoober.common.sounds.ModSounds;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChangeMobHitboxSizeEvents {

    // dimensions -> f_19815_
    private static final Field dimensionsField = ObfuscationReflectionHelper.findField(Entity.class, "f_19815_");

    public ChangeMobHitboxSizeEvents() {
        dimensionsField.setAccessible(true);
    }

    // player eats all the food at once
    // can become comically fat, changing the player's appearance and giving it slowness and resistance
    @SubscribeEvent
    public static void gorge(LivingEntityUseItemEvent.Finish event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player && !player.level.isClientSide()) {
            ItemStack previousItemStack = event.getItem();
            ItemStack currentItemStack = event.getResultStack();
            if (previousItemStack.isEdible()) {
                ItemStack handItemStack = player.getOffhandItem();
                if (ItemStack.matches(player.getMainHandItem(), currentItemStack)) {
                    handItemStack = player.getMainHandItem();
                }
                int previousCount = previousItemStack.getCount();
                FoodProperties foodProperties = handItemStack.getItem().getFoodProperties(handItemStack, player);
                if (previousCount > 1 && foodProperties != null) {
                    // play sound
                    Utilities.playSound(player, "player_gorge");

                    // fill hunger bar, capped at 20
                    int nutrition = foodProperties.getNutrition();
                    int totalNutrition = nutrition * previousCount;
                    int newPlayerNutritionRaw = totalNutrition + player.getFoodData().getFoodLevel();
                    int newPlayerNutrition = Math.min(newPlayerNutritionRaw, 20);
//                    player.getFoodData().setFoodLevel(totalNutrition + player.getFoodData().getFoodLevel());
                    player.getFoodData().setFoodLevel(newPlayerNutrition);

                    // increase player saturation
                    float saturation = foodProperties.getSaturationModifier() * 2 * nutrition;
                    // saturation effect would not work in this case because every tick of its duration it instantly adds saturation, making it broken
                    float totalSaturation = saturation * previousCount;
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
                    player.addEffect(new MobEffectInstance(ModEffects.FAT.get(), duration, 0, false, false));

                    // remove all food items from hand
                    handItemStack.setCount(0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void squash(LivingHurtEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!livingEntity.level.isClientSide()) {
            DamageSource damageSource = event.getSource();
            if (damageSource.getDirectEntity() instanceof IronGolem) {
                livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().subtract(0, 1.5, 0));
                livingEntity.addEffect(new MobEffectInstance(ModEffects.SQUASHED.get(), 30 * 20, 0, false, false));
                Utilities.playSound(livingEntity, "doit");
            }

            if (damageSource == DamageSource.ANVIL || damageSource == DamageSource.FALLING_STALACTITE) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.SQUASHED.get(), 10 * 20, 0, false, false));
                Utilities.playSound(livingEntity, "doit");
            }
        }
    }

    @SubscribeEvent
    public static void squashFall(LivingFallEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!livingEntity.level.isClientSide()) {
            float distance = event.getDistance();
            if (distance >= 20F) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.SQUASHED.get(), (int) (distance / 2 * 20), 0, false, false));
                Utilities.playSound(livingEntity, "impact");
            }
        }
    }

    @SubscribeEvent
    public static void crashSmashElytra(LivingHurtEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!livingEntity.level.isClientSide() && event.getSource() == DamageSource.FLY_INTO_WALL) {
            Direction direction = livingEntity.getDirection();
            float damage = event.getAmount();
            int duration = (int) (damage * (18/10) * 20);
            // damage is <= 3 -> effect lasts for 5 seconds
            // damage is <= 6 -> effect lasts for 10 seconds
            // damage is <= 10 -> effect lasts for 18 seconds
            if (direction == Direction.WEST || direction == Direction.EAST) {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.CRASHED.get(), duration, 0, false, false));
            }
            else {
                livingEntity.addEffect(new MobEffectInstance(ModEffects.SMASHED.get(), duration, 0, false, false));
            }
            Utilities.playSound(livingEntity, "impact");
        }
    }

    @SubscribeEvent
    public static void changeMobHitboxSize(LivingEvent.LivingTickEvent event) throws IllegalAccessException {
        LivingEntity livingEntity = event.getEntity();
        if (/*!livingEntity.level.isClientSide() && */livingEntity.getActiveEffectsMap() != null) {

            EntityDimensions entityDimensions = livingEntity.getDimensions(livingEntity.getPose());

            boolean isFat = livingEntity.hasEffect(ModEffects.FAT.get());
            boolean isSquashed = livingEntity.hasEffect(ModEffects.SQUASHED.get());
            boolean isCrashed = livingEntity.hasEffect(ModEffects.CRASHED.get());
            boolean isSmashed = livingEntity.hasEffect(ModEffects.SMASHED.get());

            // When the fat/squashed effect wears off, the living entity still maintains its changed AABB.
            // To solve this, refresh dimensions if the living entity does not have the fat/squashed effect AND its AABB has not changed back to normal.
            double aabbWidth = ChangeMobHitboxSizeHelper.roundDigits(livingEntity.getBoundingBox().getXsize(), 4);
            double aabbHeight = ChangeMobHitboxSizeHelper.roundDigits(livingEntity.getBoundingBox().getYsize(), 2);
            double fatWidth = ChangeMobHitboxSizeHelper.roundDigits(3 * entityDimensions.width, 4);
            double squashedHeight = 0.25;
            double crashedSmashedWidth = 0.25;
            boolean needToRevertFatChange = !isFat && aabbWidth == fatWidth;
            boolean needToRevertSquashedChange = !isSquashed && aabbHeight == squashedHeight;
            boolean needToRevertCrashedChange = !isCrashed && aabbWidth == crashedSmashedWidth;
            boolean needToRevertSmashedChange = !isSmashed && aabbWidth == crashedSmashedWidth;
            if (needToRevertFatChange || needToRevertSquashedChange || needToRevertCrashedChange || needToRevertSmashedChange) {
                livingEntity.refreshDimensions();
            }

            // change dimensions of living entity using reflections
            if (isFat || isSquashed || isCrashed || isSmashed) {
//                dimensionsField.setAccessible(true);
                if (isFat) entityDimensions = entityDimensions.scale(3, 1);
                if (isSquashed) entityDimensions = entityDimensions.scale(1, (float) (0.25/entityDimensions.height));
                if (isCrashed || isSmashed) entityDimensions = entityDimensions.scale((float) (0.25/entityDimensions.width), 1);
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

//    // Test for only the fat effect
//    @SubscribeEvent
//    public static void changeMobHitboxSize(EntityEvent.Size event) {
//        if (event.getEntity() instanceof LivingEntity livingEntity && /*!livingEntity.level.isClientSide() && */livingEntity.getActiveEffectsMap() != null) {
//            EntityDimensions entityDimensions = livingEntity.getDimensions(livingEntity.getPose());
//            if (livingEntity.hasEffect(ModEffects.FAT.get())) {
//                event.setNewSize(entityDimensions.scale(3, 1));
//            }
//            else event.setNewSize(entityDimensions);
//        }
//    }

    @SubscribeEvent
    public static void preventSquashedPlayerFromCrawling(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player && /*!player.level.isClientSide() && */player.getActiveEffectsMap() != null) {
            if (player.hasEffect(ModEffects.SQUASHED.get())) {
                boolean solidAbove = !player.level.noCollision(player.getBoundingBox().expandTowards(0, 1.5, 0));
                if (solidAbove) {
                    if (player.getForcedPose() == null) player.setForcedPose(Pose.STANDING);
                }
                else if (player.getForcedPose() != null) player.setForcedPose(null);
            }
            else if (player.getForcedPose() != null) player.setForcedPose(null);
        }
    }

    @SubscribeEvent
    public static void preventSquashedLivingEntityFromUnexpectedSuffocationDamage(LivingHurtEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!livingEntity.level.isClientSide() && livingEntity.getActiveEffectsMap() != null && livingEntity.hasEffect(ModEffects.SQUASHED.get())) {
            if (event.getSource() == DamageSource.IN_WALL) {
                if (livingEntity.level.noCollision(livingEntity.getBoundingBox())) {
                    event.setCanceled(true);
                }
            }
        }
    }

}
