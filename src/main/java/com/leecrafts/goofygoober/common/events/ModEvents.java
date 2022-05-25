package com.leecrafts.goofygoober.common.events;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.effects.ModEffects;
import com.leecrafts.goofygoober.common.sounds.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof Player player && !player.level.isClientSide()) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.DEATH.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public static void hallucinate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player && !player.level.isClientSide() && player.getActiveEffectsMap() != null) {
            // to prevent the effect from being applied every tick
            boolean isSoHungryThatItCouldEatAHorse = player.getFoodData().getFoodLevel() < 10;
            boolean isHallucinating = player.hasEffect(ModEffects.HALLUCINATING.get());
            if (isSoHungryThatItCouldEatAHorse && !isHallucinating) {
                player.addEffect(new MobEffectInstance(ModEffects.HALLUCINATING.get(), 20 * 1000000));
            }
            else if (!isSoHungryThatItCouldEatAHorse && isHallucinating && !player.isCreative()) {
                player.removeEffect(ModEffects.HALLUCINATING.get());
            }
        }
    }

}
