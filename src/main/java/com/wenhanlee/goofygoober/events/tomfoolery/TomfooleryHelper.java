package com.wenhanlee.goofygoober.events.tomfoolery;

import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.scallywag.TomfooleryScallywag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class TomfooleryHelper {

    private static ArrayList<EntityType<?>> scallywags;

    private static final double HORIZONTAL_RADIUS = 4.0;
    private static final double VERTICAL_RADIUS = 2.0;
    public static final int NUM_NEARBY_MOBS = 3;
    public static final int NUM_MOBS_TO_SUMMON = 5;
    private static final int NUM_SPAWN_ATTEMPTS = 10;

    private static void initializeIfNull() {
        if (scallywags == null) {
            scallywags = new ArrayList<>();
            // 7 monsters that can spawn during tomfoolery
            scallywags.add(EntityType.WITHER_SKELETON);
            scallywags.add(EntityType.SPIDER);
            scallywags.add(EntityType.CAVE_SPIDER);
            scallywags.add(EntityType.MAGMA_CUBE);
            scallywags.add(EntityType.SLIME);
            scallywags.add(EntityType.POLAR_BEAR);
            scallywags.add(EntityType.ZOMBIFIED_PIGLIN);
        }
    }

    private static EntityType<?> getRandomScallywag() {
        Random random = new Random();
        initializeIfNull();
        return scallywags.get(random.nextInt(scallywags.size()));
    }

    public static ArrayList<Mob> getNearbyMobs(Player player, boolean mustBeEligible) {
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        List<Entity> list = player.level.getEntities(player, new AABB(
                x + HORIZONTAL_RADIUS,
                y + VERTICAL_RADIUS,
                z + HORIZONTAL_RADIUS,
                x - HORIZONTAL_RADIUS,
                y - VERTICAL_RADIUS,
                z - HORIZONTAL_RADIUS
        ));
//        System.out.println(Arrays.toString(list.toArray()));
        ArrayList<Mob> mobs = new ArrayList<>();
        for (Entity entity : list) {

            if (entity instanceof Mob mob && isHostileAndOrMonster(mob)) {
                if (mustBeEligible) {
                    AtomicBoolean isEligible = new AtomicBoolean(true);
                    mob.getCapability(ModCapabilities.TOMFOOLERY_SCALLYWAG_CAPABILITY).ifPresent(iTomfooleryEligibility -> {
                        TomfooleryScallywag tomfooleryEligibility = (TomfooleryScallywag) iTomfooleryEligibility;
                        isEligible.set(tomfooleryEligibility.isEligible());
                    });
                    if (isEligible.get()) mobs.add(mob);
                }
                else mobs.add(mob);
            }
        }
        return mobs;
    }

    public static boolean spawnScallywag(Player player) {
        boolean success = false;
        ServerLevel serverLevel = (ServerLevel) player.level;
        int j = 0;
        while (!success && j < NUM_SPAWN_ATTEMPTS) {
            double spawnX = serverLevel.random.nextInt((int) HORIZONTAL_RADIUS * 2) - (int) HORIZONTAL_RADIUS;
//            double spawnY = serverLevel.random.nextInt((int) VERTICAL_RADIUS * 2) - (int) VERTICAL_RADIUS;
            double spawnZ = serverLevel.random.nextInt((int) HORIZONTAL_RADIUS * 2) - (int) HORIZONTAL_RADIUS;
            BlockPos blockPos = spawnBlockPos(player, spawnX, spawnZ);
            if (blockPos != null) {
                EntityType<?> mobType = getRandomScallywag();
                Mob mob = (Mob) mobType.create(serverLevel, null, null, null, blockPos, MobSpawnType.MOB_SUMMONED, false, false);
                if (mob != null) {
                    if (mob.checkSpawnObstruction(serverLevel)) {
                        serverLevel.addFreshEntityWithPassengers(mob);

                        // scallywags glow (which is convenient, considering all the smoke they generate)
                        mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1000000, 1));

                        youreAScallywag(mob);

                        // scallywags themselves cannot cause tomfoolery
                        revokeEligibility(mob);

                        // if the player is not nearby, then scallywags will target any monster (including other scallywags) that are near the player
                        ArrayList<Mob> nearbyMobs = getNearbyMobs(player, false);
//                        Random random = new Random();
//                        int indexOfSelf = nearbyMobs.indexOf(mob);
//                        int mobIndex = random.nextInt(nearbyMobs.size() - 1);
//                        // make sure the monster would not target itself
//                        mobIndex = mobIndex >= indexOfSelf ? mobIndex + 1 : mobIndex;
//                        Mob targetMob = nearbyMobs.get(mobIndex);
//                        mob.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(mob, targetMob.getClass(), true));
                        for (Mob mob1: nearbyMobs) {
                            mob.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(mob, mob1.getClass(), true));
                        }

                        mob.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(mob, player.getClass(), true));

                        success = true;
                    }
                    else {
                        mob.discard();
                    }
                }
            }

            j++;
        }

        return success;
    }

    public static BlockPos spawnBlockPos(Player player, double spawnX, double spawnZ) {
        BlockPos blockPos = player.blockPosition().offset(spawnX, VERTICAL_RADIUS, spawnZ);
        BlockState blockState = player.level.getBlockState(blockPos);
        boolean spawnPositionFound = false;
        int k = (int) VERTICAL_RADIUS;
        while (!spawnPositionFound && k >= (int) -VERTICAL_RADIUS) {
            BlockPos tempBlockPos = blockPos;
            BlockState tempBlockState = blockState;
            blockPos = blockPos.below();
            blockState = player.level.getBlockState(blockPos);
            if ((tempBlockState.isAir() || tempBlockState.getMaterial().isLiquid()) && blockState.getMaterial().isSolid()) {
                blockPos = tempBlockPos;
                spawnPositionFound = true;
            }
            k--;
        }
        return spawnPositionFound && blockPos != null ? blockPos : null;
    }

    public static void revokeEligibility(Mob mob) {
        mob.getCapability(ModCapabilities.TOMFOOLERY_SCALLYWAG_CAPABILITY).ifPresent(iTomfooleryEligibility -> {
            TomfooleryScallywag tomfooleryEligibility = (TomfooleryScallywag) iTomfooleryEligibility;
            tomfooleryEligibility.setEligibility(false);
        });
    }

    public static void youreAScallywag(Mob mob) {
        mob.getCapability(ModCapabilities.TOMFOOLERY_SCALLYWAG_CAPABILITY).ifPresent(iTomfooleryEligibility -> {
            TomfooleryScallywag tomfooleryEligibility = (TomfooleryScallywag) iTomfooleryEligibility;
            tomfooleryEligibility.setScallywag(true);
        });
    }

    public static void dustCloudBetween(LivingEntity entity1, LivingEntity entity2) {
        double x = (entity1.getX() + entity2.getX()) / 2;
        double y = (entity1.getY() + entity2.getY()) / 2 + 1;
        double z = (entity1.getZ() + entity2.getZ()) / 2;

        // this would only work on the client
//        entity1.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 1.0, 0.0, 0.0);

//        System.out.println("generating particles at " + x + ", " + y + ", " + z);
        ServerLevel level = (ServerLevel) entity1.level;
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z,
                4, 1.25, 1.0, 1.25, 0);
        level.sendParticles(ParticleTypes.CRIT, x, y, z,
                4, 1.25, 1.0, 1.25, 0.7);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z,
                4, 1.25, 1.0, 1.25, 1.0);
    }

    public static boolean isHostileAndOrMonster(Mob mob) {
        return mob instanceof Monster
                || mob.getType() == EntityType.SLIME
                || mob.getType() == EntityType.MAGMA_CUBE
                || mob.getType() == EntityType.SHULKER
                || mob.getType() == EntityType.HOGLIN;
    }

}
