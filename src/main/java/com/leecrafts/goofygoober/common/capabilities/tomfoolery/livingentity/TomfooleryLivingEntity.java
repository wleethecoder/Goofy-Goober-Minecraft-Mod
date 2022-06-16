package com.leecrafts.goofygoober.common.capabilities.tomfoolery.livingentity;

import net.minecraft.world.entity.Mob;

import java.util.ArrayList;

public class TomfooleryLivingEntity implements ITomfooleryLivingEntity {

    public int numAttackers;
    public ArrayList<Mob> attackers;
    public final int MAX_NUM_ATTACKERS;
    public final float TOMFOOLERY_RANGE;

    public TomfooleryLivingEntity() {
        this.numAttackers = 0;
        this.attackers = new ArrayList<>();
        this.MAX_NUM_ATTACKERS = 3;
        this.TOMFOOLERY_RANGE = 3;
    }

    @Override
    public boolean alreadyTargetedByMob(Mob mob) {
        if (attackers.isEmpty()) return false;
        for (Mob attacker : attackers) if (attacker.is(mob)) return true;
        return false;
    }
}
