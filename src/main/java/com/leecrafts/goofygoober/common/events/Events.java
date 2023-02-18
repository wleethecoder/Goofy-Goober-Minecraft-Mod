package com.leecrafts.goofygoober.common.events;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.tomfoolery.player.TomfooleryPlayer;
import com.leecrafts.goofygoober.common.effects.ModEffects;
import com.leecrafts.goofygoober.common.misc.Utilities;
import com.leecrafts.goofygoober.common.sounds.ModSounds;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// miscellaneous events
@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Events {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof Player player && !player.level.isClientSide()) {
            Utilities.playSound(player, "fail");
        }
    }

    @SubscribeEvent
    public static void hallucinate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player && !player.level.isClientSide() && player.getActiveEffectsMap() != null) {
            // to prevent the effect from being applied every tick
            boolean isSoHungryThatItCouldEatAHorse = player.getFoodData().getFoodLevel() < 10;
            boolean isHallucinating = player.hasEffect(ModEffects.HALLUCINATING.get());
            if (isSoHungryThatItCouldEatAHorse && !isHallucinating) {
                player.addEffect(new MobEffectInstance(ModEffects.HALLUCINATING.get(), 1000000 * 20, 0, false, false));
            }
            else if (!isSoHungryThatItCouldEatAHorse && isHallucinating && !player.isCreative()) {
                player.removeEffect(ModEffects.HALLUCINATING.get());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        Player originalPlayer = event.getOriginal();
        Player player = event.getPlayer();
        originalPlayer.reviveCaps();
        originalPlayer.getCapability(ModCapabilities.TOMFOOLERY_PLAYER_CAPABILITY).ifPresent(iTomfooleryPlayer -> {
            player.getCapability(ModCapabilities.TOMFOOLERY_PLAYER_CAPABILITY).ifPresent(iTomfooleryPlayer1 -> {
                TomfooleryPlayer tomfooleryPlayer = (TomfooleryPlayer) iTomfooleryPlayer;
                TomfooleryPlayer tomfooleryPlayer1 = (TomfooleryPlayer) iTomfooleryPlayer1;
                tomfooleryPlayer1.counter = tomfooleryPlayer.counter;
//                System.out.println((tomfooleryPlayer1.LIMIT - tomfooleryPlayer1.counter) / 20.0 + " seconds left");
            });
        });
        originalPlayer.invalidateCaps();
    }

}
