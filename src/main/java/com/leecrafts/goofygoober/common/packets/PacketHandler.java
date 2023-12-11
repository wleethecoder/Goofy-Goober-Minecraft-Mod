package com.leecrafts.goofygoober.common.packets;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.packets.skedaddle.*;
import io.netty.util.AttributeKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.*;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final ResourceLocation MAIN = new ResourceLocation(GoofyGoober.MOD_ID, "main");
    public static final AttributeKey<ForgePacketHandler> CONTEXT = AttributeKey.newInstance(MAIN.toString());

//    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
//            new ResourceLocation(GoofyGoober.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
//            PROTOCOL_VERSION::equals);
    public static final SimpleChannel INSTANCE = ChannelBuilder
        .named(MAIN)
        .optional()
        .networkProtocolVersion(0)
        .simpleChannel();

//        .messageBuilder(ServerboundSkedaddleWKeyPacket.class, NetworkDirection.PLAY_TO_SERVER)
//        .decoder(ServerboundSkedaddleWKeyPacket::new)
//        .encoder(ServerboundSkedaddleWKeyPacket::encode)
//        .consumerMainThread(ServerboundSkedaddleWKeyPacket::handle)
//        .add()
//
//        .messageBuilder(ClientboundSkedaddlePacket.class, NetworkDirection.PLAY_TO_CLIENT)
//        .decoder(ClientboundSkedaddlePacket::new)
//        .encoder(ClientboundSkedaddlePacket::encode)
//        .consumerMainThread(ClientboundSkedaddlePacket::handle)
//        .add()
//
//        .messageBuilder(ServerboundSkedaddleTogglePacket.class, NetworkDirection.PLAY_TO_SERVER)
//        .decoder(ServerboundSkedaddleTogglePacket::new)
//        .encoder(ServerboundSkedaddleTogglePacket::encode)
//        .consumerMainThread(ServerboundSkedaddleTogglePacket::handle)
//        .add()
//
//        .messageBuilder(ServerboundSkedaddleBumpPacket.class, NetworkDirection.PLAY_TO_SERVER)
//        .decoder(ServerboundSkedaddleBumpPacket::new)
//        .encoder(ServerboundSkedaddleBumpPacket::encode)
//        .consumerMainThread(ServerboundSkedaddleBumpPacket::handle)
//        .add()
//
//        .messageBuilder(ClientboundSoundPacket.class, NetworkDirection.PLAY_TO_CLIENT)
//        .decoder(ClientboundSoundPacket::new)
//        .encoder(ClientboundSoundPacket::encode)
//        .consumerMainThread(ClientboundSoundPacket::handle)
//        .add();

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