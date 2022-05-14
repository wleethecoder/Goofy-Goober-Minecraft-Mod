package com.wenhanlee.goofygoober.packets;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.packets.fat.ClientboundFatPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GoofyGoober.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private PacketHandler() {}

    public static void init() {
        int index = 0;
        INSTANCE.messageBuilder(ClientboundFatPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundFatPacket::encode).decoder(ClientboundFatPacket::new)
                .consumer(ClientboundFatPacket::handle).add();
    }

}
