package com.leecrafts.goofygoober.common.effects;

import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class FatEffect extends MobEffect {

    public FatEffect(MobEffectCategory mobEffectCategory, int color) { super(mobEffectCategory, color); }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) { return true; }

    @Override
    public void addAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        if (!pLivingEntity.level.isClientSide()) {
            MobEffectInstance mobEffectInstance = pLivingEntity.getEffect(ModEffects.FAT.get());
            if (mobEffectInstance != null) {
                PacketDistributor.TRACKING_ENTITY.with(() -> pLivingEntity).send(
                        new ClientboundUpdateMobEffectPacket(pLivingEntity.getId(), mobEffectInstance)
                );
            }
//            pLivingEntity.refreshDimensions();
        }
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        if (!pLivingEntity.level.isClientSide()) {
            PacketDistributor.TRACKING_ENTITY.with(() -> pLivingEntity).send(
                    new ClientboundRemoveMobEffectPacket(pLivingEntity.getId(), ModEffects.FAT.get())
            );
//            pLivingEntity.refreshDimensions();
        }
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

}
