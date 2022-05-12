package com.wenhanlee.goofygoober.events;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.capabilities.time.ITimeCounter;
import com.wenhanlee.goofygoober.capabilities.time.TimeCounterProvider;
import com.wenhanlee.goofygoober.effects.ModEffects;
import com.wenhanlee.goofygoober.sounds.CustomMobNoise;
import com.wenhanlee.goofygoober.sounds.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID)
public class ModEvents {

    static CustomMobNoise customMobNoise = new CustomMobNoise();

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ITimeCounter.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEventTimeCounter(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity && !event.getObject().getCommandSenderWorld().isClientSide) {
            TimeCounterProvider timeCounterProvider = new TimeCounterProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "counter"), timeCounterProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "limit"), timeCounterProvider);
            event.addListener(timeCounterProvider::invalidate);
        }
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinWorldEvent event) {
        customMobNoise = new CustomMobNoise();
    }

    // sharp scream of pain whenever touching a painful object
    // throws the mob high up in the air
    @SubscribeEvent
    public static void scream(LivingDamageEvent event) {
        if (!event.getEntity().level.isClientSide()) {
            customMobNoise.scream(event);
        }
    }

    // Players and Villagers snore when they sleep
    @SubscribeEvent
    public static void timeCounterTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide()) {
            customMobNoise.ambient(entity);
        }
//        if (entity instanceof Player player
//                && player.getActiveEffectsMap() != null
//                && player.hasEffect(ModEffects.FAT.get())) {
//            player.setBoundingBox(new AABB(player.getX()+0.9, player.getY()+1.8, player.getZ()+0.9, player.getX()-0.9, player.getY(), player.getZ()-0.9));
//        }
    }

    @SubscribeEvent
    public static void fatTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof Player player && !player.level.isClientSide()) {
            // server player sends to (all) clients its fat status
            boolean isFat = player.getActiveEffectsMap() != null && player.hasEffect(ModEffects.FAT.get());
//            PacketHandler.INSTANCE.send(
//                    PacketDistributor.ALL.noArg(),
//                    new ClientboundMobSizeUpdatePacket(isFat)
//            );
        }
    }

    // player eats all the food at once
    // can become comically fat, changing the player's appearance and giving it slowness and resistance
    @SubscribeEvent
    public static void eat(LivingEntityUseItemEvent.Finish event) {
        Entity entity = event.getEntity();
        if (!entity.level.isClientSide() && entity instanceof Player player) {
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

                    // forcibly trigger fat()
//                    player.setForcedPose(Pose.CROUCHING);

                }
            }
        }
    }

//    @SubscribeEvent
//    public static void fatAddEffect(PotionEvent.PotionAddedEvent event) {
//        if (!event.getEntityLiving().level.isClientSide()
//                && event.getPotionEffect().getEffect() == ModEffects.FAT.get()
//                && event.getEntityLiving() instanceof Player player) {
//            PacketHandler.INSTANCE.sendToServer(new ServerboundMobSizeUpdatePacket(true));
//        }
//    }
//
//    @SubscribeEvent
//    public static void fatRemoveEffect(PotionEvent.PotionRemoveEvent event) {
//        if (!event.getEntityLiving().level.isClientSide()
//                && event.getPotion() == ModEffects.FAT.get()
//                && event.getEntityLiving() instanceof Player player) {
//            PacketHandler.INSTANCE.sendToServer(new ServerboundMobSizeUpdatePacket(false));
//        }
//    }

    //    @SubscribeEvent
//    public static void fatSize(EntityEvent.Size event) {
//        if (!event.getEntity().level.isClientSide() && event.getEntity() instanceof Player player) {
////            System.out.println("map: " + player.getActiveEffectsMap() != null);
////            if (player.getActiveEffectsMap() != null) System.out.println("has fat: " + player.hasEffect(ModEffects.FAT.get()));
//            if (player.getActiveEffectsMap() != null && player.hasEffect(ModEffects.FAT.get())) {
//                event.setNewSize(new EntityDimensions(1.8F, 1.8F, false), true);
//            }
//            else {
//                System.out.println("DOIT");
//                event.setNewSize(new EntityDimensions(0.6F, 1.8F, false), true);
//            }
//        }
//    }

}
