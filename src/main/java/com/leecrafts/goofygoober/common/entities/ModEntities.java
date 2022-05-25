package com.leecrafts.goofygoober.common.entities;

import com.leecrafts.goofygoober.GoofyGoober;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITIES, GoofyGoober.MOD_ID);

    public static final RegistryObject<EntityType<SteakEntity>> STEAK_ENTITY = ENTITIES.register("steak",
            () -> EntityType.Builder.of(SteakEntity::new, MobCategory.MISC).sized(1, 2)
                    .build(new ResourceLocation(GoofyGoober.MOD_ID, "steak").toString()));

    public static void register(IEventBus eventBus) { ENTITIES.register(eventBus); }

}