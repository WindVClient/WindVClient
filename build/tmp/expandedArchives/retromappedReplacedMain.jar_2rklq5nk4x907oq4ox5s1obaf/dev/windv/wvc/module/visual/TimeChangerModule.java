package dev.windv.wvc.module.visual;

import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * TimeChanger モジュール
 * ゲーム内の時間をローカルで変更します。
 */
public class TimeChangerModule extends WVCModule {

    private final SliderSetting time;

    public TimeChangerModule() {
        super("TimeChanger", false);
        this.addSetting(time = new SliderSetting("Time", 12000, 0, 24000, true));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (this.isEnabled() && Minecraft.func_71410_x().field_71441_e != null) {
            // サーバーからのパケットに勝つために、毎ティック強制的にセット
            Minecraft.func_71410_x().field_71441_e.func_72877_b((long) time.getValue());
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (this.isEnabled() && Minecraft.func_71410_x().field_71441_e != null) {
            // 描画直前にもセットすることでチラつきを抑える
            Minecraft.func_71410_x().field_71441_e.func_72877_b((long) time.getValue());
        }
    }
}
