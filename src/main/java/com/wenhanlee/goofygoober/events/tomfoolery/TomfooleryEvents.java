package com.wenhanlee.goofygoober.events.tomfoolery;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.capabilities.ModCapabilities;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.counter.ITomfooleryCounter;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.counter.TomfooleryCounter;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.counter.TomfooleryCounterProvider;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.eligibility.TomfooleryEligibility;
import com.wenhanlee.goofygoober.capabilities.tomfoolery.eligibility.TomfooleryEligibilityProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID)
public class TomfooleryEvents {

    static TomfooleryHelper tomfooleryHelper = new TomfooleryHelper();

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ITomfooleryCounter.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEventCounter(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player && !event.getObject().getCommandSenderWorld().isClientSide()) {
            TomfooleryCounterProvider tomfooleryCounterProvider = new TomfooleryCounterProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "tomfoolery_counter"), tomfooleryCounterProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "tomfoolery_cooldown"), tomfooleryCounterProvider);
            event.addListener(tomfooleryCounterProvider::invalidate);
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEventEligibility(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Mob && !event.getObject().getCommandSenderWorld().isClientSide()) {
            TomfooleryEligibilityProvider tomfooleryEligibilityProvider = new TomfooleryEligibilityProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "is_eligible_to_cause_tomfoolery"), tomfooleryEligibilityProvider);
            event.addListener(tomfooleryEligibilityProvider::invalidate);
        }
    }

    @SubscribeEvent
    public static void onScallywagSpawnEvent(LivingSpawnEvent.AllowDespawn event) {
        if (event.getEntityLiving() instanceof Mob mob && !mob.level.isClientSide()) {
            mob.getCapability(ModCapabilities.TOMFOOLERY_ELIGIBILITY_CAPABILITY).ifPresent(iTomfooleryEligibility -> {
                TomfooleryEligibility tomfooleryEligibility = (TomfooleryEligibility) iTomfooleryEligibility;
                if (!tomfooleryEligibility.getEligibility()) event.setResult(Event.Result.DENY);
            });
        }
    }

    // When the player is around 3 or more monster mobs (which include Endermen and Zombified Piglins), unbridled tomfoolery ensues
    @SubscribeEvent
    public static void tick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player && !player.level.isClientSide()) {
            player.getCapability(ModCapabilities.TOMFOOLERY_COUNTER_CAPABILITY).ifPresent(iTomfooleryCounter -> {
                TomfooleryCounter tomfooleryCounter = (TomfooleryCounter) iTomfooleryCounter;
                if (!tomfooleryCounter.cooldown) {
                    ArrayList<Mob> eligibleMonsters = tomfooleryHelper.getNearbyEligibleMonsters(player);
//                    System.out.println("Monsters eligible to cause tomfoolery: " + eligibleMonsters);
                    if (eligibleMonsters.size() >= tomfooleryHelper.NUM_NEARBY_MOBS) {
                        System.out.println("TOMFOOLERY ENSUES!");

                        for (Mob mob : eligibleMonsters) {
                            tomfooleryHelper.revokeEligibility(mob);
                        }

                        boolean anyScallywagSpawned = false;
                        for (int i = 0; i < tomfooleryHelper.NUM_MOBS_TO_SUMMON; i++) {
                            if (tomfooleryHelper.spawnScallywag(player)) anyScallywagSpawned = true;
                        }

                        if (anyScallywagSpawned) tomfooleryCounter.cooldown = true;

                    }
                }
                else {
                    tomfooleryCounter.incrementCounter();
                    if (tomfooleryCounter.counter >= tomfooleryCounter.limit) {
                        tomfooleryCounter.resetCounter();
                        tomfooleryCounter.cooldown = false;
                    }
                }
            });
        }
    }

}
