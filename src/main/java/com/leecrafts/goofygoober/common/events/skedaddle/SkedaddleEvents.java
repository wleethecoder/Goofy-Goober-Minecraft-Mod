package com.leecrafts.goofygoober.common.events.skedaddle;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.ISkedaddle;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.Skedaddle;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.SkedaddleProvider;
import com.leecrafts.goofygoober.common.misc.Utilities;
import com.leecrafts.goofygoober.common.sounds.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID)
public class SkedaddleEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ISkedaddle.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEventCooldownCounter(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player && !player.getCommandSenderWorld().isClientSide()) {
            SkedaddleProvider skedaddleProvider = new SkedaddleProvider();
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_charge_counter"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_charging"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "skedaddle_takeoff"), skedaddleProvider);
            event.addCapability(new ResourceLocation(GoofyGoober.MOD_ID, "w_pressed"), skedaddleProvider);
        }
    }

    @SubscribeEvent
    public static void skedaddle(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player && !player.level.isClientSide()) {
            player.getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                Skedaddle skedaddle = (Skedaddle) iSkedaddle;
                if (true/*skedaddle enabled is toggled on*/) {
                    if (!skedaddle.skedaddleTakeoff && skedaddle.wPressed) {
                        if (skedaddle.skedaddleCharging) {
                            // player runs in place
                            player.teleportTo(player.getX(), player.getY(), player.getZ());

                            skedaddle.incrementCounter();
                            if (skedaddle.skedaddleChargeCounter >= skedaddle.skedaddleChargeLimit) {
                                if (player.getActiveEffectsMap() != null) {
                                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, skedaddle.skedaddleDuration, 1));
                                }
                                skedaddle.skedaddleTakeoff = true;
                                Utilities.playSound(player, ModSounds.PLAYER_TAKEOFF.get());
                            }
                        } else if (player.isSprinting()) {
                            skedaddle.skedaddleCharging = true;
                            Utilities.playSound(player, ModSounds.PLAYER_SKEDADDLE.get());
                        }
                    }
                } else {
                    skedaddle.reset();
                }
            });
        }
    }

}
