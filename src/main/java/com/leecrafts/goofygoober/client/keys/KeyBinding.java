package com.leecrafts.goofygoober.client.keys;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {

    public static final String KEY_TOGGLE_SKEDADDLE = "key.goofygoober.toggle_skedaddle_key";

    public static final KeyMapping TOGGLE_KEY = new KeyMapping(KEY_TOGGLE_SKEDADDLE, KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, KeyMapping.CATEGORY_GAMEPLAY);

    // oudated part
//    public static KeyMapping toggleSkedaddleKeyMapping;
//
//    public static void init() {
//        toggleSkedaddleKeyMapping = registerKey("toggle_skedaddle_key", KeyMapping.CATEGORY_GAMEPLAY, InputConstants.KEY_V);
//    }
//
//    private static KeyMapping registerKey(String name, String category, int keycode) {
//        final var key = new KeyMapping("key." + GoofyGoober.MOD_ID + "." + name, keycode, category);
//        ClientRegistry.registerKeyBinding(key);
//        return key;
//    }

}
