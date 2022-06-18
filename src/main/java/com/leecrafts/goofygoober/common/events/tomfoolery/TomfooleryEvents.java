package com.leecrafts.goofygoober.common.events.tomfoolery;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.livingentity.TomfooleryLivingEntity;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.livingentity.TomfooleryLivingEntityProvider;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.player.ITomfooleryPlayer;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.player.TomfooleryPlayer;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.player.TomfooleryPlayerProvider;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.mob.ITomfooleryMob;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.mob.TomfooleryMob;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.mob.TomfooleryMobProvider;
import com.leecrafts.goofygoober.common.misc.Utilities;
import com.leecrafts.goofygoober.common.sounds.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TomfooleryEvents {

    // dealDamage -> m_33637_
    private static final Method dealDamageMethod = ObfuscationReflectionHelper.findMethod(Slime.class, "m_33637_", LivingEntity.class);

    public TomfooleryEvents() {
        dealDamageMethod.setAccessible(true);
    }

    // Tomfoolery is simply when a mob gets into a fight with a player or any living entity.
    // A cloud of dust appears, and random goofy sound effects play.
    // If the player is near 3 or more hostile/monster mobs, 5 different hostile/monster mobs spawn around the player, adding to the chaos.
    // Summoned mobs can attack each other.

    // As a result, it can become very disorienting to both the eyes and ears. Be careful.

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ITomfooleryPlayer.class);
        event.register(ITomfooleryMob.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEventPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player && !player.getCommandSenderWorld().isClientSide()) {
            TomfooleryPlayerProvider tomfooleryPlayerProvider = new TomfooleryPlayerProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "tomfoolery_cooldown_counter"), tomfooleryPlayerProvider);
