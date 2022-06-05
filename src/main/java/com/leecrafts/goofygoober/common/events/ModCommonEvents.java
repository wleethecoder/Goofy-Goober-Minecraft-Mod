package com.leecrafts.goofygoober.common.events;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.entities.ModEntities;
import com.leecrafts.goofygoober.common.entities.SteakEntity;
import com.leecrafts.goofygoober.common.packets.PacketHandler;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCommonEvents {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) { event.enqueueWork(PacketHandler::init); }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.STEAK_ENTITY.get(), SteakEntity.createMobAttributes().build());
    }

}
