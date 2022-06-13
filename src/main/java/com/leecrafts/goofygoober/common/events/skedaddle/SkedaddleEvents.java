package com.leecrafts.goofygoober.common.events.skedaddle;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.ISkedaddle;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.Skedaddle;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.SkedaddleProvider;
import com.leecrafts.goofygoober.common.misc.Utilities;
import com.leecrafts.goofygoober.common.sounds.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SkedaddleEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ISkedaddle.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEventCooldownCounter(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            SkedaddleProvider skedaddleProvider = new SkedaddleProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_enabled"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_counter"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_charging"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_takeoff"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_finished"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_devious_walk"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "w_pressed"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_should_animate_on_client"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "in_water"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "wham"), skedaddleProvider);
        }
    }

    @SubscribeEvent
    public static void skedaddle(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player && !player.level.isClientSide()) {
            player.getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                Skedaddle skedaddle = (Skedaddle) iSkedaddle;
                // cooldown after running into wall
                if (skedaddle.wham) {
                    skedaddle.incrementCounter();
                    if (skedaddle.counter >= skedaddle.WHAM_COOLDOWN_LIMIT) {
                        skedaddle.wham = false;
                        skedaddle.reset(player);
                    }
                }
                else {
                    SkedaddleHelper.waterCheck(player, skedaddle);
                    if (skedaddle.enabled && !skedaddle.inWater && skedaddle.wPressed && !skedaddle.finished) {
                        if (!skedaddle.takeoff) {
                            if (skedaddle.charging) {
                                // player runs in place while charging
//                            player.teleportTo(player.getX(), player.getY(), player.getZ()); // <-- too uncomfortably jittery

                                SkedaddleHelper.dustParticles(player);
                                skedaddle.incrementCounter();
                                if (skedaddle.counter >= skedaddle.CHARGE_LIMIT) {
                                    SkedaddleHelper.applySpeed(player, skedaddle);
                                    SkedaddleHelper.removeSlowness(player, skedaddle);
                                    skedaddle.takeoff = true;
                                    Utilities.playSound(player, ModSounds.PLAYER_TAKEOFF.get());
                                }
                            }
                            else if (player.isSprinting()) {
                                // player starts charging (skedaddleCharging gets set to true in sendClientBoundPacket)
                                SkedaddleHelper.applySlowness(player, skedaddle);
                                Utilities.playSound(player, ModSounds.PLAYER_SKEDADDLE.get());
                                skedaddle.sendClientboundPacket(player, true, true);
                            }
                        }
                        else {
                            // skedaddling

                            if (!player.isSprinting()) {
                                skedaddle.reset(player);
                            }
                            else {
                                // devious walk
                                if (player.isShiftKeyDown()) {
                                    if (!skedaddle.deviousWalk) skedaddle.deviousWalk = true;
                                    skedaddle.incrementCounter();
                                    if (skedaddle.counter >= skedaddle.PLAYER_SNEAK_AMBIENT_DURATION) {
                                        Utilities.playSound(player, ModSounds.PLAYER_SNEAK.get(), 1, 1);
                                        skedaddle.counter = 0;
                                    }
                                }
                                else skedaddle.deviousWalk = false;

                                SkedaddleHelper.dustParticles(player);

                                // takes up more hunger than usual
                                player.getFoodData().addExhaustion(0.01F);

                                // when speed effect wears off, player is no longer skedaddling
                                if (player.getActiveEffectsMap() != null && !player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                                    skedaddle.takeoff = false;
                                    skedaddle.finished = true;
                                    skedaddle.deviousWalk = false;
                                    skedaddle.sendClientboundPacket(player, false, false);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerTrackingEvent(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof Player target && !target.level.isClientSide()) {
            target.getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                Skedaddle skedaddle = (Skedaddle) iSkedaddle;
                skedaddle.sendClientboundPacket(target, skedaddle.charging, skedaddle.shouldAnimateOnClient);
            });
        }
    }

    // When the player is sneaking while skedaddling, it can evade an enemy's detection, even when the enemy had already targeted the player
    // The player just needs be at least 4 blocks from the enemy
    @SubscribeEvent
    public static void deviousWalk(LivingSetAttackTargetEvent event) {
        if (event.getEntityLiving() instanceof Mob mob && event.getTarget() instanceof Player player && !player.level.isClientSide()) {
            player.getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                Skedaddle skedaddle = (Skedaddle) iSkedaddle;
                if (skedaddle.deviousWalk && player.distanceTo(mob) >= 4) {
                    mob.setTarget(null);
                }
            });
        }
    }

}
