package com.leecrafts.goofygoober.common.packets;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.packets.skedaddle.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(GoofyGoober.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private PacketHandler() {}

    public static void init() {
        int index = 0;
        INSTANCE.messageBuilder(ServerboundSkedaddleWKeyPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ServerboundSkedaddleWKeyPacket::encode).decoder(ServerboundSkedaddleWKeyPacket::new)
                .consumerMainThread(ServerboundSkedaddleWKeyPacket::handle).add();
        INSTANCE.messageBuilder(ClientboundSkedaddlePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundSkedaddlePacket::encode).decoder(ClientboundSkedaddlePacket::new)
                .consumerMainThread(ClientboundSkedaddlePacket::handle).add();
        INSTANCE.messageBuilder(ServerboundSkedaddleTogglePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ServerboundSkedaddleTogglePacket::encode).decoder(ServerboundSkedaddleTogglePacket::new)
                .consumerMainThread(ServerboundSkedaddleTogglePacket::handle).add();
        INSTANCE.messageBuilder(ServerboundSkedaddleBumpPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ServerboundSkedaddleBumpPacket::encode).decoder(ServerboundSkedaddleBumpPacket::new)
                .consumerMainThread(ServerboundSkedaddleBumpPacket::handle).add();
        INSTANCE.messageBuilder(ClientboundSoundPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundSoundPacket::encode).decoder(ClientboundSoundPacket::new)
                .consumerMainThread(ClientboundSoundPacket::handle).add();
    }

}