//            event.addListener(tomfooleryPlayerProvider::invalidate);
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEventMob(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Mob mob && !mob.getCommandSenderWorld().isClientSide()) {
            TomfooleryMobProvider tomfooleryMobProvider = new TomfooleryMobProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "is_eligible_to_summon_nearby_mobs"), tomfooleryMobProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "summoned"), tomfooleryMobProvider);
            event.addListener(tomfooleryMobProvider::invalidate);
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEventLivingEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity livingEntity && !livingEntity.getCommandSenderWorld().isClientSide()) {
            TomfooleryLivingEntityProvider tomfooleryLivingEntityProvider = new TomfooleryLivingEntityProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "num_attackers"), tomfooleryLivingEntityProvider);
            if (!(livingEntity instanceof Player)) {
                event.addListener(tomfooleryLivingEntityProvider::invalidate);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
//        if (event.getEntity() instanceof Mob mob && !mob.level.isClientSide()) {
//            // iron golems and illagers/raiders are scallywags by default
//            if ((mob instanceof IronGolem
//                    || mob instanceof AbstractIllager
//                    || mob instanceof Ravager)) {
//                TomfooleryHelper.mobIsSummoned(mob);
//            }
//            // there is a 20% chance for any hostile/monster mob to be a scallywag
//            else if (TomfooleryHelper.isHostileAndOrMonster(mob)) {
//                if (Utilities.random.nextInt(5) == 0) TomfooleryHelper.mobIsSummoned(mob);
//            }

        // To make it fairer, smaller slimes and magma cubes are ineligible to summon nearby mobs by default
        // MagmaCube extends Slime
        if (event.getEntity() instanceof Slime slime && !slime.level.isClientSide() && slime.getSize() <= 2) {
            TomfooleryHelper.revokeEligibility(slime);
        }
//        }
    }

    // player tick update
    @SubscribeEvent
    public static void playerTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player && !player.level.isClientSide()) {
            player.getCapability(ModCapabilities.TOMFOOLERY_PLAYER_CAPABILITY).ifPresent(iTomfooleryPlayer -> {
                TomfooleryPlayer tomfooleryPlayer = (TomfooleryPlayer) iTomfooleryPlayer;
                if (tomfooleryPlayer.counter >= tomfooleryPlayer.LIMIT) {
                    ArrayList<Mob> nearbyEligibleMobs = TomfooleryHelper.getNearbyMobs(player, true);
                    if (nearbyEligibleMobs.size() >= TomfooleryHelper.NUM_NEARBY_MOBS) {
                        boolean anyMobsSpawned = false;

                        // shuffled version of the spawnableMobs list
                        // as a result, n UNIQUE mobs will be spawned
                        ArrayList<EntityType<?>> mobsToSpawn = TomfooleryHelper.getMobsToSpawn();

                        for (int i = 0; i < TomfooleryHelper.NUM_MOBS_TO_SUMMON; i++) {
                            try {
                                EntityType<?> mobType = mobsToSpawn.get(i);
                                if (TomfooleryHelper.spawnMob(player, mobType)) anyMobsSpawned = true;
                            } catch (InvocationTargetException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        if (anyMobsSpawned) {
                            for (Mob mob : nearbyEligibleMobs) {
                                // nearby eligible mobs no longer become eligible to summon any more mobs
                                TomfooleryHelper.revokeEligibility(mob);
                            }
                            tomfooleryPlayer.resetCounter();
                        }
                        // if no mobs spawned successfully, then the cooldown will be 1 minute instead of 2
                        else tomfooleryPlayer.counter = tomfooleryPlayer.LIMIT / 2;

                    }
                }
                else tomfooleryPlayer.incrementCounter();
            });
        }
    }

    // mob tick update
    @SubscribeEvent
    public static void mobTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Mob mob && !mob.level.isClientSide()) {
            LivingEntity target = mob.getTarget();
            if (target != null && !target.isRemoved()) {
                mob.getCapability(ModCapabilities.TOMFOOLERY_MOB_CAPABILITY).ifPresent(iTomfooleryMob -> {
                    TomfooleryMob tomfooleryMob = (TomfooleryMob) iTomfooleryMob;
                    target.getCapability(ModCapabilities.TOMFOOLERY_LIVING_ENTITY_CAPABILITY).ifPresent(iTomfooleryLivingEntity -> {
                        TomfooleryLivingEntity tomfooleryLivingEntity = (TomfooleryLivingEntity) iTomfooleryLivingEntity;
                        if (mob.distanceTo(target) < tomfooleryLivingEntity.TOMFOOLERY_RANGE) {
                            // see line 197
                            if (tomfooleryLivingEntity.numAttackers < tomfooleryLivingEntity.MAX_NUM_ATTACKERS || tomfooleryLivingEntity.alreadyTargetedByMob(mob)) {
                                if (!tomfooleryLivingEntity.alreadyTargetedByMob(mob)) {
                                    tomfooleryLivingEntity.attackers.add(mob);
                                    tomfooleryLivingEntity.numAttackers++;
                                }

                                // a large cloud of dust appears during tomfoolery
                                // that is, when mobs are fighting the player or other living entities
                                TomfooleryHelper.dustCloudBetween(mob, target);

                                // goofy noises
                                tomfooleryMob.incrementCounter();
                                if (tomfooleryMob.counter >= tomfooleryMob.limit) {
                                    Utilities.playSound(mob, ModSounds.TOMFOOLERY.get());
                                    tomfooleryMob.resetCounter();
                                    tomfooleryMob.rollLimit();
                                }
                            }
                        }
                    });
                });
            }
        }
    }

    // For each living entity, a limit is placed on how many attacking mobs can make dust clouds and goofy noises.
    // This is to prevent lag. Imagine that there is no limit, and you are in an enderman farm!
    @SubscribeEvent
    public static void livingEntityTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();
        if (!livingEntity.level.isClientSide()) {
            livingEntity.getCapability(ModCapabilities.TOMFOOLERY_LIVING_ENTITY_CAPABILITY).ifPresent(iTomfooleryLivingEntity -> {
                TomfooleryLivingEntity tomfooleryLivingEntity = (TomfooleryLivingEntity) iTomfooleryLivingEntity;
                // using Iterator to avoid ConcurrentModificationExceptions
                Iterator<Mob> it = tomfooleryLivingEntity.attackers.iterator();
                while (it.hasNext()) {
                    Mob attacker = it.next();
                    if (attacker.isRemoved()
                            || attacker.getTarget() == null
                            || !attacker.getTarget().is(livingEntity)
                            || attacker.distanceTo(livingEntity) >= tomfooleryLivingEntity.TOMFOOLERY_RANGE) {
                        it.remove();
                        tomfooleryLivingEntity.numAttackers--;
                    }
                }
            });
        }
    }

    // special case for slimes and magma cubes because they cannot normally damage any mobs besides players and iron golems
    @SubscribeEvent
    public static void slimeMagmaCubeDamage(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Slime slime && !slime.level.isClientSide()) { // MagmaCube extends Slime
            slime.getCapability(ModCapabilities.TOMFOOLERY_MOB_CAPABILITY).ifPresent(iTomfooleryMob -> {
                TomfooleryMob tomfooleryMob = (TomfooleryMob) iTomfooleryMob;
                if (tomfooleryMob.isSummoned()) {
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
