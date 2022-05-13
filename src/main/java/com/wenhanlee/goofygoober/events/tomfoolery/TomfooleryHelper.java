package com.wenhanlee.goofygoober.events.tomfoolery;

import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.eligibility.TomfooleryEligibility;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class TomfooleryHelper {

    ArrayList<EntityType<?>> scallywags;

    final double HORIZONTAL_RADIUS = 4.0;
    final double VERTICAL_RADIUS = 2.0;
    final int NUM_NEARBY_MOBS = 3;
    final int NUM_MOBS_TO_SUMMON = 5;
    final int NUM_SPAWN_ATTEMPTS = 10;

    public TomfooleryHelper() {
        scallywags = new ArrayList<>();
        // 12 monsters that can spawn during tomfoolery
        scallywags.add(EntityType.DROWNED);
        scallywags.add(EntityType.HUSK);
        scallywags.add(EntityType.SKELETON);
        scallywags.add(EntityType.STRAY);
        scallywags.add(EntityType.WITHER_SKELETON);
        scallywags.add(EntityType.SPIDER);
        scallywags.add(EntityType.CAVE_SPIDER);
        scallywags.add(EntityType.GUARDIAN);
        scallywags.add(EntityType.ZOGLIN);
        scallywags.add(EntityType.MAGMA_CUBE);
        scallywags.add(EntityType.SLIME);
        scallywags.add(EntityType.WITCH);
    }

    private EntityType<?> getRandom(ArrayList<EntityType<?>> scallywags) {
        Random random = new Random();
        return scallywags.get(random.nextInt(scallywags.size()));
    }

    public ArrayList<Mob> getNearbyEligibleMonsters(Player player) {
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
        ArrayList<Mob> eligibleMonsters = new ArrayList<>();
        for (Entity entity : list) {

            // maybe add more to the if statement (i.e. slime, magma cube)
            if (entity instanceof Monster monster) {
                AtomicBoolean isEligible = new AtomicBoolean(true);
                monster.getCapability(ModCapabilities.TOMFOOLERY_ELIGIBILITY_CAPABILITY).ifPresent(iTomfooleryEligibility -> {
                    TomfooleryEligibility tomfooleryEligibility = (TomfooleryEligibility) iTomfooleryEligibility;
                    isEligible.set(tomfooleryEligibility.getEligibility());
                });
                if (isEligible.get()) eligibleMonsters.add(monster);
            }
        }
        return eligibleMonsters;
    }

    public boolean spawnScallywag(Player player) {
        boolean success = false;
        ServerLevel serverLevel = (ServerLevel) player.level;
        int j = 0;
        while (!success && j < NUM_SPAWN_ATTEMPTS) {
            double spawnX = serverLevel.random.nextInt((int) HORIZONTAL_RADIUS * 2) - (int) HORIZONTAL_RADIUS;
//            double spawnY = serverLevel.random.nextInt((int) VERTICAL_RADIUS * 2) - (int) VERTICAL_RADIUS;
            double spawnZ = serverLevel.random.nextInt((int) HORIZONTAL_RADIUS * 2) - (int) HORIZONTAL_RADIUS;
            BlockPos blockPos = spawnBlockPos(player, spawnX, spawnZ);
            if (blockPos != null) {
                EntityType<?> mobType = getRandom(scallywags);
                Mob mob = (Mob) mobType.create(serverLevel, null, null, null, blockPos, MobSpawnType.MOB_SUMMONED, false, false);
                if (mob != null) {
                    if (mob.checkSpawnObstruction(serverLevel)) {
                        serverLevel.addFreshEntityWithPassengers(mob);
                        revokeEligibility(mob);
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

    public BlockPos spawnBlockPos(Player player, double spawnX, double spawnZ) {
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

    public void revokeEligibility(Mob mob) {
        mob.getCapability(ModCapabilities.TOMFOOLERY_ELIGIBILITY_CAPABILITY).ifPresent(iTomfooleryEligibility -> {
            TomfooleryEligibility tomfooleryEligibility = (TomfooleryEligibility) iTomfooleryEligibility;
            tomfooleryEligibility.setEligibility(false);
        });
    }

}
