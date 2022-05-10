package com.wenhanlee.goofygoober.packets;

import com.wenhanlee.goofygoober.GoofyGoober;
import com.wenhanlee.goofygoober.packets.mobSize.ClientboundMobSizeUpdatePacket;
import com.wenhanlee.goofygoober.packets.mobSize.ServerboundMobSizeUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GoofyGoober.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private PacketHandler() {
    }

    public static void init() {
        int index = 0;
//        INSTANCE.messageBuilder(ServerboundMobSizeUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
//                .encoder(ServerboundMobSizeUpdatePacket::encode).decoder(ServerboundMobSizeUpdatePacket::new)
//                .consumer(ServerboundMobSizeUpdatePacket::handle).add();
        INSTANCE.messageBuilder(ClientboundMobSizeUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundMobSizeUpdatePacket::encode).decoder(ClientboundMobSizeUpdatePacket::new)
                .consumer(ClientboundMobSizeUpdatePacket::handle).add();
    }

}
