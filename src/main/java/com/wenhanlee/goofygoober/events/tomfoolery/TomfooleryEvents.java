package com.wenhanlee.goofygoober.events.tomfoolery;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.cooldownCounter.ITomfooleryCooldownCounter;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.cooldownCounter.TomfooleryCooldownCounter;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.cooldownCounter.TomfooleryCooldownCounterProvider;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.scallywag.TomfooleryScallywag;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.scallywag.TomfooleryScallywagProvider;
import com.wenhanlee.goofygoober.sounds.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID)
public class TomfooleryEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ITomfooleryCooldownCounter.class);
    }

    // a cooldown of two minutes happens when a player causes tomfoolery
    // a player causes tomfoolery by being near 3 or more monsters
    @SubscribeEvent
    public static void onAttachCapabilitiesEventCooldownCounter(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player && !event.getObject().getCommandSenderWorld().isClientSide()) {
            TomfooleryCooldownCounterProvider tomfooleryCooldownCounterProvider = new TomfooleryCooldownCounterProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "tomfoolery_cooldown_counter"), tomfooleryCooldownCounterProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "tomfoolery_cooldown_on"), tomfooleryCooldownCounterProvider);
            event.addListener(tomfooleryCooldownCounterProvider::invalidate);
        }
    }

    // scallywags do not count towards whether a player is near 3 or more monsters
    // they also make really goofy noises
    @SubscribeEvent
    public static void onAttachCapabilitiesEventScallywag(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Mob && !event.getObject().getCommandSenderWorld().isClientSide()) {
            TomfooleryScallywagProvider tomfooleryScallywagProvider = new TomfooleryScallywagProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "is_eligible_to_cause_tomfoolery"), tomfooleryScallywagProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "scallywag"), tomfooleryScallywagProvider);
            event.addListener(tomfooleryScallywagProvider::invalidate);
        }
    }

    // scallywags do not despawn
    @SubscribeEvent
    public static void onScallywagSpawnEvent(LivingSpawnEvent.AllowDespawn event) {
        if (event.getEntityLiving() instanceof Mob mob && !mob.level.isClientSide()) {
            mob.getCapability(ModCapabilities.TOMFOOLERY_SCALLYWAG_CAPABILITY).ifPresent(iTomfooleryScallywag -> {
                TomfooleryScallywag tomfooleryScallywag = (TomfooleryScallywag) iTomfooleryScallywag;
                if (tomfooleryScallywag.isScallywag()) event.setResult(Event.Result.DENY);
            });
        }
    }

    // When the player is around 3 or more monster mobs (which include Endermen and Zombified Piglins), unbridled tomfoolery ensues
    @SubscribeEvent
    public static void playerTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player && !player.level.isClientSide()) {
            player.getCapability(ModCapabilities.TOMFOOLERY_COOLDOWN_COUNTER_CAPABILITY).ifPresent(iTomfooleryCooldownCounter -> {
                TomfooleryCooldownCounter tomfooleryCooldownCounter = (TomfooleryCooldownCounter) iTomfooleryCooldownCounter;
                if (!tomfooleryCooldownCounter.cooldown) {
                    ArrayList<Mob> nearbyEligibleMobs = TomfooleryHelper.getNearbyMobs(player, true);
//                    System.out.println("Monsters eligible to cause tomfoolery: " + eligibleMonsters);
                    if (nearbyEligibleMobs.size() >= TomfooleryHelper.NUM_NEARBY_MOBS) {
                        System.out.println("TOMFOOLERY ENSUES!");

                        for (Mob mob : nearbyEligibleMobs) {
                            // nearby eligible mobs no longer become eligible to cause any more tomfoolery
                            TomfooleryHelper.revokeEligibility(mob);
                        }

                        boolean anyScallywagSpawned = false;
                        for (int i = 0; i < TomfooleryHelper.NUM_MOBS_TO_SUMMON; i++) {
                            try {
                                if (TomfooleryHelper.spawnScallywag(player)) anyScallywagSpawned = true;
                            } catch (InvocationTargetException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        if (anyScallywagSpawned) tomfooleryCooldownCounter.cooldown = true;

                    }
                }
                else {
                    tomfooleryCooldownCounter.incrementCounter();
                    if (tomfooleryCooldownCounter.counter >= tomfooleryCooldownCounter.limit) {
                        tomfooleryCooldownCounter.resetCounter();
                        tomfooleryCooldownCounter.cooldown = false;
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void scallywagTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Mob mob) {
            LivingEntity target = mob.getTarget();
            if (target != null) {
                double distance = mob.distanceTo(target);
                mob.getCapability(ModCapabilities.TOMFOOLERY_SCALLYWAG_CAPABILITY).ifPresent(iTomfooleryScallywag -> {
                    TomfooleryScallywag tomfooleryScallywag = (TomfooleryScallywag) iTomfooleryScallywag;
                    if (tomfooleryScallywag.isScallywag() && distance < 3.0) {
                        // a large cloud of dust appears during the tomfoolery
                        // that is, when the scallywags are fighting the player or each other
                        TomfooleryHelper.dustCloudBetween(mob, target);

                        if (tomfooleryScallywag.counter >= tomfooleryScallywag.limit) {
                            Random random = new Random();
                            mob.playSound(ModSounds.TOMFOOLERY.get(), 2.5F, 0.9f + random.nextFloat(0.2f));
                            tomfooleryScallywag.resetCounter();
                            tomfooleryScallywag.rollLimit();
                        }
                        else tomfooleryScallywag.incrementCounter();
                    }
                });
            }
        }
    }

    // special case for slimes and magma cubes because they cannot normally damage any mobs besides players and iron golems
    @SubscribeEvent
    public static void slimeMagmaCubeDamage(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Slime slime) { // MagmaCube extends Slime
            slime.getCapability(ModCapabilities.TOMFOOLERY_SCALLYWAG_CAPABILITY).ifPresent(iTomfooleryScallywag -> {
                TomfooleryScallywag tomfooleryScallywag = (TomfooleryScallywag) iTomfooleryScallywag;
                if (tomfooleryScallywag.isScallywag()) {
                    Method method = ObfuscationReflectionHelper.findMethod(Slime.class, "dealDamage", LivingEntity.class);
                    // 3*.6 - 0.5202*(2^(3-1))/2
                    AABB aabb = slime.getBoundingBox();
                    int size = slime.getSize();
                    double offset = size * 0.6 - Math.abs(aabb.minX - aabb.maxX) * Math.pow(2, (size - 1)) / 2;
                    List<Entity> list = slime.level.getEntities(slime, aabb.inflate(offset));
                    method.setAccessible(true);
                    for (Entity entity : list) {
                        if (entity instanceof LivingEntity livingEntity && livingEntity.equals(slime.getTarget())) {
                            try {
                                method.invoke(slime, livingEntity);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

}
