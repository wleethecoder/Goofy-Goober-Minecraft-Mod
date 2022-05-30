package com.leecrafts.goofygoober.common.packets;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.common.packets.key.ServerboundPlayerWKeyPacket;
import com.leecrafts.goofygoober.common.packets.skedaddle.ClientboundSkedaddlePacket;
import com.leecrafts.goofygoober.common.packets.skedaddle.ServerboundSkedaddleTogglePacket;
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
        INSTANCE.messageBuilder(ClientboundSkedaddlePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ClientboundSkedaddlePacket::encode).decoder(ClientboundSkedaddlePacket::new)
                .consumer(ClientboundSkedaddlePacket::handle).add();
        INSTANCE.messageBuilder(ServerboundSkedaddleTogglePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ServerboundSkedaddleTogglePacket::encode).decoder(ServerboundSkedaddleTogglePacket::new)
                .consumer(ServerboundSkedaddleTogglePacket::handle).add();
    }

}