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
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID)
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
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_charge_counter"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_charging"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_takeoff"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "w_pressed"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_should_animate_on_client"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "in_water"), skedaddleProvider);
        }
    }

    @SubscribeEvent
    public static void skedaddle(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player && !player.level.isClientSide()) {
            player.getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                Skedaddle skedaddle = (Skedaddle) iSkedaddle;
                SkedaddleHelper.waterCheck(player, skedaddle);
                if (skedaddle.skedaddleEnabled && !skedaddle.alreadyInWater) {
                    if (!skedaddle.skedaddleTakeoff && skedaddle.wPressed) {
                        if (skedaddle.skedaddleCharging) {
                            // player runs in place
//                            player.teleportTo(player.getX(), player.getY(), player.getZ()); // <-- too uncomfortably jittery

                            skedaddle.incrementCounter();
                            if (skedaddle.skedaddleChargeCounter >= skedaddle.skedaddleChargeLimit) {
                                SkedaddleHelper.applySpeed(player, skedaddle);
                                SkedaddleHelper.removeSlowness(player, skedaddle);
                                skedaddle.skedaddleTakeoff = true;
                                Utilities.playSound(player, ModSounds.PLAYER_TAKEOFF.get());
                            }
                        } else if (player.isSprinting()) {
                            // player starts charging (skedaddleCharging gets set to true in sendClientBoundPacket)
                            SkedaddleHelper.applySlowness(player, skedaddle);
                            Utilities.playSound(player, ModSounds.PLAYER_SKEDADDLE.get());
                            skedaddle.sendClientBoundPacket(player, true, true);
                        }
                    }
                    else if (skedaddle.skedaddleTakeoff && skedaddle.shouldAnimateOnClient) {
                        if (player.getActiveEffectsMap() != null && !player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                            skedaddle.sendClientBoundPacket(player, false, false);
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
                skedaddle.sendClientBoundPacket(target, skedaddle.skedaddleCharging, skedaddle.shouldAnimateOnClient);
            });
        }
    }

}
