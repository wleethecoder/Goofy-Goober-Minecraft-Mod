package com.leecrafts.goofygoober.client.keys;

import com.leecrafts.goofygoober.GoofyGoober;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public class KeyInit {

    public static KeyMapping toggleSkedaddleKeyMapping;

    public static void init() {
        toggleSkedaddleKeyMapping = registerKey("toggle_skedaddle_key", KeyMapping.CATEGORY_GAMEPLAY, InputConstants.KEY_V);
    }

    private static KeyMapping registerKey(String name, String category, int keycode) {
        final var key = new KeyMapping("key." + GoofyGoober.MOD_ID + "." + name, keycode, category);
        ClientRegistry.registerKeyBinding(key);
        return key;
    }

}
