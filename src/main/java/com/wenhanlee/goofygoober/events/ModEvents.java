package com.wenhanlee.goofygoober.events;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.sounds.ModSounds;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void screamingPain(LivingDamageEvent event) {
        Entity entity = event.getEntity();
        if (!entity.level.isClientSide()) {
            String source = event.getSource().getMsgId();
            float yMovement = 1.5F;
            if (source.equals("cactus") || source.equals("inFire") || source.equals("lava")) {
                if (source.equals("inFire")) yMovement = 2.5F;
                if (source.equals("lava")) yMovement = 5.0F;
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, yMovement, 0.0D));
//                entity.playSound(ModSoundEvents.TOM_SCREAM.get(), 1.0F, 1.0F);
            }
            else entity.playSound(ModSounds.TOM_SCREAM.get(), 1.0F, 1.0F);
        }
    }

}
