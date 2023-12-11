package com.leecrafts.goofygoober.client.events.skedaddle;

import com.leecrafts.goofygoober.GoofyGoober;
import com.leecrafts.goofygoober.client.keys.KeyBinding;
import com.leecrafts.goofygoober.common.capabilities.ModCapabilities;
import com.leecrafts.goofygoober.common.capabilities.skedaddle.Skedaddle;
import com.leecrafts.goofygoober.common.packets.PacketHandler;
import com.leecrafts.goofygoober.common.packets.skedaddle.ServerboundSkedaddleBumpPacket;
import com.leecrafts.goofygoober.common.packets.skedaddle.ServerboundSkedaddleWKeyPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = GoofyGoober.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SkedaddleClientEvents {

    public static boolean skedaddleEnabled;
    private static boolean skedaddleToggleKeyAlreadyPressed = false;

    private static boolean alreadyCollided = false;
    private static final float WHAM_MAX_ANGLE = 30;

    @SubscribeEvent
    public static void initializeSkedaddleDisabledOnLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        SkedaddleClientHelper.sendServerboundPacket(false);
    }

    @SubscribeEvent
    public static void initializeSkedaddleDisabledOnRespawn(ClientPlayerNetworkEvent.Clone event) {
        SkedaddleClientHelper.sendServerboundPacket(false);
    }

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(KeyBinding.TOGGLE_KEY);
    }

    @SubscribeEvent
    public static void toggleSkedaddle(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer != null) {
                boolean pressed = KeyBinding.TOGGLE_KEY.isDown();
                if (pressed && !skedaddleToggleKeyAlreadyPressed) {
                    skedaddleToggleKeyAlreadyPressed = true;
                    SkedaddleClientHelper.sendServerboundPacket(!skedaddleEnabled);
                    String message = skedaddleEnabled ? "Skedaddle enabled" : "Skedaddle disabled";
//                    localPlayer.displayClientMessage(new TextComponent(message), true);
                    localPlayer.displayClientMessage(Component.literal(message), true);
                }
                else if (!pressed && skedaddleToggleKeyAlreadyPressed) {
                    skedaddleToggleKeyAlreadyPressed = false;
                }
            }
        }
    }

    @SubscribeEvent
    public static void startOrStopSkedaddle(InputEvent.Key event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer != null) {
            if (event.getKey() == InputConstants.KEY_W && event.getAction() == GLFW.GLFW_PRESS) {
//                PacketHandler.INSTANCE.sendToServer(new ServerboundSkedaddleWKeyPacket(true));
                PacketHandler.INSTANCE.send(new ServerboundSkedaddleWKeyPacket(true), PacketDistributor.SERVER.noArg());
            }
            else if (event.getKey() == InputConstants.KEY_W && event.getAction() == GLFW.GLFW_RELEASE) {
//                PacketHandler.INSTANCE.sendToServer(new ServerboundSkedaddleWKeyPacket(false));
                PacketHandler.INSTANCE.send(new ServerboundSkedaddleWKeyPacket(false), PacketDistributor.SERVER.noArg());
            }
        }
    }

    @SubscribeEvent
    public static void skedaddleAnimation(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
        if (event.getEntity() instanceof Player player) {
            player.getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                Skedaddle skedaddle = (Skedaddle) iSkedaddle;
                if (skedaddle.shouldAnimateOnClient) {
                    player.walkAnimation.setSpeed(3.0f);
                }
            });
        }
    }

    @SubscribeEvent
    public static void cancelFOVChangeUponSkedaddleCharging(ComputeFovModifierEvent event) {
        if (event.getNewFovModifier() < 1) {
            event.getPlayer().getCapability(ModCapabilities.SKEDADDLE_CAPABILITY).ifPresent(iSkedaddle -> {
                Skedaddle skedaddle = (Skedaddle) iSkedaddle;
                if (skedaddle.charging) event.setNewFovModifier(1);
            });
        }
    }

    // for some reason, Entity#horizontalCollision only changes client-side when the player is on the ground
    @SubscribeEvent
    public static void skedaddleWham(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;
            if (localPlayer != null) {
                // no need to send a packet every tick
                if (localPlayer.horizontalCollision && !alreadyCollided) {
                    alreadyCollided = true;
                    float angleMod90 = Math.abs(localPlayer.getYRot()) % 90;
                    Vec3 deltaMovement = localPlayer.getDeltaMovement();
                    Direction direction = localPlayer.getDirection();
                    boolean correctAngle = angleMod90 < WHAM_MAX_ANGLE || angleMod90 > 90 - WHAM_MAX_ANGLE;
                    boolean headOn = ((direction == Direction.NORTH || direction == Direction.SOUTH) && deltaMovement.z() == 0)
                            || ((direction == Direction.WEST || direction == Direction.EAST) && deltaMovement.x() == 0);
                    if (correctAngle && headOn) {
//                        PacketHandler.INSTANCE.sendToServer(new ServerboundSkedaddleBumpPacket(true));
                        PacketHandler.INSTANCE.send(new ServerboundSkedaddleBumpPacket(true), PacketDistributor.SERVER.noArg());
                    }
                }
                else if (!localPlayer.horizontalCollision && alreadyCollided) {
                    alreadyCollided = false;
                }
            }
        }
    }

}
