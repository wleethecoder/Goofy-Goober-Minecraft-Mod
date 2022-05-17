package com.wenhanlee.goofygoober.effects;

import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class FatEffect extends MobEffect {

    public FatEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        double x = pLivingEntity.getX();
        double y = pLivingEntity.getY();
        double z = pLivingEntity.getZ();
        pLivingEntity.setBoundingBox(new AABB(x + 0.9, y + 1.8, z + 0.9, x - 0.9, y, z - 0.9));
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void addAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        if (pLivingEntity instanceof Player player) {
//            System.out.println(player.getDisplayName().getString() + " has gained the fat effect");
            MobEffectInstance mobEffectInstance = player.getEffect(ModEffects.FAT.get());
            if (mobEffectInstance != null) {
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player).send(
                        new ClientboundUpdateMobEffectPacket(player.getId(), mobEffectInstance)
                );
            }
        }
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    @Override
    public void removeAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        if (pLivingEntity instanceof Player player) {
//            System.out.println(player.getDisplayName().getString() + " has lost the fat effect");
            PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player).send(
                    new ClientboundRemoveMobEffectPacket(player.getId(), ModEffects.FAT.get())
            );
        }
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

}
