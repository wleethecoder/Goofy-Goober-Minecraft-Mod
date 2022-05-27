package com.leecrafts.goofygoober.common.packets;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.packets.key.ServerboundPlayerWKeyPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GoofyGoober.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private PacketHandler() {}

    public static void init() {
        int index = 0;
        INSTANCE.messageBuilder(ServerboundPlayerWKeyPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ServerboundPlayerWKeyPacket::encode).decoder(ServerboundPlayerWKeyPacket::new)
                .consumer(ServerboundPlayerWKeyPacket::handle).add();
    }

}