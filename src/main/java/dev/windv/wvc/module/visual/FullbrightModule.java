package dev.windv.wvc.module.visual;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;

/**
 * Fullbright - 暗い場所でも明るく表示する
 */
public class FullbrightModule extends WVCModule {

    private float oldGamma;

    public FullbrightModule(boolean enabled) {
        super("Fullbright", enabled);
    }

    @Override
    public void onEnable() {
        oldGamma = Minecraft.getMinecraft().gameSettings.gammaSetting;
        Minecraft.getMinecraft().gameSettings.gammaSetting = 1000f;
    }

    @Override
    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.gammaSetting = oldGamma;
    }
}
