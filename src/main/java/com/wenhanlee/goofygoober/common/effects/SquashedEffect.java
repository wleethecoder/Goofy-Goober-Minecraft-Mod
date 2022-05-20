package com.wenhanlee.goofygoober.common.effects;

import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class SquashedEffect extends MobEffect {

    public SquashedEffect(MobEffectCategory mobEffectCategory, int color) { super(mobEffectCategory, color); }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void addAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        MobEffectInstance mobEffectInstance = pLivingEntity.getEffect(ModEffects.SQUASHED.get());
        if (mobEffectInstance != null) {
            PacketDistributor.TRACKING_ENTITY.with(() -> pLivingEntity).send(
                    new ClientboundUpdateMobEffectPacket(pLivingEntity.getId(), mobEffectInstance)
            );
        }
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        PacketDistributor.TRACKING_ENTITY.with(() -> pLivingEntity).send(
                new ClientboundRemoveMobEffectPacket(pLivingEntity.getId(), ModEffects.SQUASHED.get())
        );
//        MinecraftForge.EVENT_BUS.post(new EntityEvent.Size(pLivingEntity, pLivingEntity.getPose(), pLivingEntity.getDimensions(pLivingEntity.getPose()), pLivingEntity.getEyeHeight()));
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

}
