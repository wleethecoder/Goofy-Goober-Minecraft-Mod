package com.leecrafts.goofygoober.common.events.tomfoolery;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.cooldowncounter.ITomfooleryCooldownCounter;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.cooldowncounter.TomfooleryCooldownCounter;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.cooldowncounter.TomfooleryCooldownCounterProvider;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.scallywag.ITomfooleryScallywag;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.scallywag.TomfooleryScallywag;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.scallywag.TomfooleryScallywagProvider;
import com.leecrafts.goofygoober.common.misc.Utilities;
import com.leecrafts.goofygoober.common.sounds.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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

    // dealDamage -> m_33637_
    private static final Method dealDamageMethod = ObfuscationReflectionHelper.findMethod(Slime.class, "m_33637_", LivingEntity.class);;

    public TomfooleryEvents() {
        dealDamageMethod.setAccessible(true);
    }

    // During tomfoolery, 5 different hostile/monster mobs spawn around the player (those spawned mobs are called scallywags).
    // Scallywags can attack each other.
    // A cloud of dust appears whenever scallywags get in a fight.
    // Random goofy sound effects play.
    // As a result, chaos ensues, and it becomes very disorienting to both the eyes and ears. Be careful.

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ITomfooleryCooldownCounter.class);
        event.register(ITomfooleryScallywag.class);
    }

    // a cooldown of two minutes happens when a player causes tomfoolery
    // a player causes tomfoolery by being near 3 or more monsters
    @SubscribeEvent
    public static void onAttachCapabilitiesEventCooldownCounter(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player && !player.getCommandSenderWorld().isClientSide()) {
            TomfooleryCooldownCounterProvider tomfooleryCooldownCounterProvider = new TomfooleryCooldownCounterProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "tomfoolery_cooldown_counter"), tomfooleryCooldownCounterProvider);
//            event.addListener(tomfooleryCooldownCounterProvider::invalidate);
        }
    }

    // scallywags do not count towards whether a player is near 3 or more monsters
    // they also make really goofy noises
    @SubscribeEvent
    public static void onAttachCapabilitiesEventScallywag(AttachCapabilitiesEvent<Entity> event) {
        // the isScallywag variable is false by default (I set it to true when appropriate)
        if (event.getObject() instanceof Mob mob && !mob.getCommandSenderWorld().isClientSide()) {
            TomfooleryScallywagProvider tomfooleryScallywagProvider = new TomfooleryScallywagProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "is_eligible_to_cause_tomfoolery"), tomfooleryScallywagProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "scallywag"), tomfooleryScallywagProvider);
            event.addListener(tomfooleryScallywagProvider::invalidate);
        }
    }

    // I wanted iron golems to also create dust clouds whenever fighting other mobs, so I guess they're one of the good scallywags
    @SubscribeEvent
    public static void ironGolemJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof IronGolem ironGolem && !ironGolem.level.isClientSide()) {
            TomfooleryHelper.youreAScallywag(ironGolem);
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

    // player tick update
    @SubscribeEvent
    public static void playerTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player && !player.level.isClientSide()) {
            player.getCapability(ModCapabilities.TOMFOOLERY_COOLDOWN_COUNTER_CAPABILITY).ifPresent(iTomfooleryCooldownCounter -> {
                TomfooleryCooldownCounter tomfooleryCooldownCounter = (TomfooleryCooldownCounter) iTomfooleryCooldownCounter;
                if (tomfooleryCooldownCounter.counter >= tomfooleryCooldownCounter.limit) {
                    ArrayList<Mob> nearbyEligibleMobs = TomfooleryHelper.getNearbyMobs(player, true);
//                    System.out.println("Nearby mobs eligible to cause tomfoolery: " + nearbyEligibleMobs);
                    if (nearbyEligibleMobs.size() >= TomfooleryHelper.NUM_NEARBY_MOBS) {
                        boolean anyScallywagSpawned = false;

                        // shuffled version of the list of spawnable scallywags
                        // as a result, n UNIQUE scallywags will be spawned
                        ArrayList<EntityType<?>> scallywagsToSpawn = TomfooleryHelper.getScallywagsToSpawn();

                        for (int i = 0; i < TomfooleryHelper.NUM_MOBS_TO_SUMMON; i++) {
                            try {
                                EntityType<?> mobType = scallywagsToSpawn.get(i);
                                if (TomfooleryHelper.spawnScallywag(player, mobType)) anyScallywagSpawned = true;
                            } catch (InvocationTargetException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        if (anyScallywagSpawned) {
                            System.out.println("TOMFOOLERY ENSUES!");
                            for (Mob mob : nearbyEligibleMobs) {
                                // nearby eligible mobs no longer become eligible to cause any more tomfoolery
                                TomfooleryHelper.revokeEligibility(mob);
                            }
                            tomfooleryCooldownCounter.resetCounter();
                        }

                    }
                }
                else tomfooleryCooldownCounter.incrementCounter();
            });
        }
    }

    // scallywag tick update
    @SubscribeEvent
    public static void scallywagTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Mob mob && !mob.level.isClientSide()) {
            LivingEntity target = mob.getTarget();
            if (target != null) {
                double distance = mob.distanceTo(target);
                mob.getCapability(ModCapabilities.TOMFOOLERY_SCALLYWAG_CAPABILITY).ifPresent(iTomfooleryScallywag -> {
                    TomfooleryScallywag tomfooleryScallywag = (TomfooleryScallywag) iTomfooleryScallywag;
                    if (tomfooleryScallywag.isScallywag() && distance < 3) {
                        // a large cloud of dust appears during the tomfoolery
                        // that is, when the scallywags are fighting the player or each other
                        TomfooleryHelper.dustCloudBetween(mob, target);

                        tomfooleryScallywag.incrementCounter();
                        if (tomfooleryScallywag.counter >= tomfooleryScallywag.limit) {
                            Utilities.playSound(mob, ModSounds.TOMFOOLERY.get(), 2.5F);
                            tomfooleryScallywag.resetCounter();
                            tomfooleryScallywag.rollLimit();
                        }
                    }
                });
            }
        }
    }

    // special case for slimes and magma cubes because they cannot normally damage any mobs besides players and iron golems
    @SubscribeEvent
    public static void slimeMagmaCubeDamage(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Slime slime && !slime.level.isClientSide()) { // MagmaCube extends Slime
            slime.getCapability(ModCapabilities.TOMFOOLERY_SCALLYWAG_CAPABILITY).ifPresent(iTomfooleryScallywag -> {
                TomfooleryScallywag tomfooleryScallywag = (TomfooleryScallywag) iTomfooleryScallywag;
                if (tomfooleryScallywag.isScallywag()) {
                    // TODO make this less CPU intensive
                    // 3*.6 - 0.5202*(2^(3-1))/2
                    AABB aabb = slime.getBoundingBox();
                    int size = slime.getSize();
                    double offset = size * 0.6 - aabb.getXsize() * Math.pow(2, (size - 1)) / 2;
                    List<Entity> list = slime.level.getEntities(slime, aabb.inflate(offset));
                    for (Entity entity : list) {
                        if (entity instanceof LivingEntity livingEntity && livingEntity.equals(slime.getTarget())) {
//                            dealDamageMethod.setAccessible(true);
                            try {
                                dealDamageMethod.invoke(slime, livingEntity);
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
