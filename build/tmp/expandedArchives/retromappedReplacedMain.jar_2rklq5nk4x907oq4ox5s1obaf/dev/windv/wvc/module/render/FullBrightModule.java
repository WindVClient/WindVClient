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
        originalGamma = Minecraft.func_71410_x().field_71474_y.field_74333_Y;
    }

    @Override
    public void onDisable() {
        Minecraft.func_71410_x().field_71474_y.field_74333_Y = originalGamma;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.isEnabled()) {
            Minecraft.func_71410_x().field_71474_y.field_74333_Y = 100.0f;
        }
    }
}
