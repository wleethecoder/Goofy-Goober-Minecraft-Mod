package com.wenhanlee.goofygoober.events;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.PlayersAndVillagersTickCounter;
import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.capabilities.time.ITimeCounter;
import com.wenhanlee.goofygoober.capabilities.time.TimeCounter;
import com.wenhanlee.goofygoober.capabilities.time.TimeCounterProvider;
import com.wenhanlee.goofygoober.sounds.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ITimeCounter.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if ((event.getObject() instanceof Player || event.getObject() instanceof Villager) && !event.getObject().getCommandSenderWorld().isClientSide) {
            TimeCounterProvider timeCounterProvider = new TimeCounterProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "counter"), timeCounterProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "limit"), timeCounterProvider);
            event.addListener(timeCounterProvider::invalidate);
        }
    }

    // sharp scream of pain whenever touching a painful object
    // throws the mob high up in the air
    @SubscribeEvent
    public static void screamingPain(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide()) {
            String source = event.getSource().getMsgId();
            float yMovement = 1.5F;
            // TODO: add more sources of damage
            // TODO: add more screaming sounds
            if (source.equals("cactus") || source.equals("inFire") || source.equals("lava")) {
                if (source.equals("inFire")) yMovement = 2.5F;
                if (source.equals("lava")) yMovement = 5.0F;
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, yMovement, 0.0D));
                entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.SCREAM.get(), SoundSource.AMBIENT, 1.0F, 1.0F);
            }
        }
    }

    // Players and Villagers snore when they sleep
    @SubscribeEvent
    public static void snore(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if ((entity instanceof Player || entity instanceof Villager) && !entity.level.isClientSide()) {
            entity.getCapability(ModCapabilities.TIME_COUNTER_CAPABILITY).ifPresent(iTimeCounter -> {
                TimeCounter timeCounter = (TimeCounter) iTimeCounter;
                timeCounter.incrementCounter();
                if (timeCounter.counter >= timeCounter.limit) {
                    if (entity.isSleeping()) {
                        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), timeCounter.sleepingNoise, SoundSource.AMBIENT, 1.0F, 1.0F);
                        timeCounter.resetCounter();
                        timeCounter.rollLimit();
                    }
                    else {
                        timeCounter.rollSleepingNoise();
                    }
                }
            });
        }
    }

    // player eats all the food at once
    // can become comically fat, changing the player's appearance and giving it slowness and resistance
    @SubscribeEvent
    public static void eat(LivingEntityUseItemEvent.Finish event) {
        Entity entity = event.getEntity();
        if (!entity.level.isClientSide() && entity instanceof Player) {
            Player player = (Player) entity;
            if (event.getItem().isEdible()) {
                ItemStack handItem = player.getMainHandItem();
                if (player.getOffhandItem() == event.getItem()) {
                    handItem = player.getOffhandItem();
                }
                int count = handItem.getCount();
                if (count > 1 && handItem.getItem().getFoodProperties() != null) { // unsure if the second condition is necessary
                    // play sound
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.PLAYER_GORGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

                    // TODO: make player wider
//                    player.STANDING_DIMENSIONS.scale(5.0f, 1.0f);
//                    player.setBoundingBox(new AABB(player.getX() - 0.3, player.getY() - 0.3, player.getZ() - 0.3, player.getX() + 0.3, player.getY() + 0.3, player.getZ() + 0.3));

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

                    // remove all food items from hand
                    handItem.setCount(0);
                }
            }
        }
    }

}
