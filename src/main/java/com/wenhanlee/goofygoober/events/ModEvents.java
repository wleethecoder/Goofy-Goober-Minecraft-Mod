package com.wenhanlee.goofygoober.events;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.sounds.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID)
public class ModEvents {

    // sharp scream of pain whenever touching a painful object
    // will throw the mob high up in the air
    @SubscribeEvent
    public static void screamingPain(LivingDamageEvent event) {
        Entity entity = event.getEntityLiving();
        if (!entity.level.isClientSide()) {
            String source = event.getSource().getMsgId();
            float yMovement = 1.5F;
            if (source.equals("cactus") || source.equals("inFire") || source.equals("lava")) {
                if (source.equals("inFire")) yMovement = 2.5F;
                if (source.equals("lava")) yMovement = 5.0F;
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, yMovement, 0.0D));
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    player.playSound(ModSounds.SCREAM.get(), 1.0F, 1.0F);
                }
                else entity.playSound(ModSounds.SCREAM.get(), 1.0F, 1.0F);
            }
        }
    }

    @SubscribeEvent
    public static void snore(EntityEvent.Size event) {
        Entity entity = event.getEntity();
        if (entity instanceof Villager) {
            Random random = new Random();
            int random_int = random.nextInt(2);
            SoundEvent sound = ModSounds.SNORE_LOUD.get();
            if (random_int == 1) sound = ModSounds.SNORE_MIMIMI.get();
            if (!((Villager) entity).isSleeping()) entity.playSound(sound, 1.0F, 1.0F);
//            while (((Villager) entity).isSleeping()) {
//                entity.playSound(sound, 1.0F, 1.0F);
//                int start = entity.tickCount;
//                while (entity.tickCount - start < 60);
//            }
        }
    }

    // player eats all the food at once
    // can become comically fat, changing the player's appearance and giving it saturation, resistance, and slowness
    @SubscribeEvent
    public static void eat(LivingEntityUseItemEvent event) {
        Entity entity = event.getEntity();
        if (!entity.level.isClientSide() && entity instanceof Player) {
            Player player = (Player) entity;
            if (event.getItem().isEdible()) {
                ItemStack handItem = player.getMainHandItem();
                if (player.getOffhandItem() == event.getItem()) {
                    handItem = player.getOffhandItem();
                }
                int count = handItem.getCount();
                if (count > 1) {
                    int nutrition = handItem.getItem().getFoodProperties().getNutrition();
                    float saturationModifier = handItem.getItem().getFoodProperties().getSaturationModifier() * 2 * nutrition;
                    float saturationMultiplier = saturationModifier / 2 * count;
                    int saturationAmplifier = (int) saturationMultiplier - 1;
                    int slownessAmplifier = (int) (saturationMultiplier / 100) - 1;
                    int resistanceAmplifier = slownessAmplifier > 3 ? 3 : slownessAmplifier;
                    int duration = (int) (saturationMultiplier / 100 * 60 * 20);
                    handItem.setCount(0);
                    player.addEffect(new MobEffectInstance(MobEffects.SATURATION, duration, saturationAmplifier));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, slownessAmplifier));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, resistanceAmplifier));
//                    player.STANDING_DIMENSIONS.scale(5.0f, 0.0f);
//                    player.setBoundingBox(new AABB(player.getX() - 0.3, player.getY() - 0.3, player.getZ() - 0.3, player.getX() + 0.3, player.getY() + 0.3, player.getZ() + 0.3));
                }
            }
        }
    }

}
