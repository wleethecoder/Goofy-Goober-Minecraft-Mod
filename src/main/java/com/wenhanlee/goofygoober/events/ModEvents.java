package com.wenhanlee.goofygoober.events;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.PlayersAndVillagersTickCounter;
import com.wenhanlee.goofygoober.sounds.ModSounds;
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

    // TODO: implement hashmap (please)
//    static List<PlayersAndVillagersTickCounter> playersAndVillagers = new ArrayList<PlayersAndVillagersTickCounter>();
    static HashMap<String, PlayersAndVillagersTickCounter> playersAndVillagers = new HashMap<>();

//    public static int indexPlayerOrVillager(String UUID, List<PlayersAndVillagersTickCounter> playersAndVillagers) {
//        for (int i = 0; i < playersAndVillagers.size(); i++) {
//            if (UUID.equals(playersAndVillagers.get(i).getUUID())) {
//                return i;
//            }
//        }
//        return -1;
//    }

    // sharp scream of pain whenever touching a painful object
    // will throw the mob high up in the air
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
//                if (entity instanceof Player) {
//                    Player player = (Player) entity;
//                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SCREAM.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
//                }
//                else entity.playSound(ModSounds.SCREAM.get(), 1.0F, 1.0F);
            }
        }
    }

    @SubscribeEvent
    public static void trackPlayersAndVillagers(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player || entity instanceof Villager) {
//            playersAndVillagers.add(new PlayersAndVillagersTickCounter(entity));
            playersAndVillagers.put(entity.getStringUUID(), new PlayersAndVillagersTickCounter(entity));
        }

    }

    // TODO: make villager and player play sound in a loop while sleeping
    // TODO: polish the timing of noises
    // TODO: polish the noises themselves
    @SubscribeEvent
    public static void snore(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        String entityUUID = entity.getStringUUID();
//        int index = indexPlayerOrVillager(entity.getStringUUID(), playersAndVillagers);
        PlayersAndVillagersTickCounter playerOrVillagerTickCounter = playersAndVillagers.get(entityUUID);
        if (playerOrVillagerTickCounter != null) {
            if (entity.isRemoved()) {
                playersAndVillagers.remove(entityUUID);
            }
            else {
                playersAndVillagers.get(entityUUID).incrementTickCount();
                if (playersAndVillagers.get(entityUUID).getTickCount() >= 200) {
                    if (entity.isSleeping()) {
                        SoundEvent sound = playersAndVillagers.get(entityUUID).getSleepingNoise();
                        entity.playSound(sound, 1.0F, 1.0F);
                        entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundSource.AMBIENT, 1.0F, 1.0F);
                        playersAndVillagers.get(entityUUID).resetTickCount();
                    }
                    else {
                        playersAndVillagers.get(entityUUID).rollSleepingNoise();
                    }
                }
            }
        }
    }

    // player eats all the food at once
    // can become comically fat, changing the player's appearance and giving it slowness and resistance
    // TODO: debug hunger and saturation levels
    // TODO: decide if you need .Start or .Finish
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
                if (count > 1 && handItem.getItem().getFoodProperties() != null) {
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
                    System.out.println(player.getFoodData().getFoodLevel());

                    // increase player saturation
                    float saturation = handItem.getItem().getFoodProperties().getSaturationModifier() * 2 * nutrition;
                    // saturation effect would not work in this case because every tick of its duration it instantly adds saturation, making it broken
                    float totalSaturation = saturation * count;
                    float newPlayerSaturation = totalSaturation + player.getFoodData().getSaturationLevel();
                    player.getFoodData().setSaturation(newPlayerSaturation);
                    float saturationMultiplier = totalSaturation / 200;
//                    int saturationAmplifier = (int) totalSaturation - 1;
                    System.out.println(player.getFoodData().getSaturationLevel());

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
