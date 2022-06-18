package com.leecrafts.goofygoober.common.events.tomfoolery;

import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.mob.TomfooleryMob;
import com.leecrafts.goofygoober.common.misc.Utilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TomfooleryHelper {

    // list of mobs that can spawn
    private static ArrayList<EntityType<?>> summonableMobs;

    private static final double HORIZONTAL_RADIUS = 4;
    private static final double VERTICAL_RADIUS = 2;
    public static final int NUM_NEARBY_MOBS = 3;
    public static final int NUM_MOBS_TO_SUMMON = 5;
    private static final int NUM_SPAWN_ATTEMPTS = 10;

    // setSize -> m_7839_
    private static final Method setSizeMethod = ObfuscationReflectionHelper.findMethod(Slime.class, "m_7839_", int.class, boolean.class);

    public TomfooleryHelper() {
        setSizeMethod.setAccessible(true);
    }

    private static void initializeIfNull() {
        if (summonableMobs == null) {
            summonableMobs = new ArrayList<>();
            // 7 mobs that can spawn during tomfoolery
            summonableMobs.add(EntityType.WITHER_SKELETON);
            summonableMobs.add(EntityType.SPIDER);
            summonableMobs.add(EntityType.CAVE_SPIDER);
            summonableMobs.add(EntityType.MAGMA_CUBE);
            summonableMobs.add(EntityType.SLIME);
            summonableMobs.add(EntityType.POLAR_BEAR);
            summonableMobs.add(EntityType.ZOMBIFIED_PIGLIN);
        }
    }

    // returns a shuffled version of the summonableMobs list so that a random sample can be drawn
    public static ArrayList<EntityType<?>> getMobsToSpawn() {
        initializeIfNull();
        ArrayList<EntityType<?>> mobsToSpawn = new ArrayList<>(summonableMobs);
        Collections.shuffle(mobsToSpawn);
        return mobsToSpawn;
    }

    // returns a list of hostile/monster mobs near the player
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
        ArrayList<Mob> mobs = new ArrayList<>();
        for (Entity entity : list) {

            if (entity instanceof Mob mob && isHostileAndOrMonster(mob)) {
                if (mustBeEligible) {
                    AtomicBoolean isEligibleToSummonNearbyMobs = new AtomicBoolean(true);
                    mob.getCapability(ModCapabilities.TOMFOOLERY_MOB_CAPABILITY).ifPresent(iTomfooleryMob -> {
                        TomfooleryMob tomfooleryMob = (TomfooleryMob) iTomfooleryMob;
                        isEligibleToSummonNearbyMobs.set(tomfooleryMob.isEligibleToSummonNearbyMobs());
                    });
                    if (isEligibleToSummonNearbyMobs.get()) mobs.add(mob);
                }
                else mobs.add(mob);
            }
        }
        return mobs;
    }

    // makes an attempt to spawn a mob
    // returns whether the spawn attempt is successful
    public static boolean spawnMob(Player player, EntityType<?> mobType) throws InvocationTargetException, IllegalAccessException {
        boolean success = false;
        ServerLevel serverLevel = (ServerLevel) player.level;
        int j = 0;
        while (!success && j < NUM_SPAWN_ATTEMPTS) {
            double spawnX = serverLevel.random.nextInt((int) HORIZONTAL_RADIUS * 2) - (int) HORIZONTAL_RADIUS;
//            double spawnY = serverLevel.random.nextInt((int) VERTICAL_RADIUS * 2) - (int) VERTICAL_RADIUS;
            double spawnZ = serverLevel.random.nextInt((int) HORIZONTAL_RADIUS * 2) - (int) HORIZONTAL_RADIUS;
            BlockPos blockPos = spawnBlockPos(player, spawnX, spawnZ);
            if (blockPos != null) {
                Mob mob = (Mob) mobType.create(serverLevel, null, null, null, blockPos, MobSpawnType.MOB_SUMMONED, false, false);
                if (mob != null) {
                    if (mob.checkSpawnObstruction(serverLevel)) {

                        setMobStats(mob);

                        serverLevel.addFreshEntityWithPassengers(mob);

                        mobIsSummoned(mob);

                        // summoned mobs themselves cannot summon more mobs
                        revokeEligibility(mob);

                        // summoned mobs will target both the player and any nearby hostile/monster mob
                        ArrayList<Mob> nearbyMobs = getNearbyMobs(player, false);
//                        int indexOfSelf = nearbyMobs.indexOf(mob);
//                        int mobIndex = Utilities.random.nextInt(nearbyMobs.size() - 1);
//                        // make sure the mob would not target itself
//                        mobIndex = mobIndex >= indexOfSelf ? mobIndex + 1 : mobIndex;
//                        Mob targetMob = nearbyMobs.get(mobIndex);
//                        mob.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(mob, targetMob.getClass(), true));

                        // 20% chance that the mob will prioritize targeting the player over other nearby mobs
                        boolean prioritizeMobsOverPlayers = Utilities.random.nextInt(5) >= 1;
                        for (Mob mob1: nearbyMobs) {
                            if (!mob.is(mob1)) mob.targetSelector.addGoal(prioritizeMobsOverPlayers ? 1 : 2, new NearestAttackableTargetGoal<>(mob, mob1.getClass(), false));
                        }

                        // Trust me, nobody wants zombified piglins to aggro towards players--especially when unprovoked.
                        if (!(mob instanceof ZombifiedPiglin) || player.level.dimension() != Level.NETHER) mob.targetSelector.addGoal(prioritizeMobsOverPlayers ? 2 : 1, new NearestAttackableTargetGoal<>(mob, Player.class, true));

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
        mob.getCapability(ModCapabilities.TOMFOOLERY_MOB_CAPABILITY).ifPresent(iTomfooleryMob -> {
            TomfooleryMob tomfooleryMob = (TomfooleryMob) iTomfooleryMob;
            tomfooleryMob.setEligibility(false);
        });
    }

    public static void mobIsSummoned(Mob mob) {
        mob.getCapability(ModCapabilities.TOMFOOLERY_MOB_CAPABILITY).ifPresent(iTomfooleryMob -> {
            TomfooleryMob tomfooleryMob = (TomfooleryMob) iTomfooleryMob;
            tomfooleryMob.setSummoned(true);
        });
    }

    public static void dustCloudBetween(Mob attacker, LivingEntity target) {
        // makes it somewhat less visually disorienting to players
        int particleCount = target instanceof Player ? 1 : 4;

        double x = (attacker.getX() + target.getX()) / 2;
        double y = (attacker.getY() + target.getY()) / 2 + 1;
        double z = (attacker.getZ() + target.getZ()) / 2;

        ServerLevel level = (ServerLevel) attacker.level;
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z,
                particleCount, 1.25, 1, 1.25, 0);
        level.sendParticles(ParticleTypes.CRIT, x, y, z,
                particleCount, 1.25, 1, 1.25, 0.7);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z,
                particleCount, 1.25, 1, 1.25, 1);
    }

    // returns if this is the type of mob that the player must be near in order to cause tomfoolery
    // basically all hostiles and monsters
    // ...and polar bears too
    public static boolean isHostileAndOrMonster(Mob mob) {
        return mob instanceof Monster
                || mob.getType() == EntityType.SLIME
                || mob.getType() == EntityType.MAGMA_CUBE
                || mob.getType() == EntityType.SHULKER
                || mob.getType() == EntityType.HOGLIN
                || mob.getType() == EntityType.POLAR_BEAR;
    }

    public static void setMobStats(Mob mob) throws InvocationTargetException, IllegalAccessException {

        if (mob instanceof Spider spider) { // CaveSpider extends Spider
            spider.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1000000));
            spider.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1000000));
        }

        // summoned Slimes and Magma cubes are always size 3
        if (mob instanceof Slime slime) { // MagmaCube extends Slime
//            method.setAccessible(true);
            setSizeMethod.invoke(slime, 4, true);
        }

//        if (mob instanceof PolarBear || mob instanceof ZombifiedPiglin) {
//            mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1000000));
//        }

    }

}
