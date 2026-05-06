package dev.windv.wvc.module.render;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * FullBright モジュール
 * 常に明るさを最大(gamma = 100)にします。
 */
public class FullBrightModule extends WVCModule {
    
    private float originalGamma;

    public FullBrightModule() {
        super("FullBright", false); // デフォルトはOFF
    }

    @Override
    public void onEnable() {
        originalGamma = Minecraft.getMinecraft().gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.gammaSetting = originalGamma;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.isEnabled()) {
            Minecraft.getMinecraft().gameSettings.gammaSetting = 100.0f;
        }
    }
}
