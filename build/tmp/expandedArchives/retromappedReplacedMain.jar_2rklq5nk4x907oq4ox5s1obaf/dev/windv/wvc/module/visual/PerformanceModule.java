package dev.windv.wvc.module.visual;

import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.BooleanSetting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Performance Boost - 不要な描画を制限してFPSを稼ぐ
 */
public class PerformanceModule extends WVCModule {

    private final BooleanSetting fastRender;
    private final BooleanSetting noParticles;

    public PerformanceModule(boolean enabled) {
        super("Performance", enabled);
        this.addSetting(fastRender = new BooleanSetting("Fast Render", true));
        this.addSetting(noParticles = new BooleanSetting("No Particles", false));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!isEnabled()) return;

        Minecraft mc = Minecraft.func_71410_x();
        
        // Fast Render 簡易実装: パーティクルを最小限に設定
        if (fastRender.isEnabled()) {
            mc.field_71474_y.field_74362_aa = noParticles.isEnabled() ? 2 : 1; // 1: Decreased, 2: Minimal
        }
    }

    // パーティクル発生時のフックなどはRenderEventHandler側で実装
    public boolean isNoParticles() {
        return isEnabled() && noParticles.isEnabled();
    }
}
