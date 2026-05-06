package dev.windv.wvc.module.visual;

import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.ColorSetting;

/**
 * HitColor モジュール
 * ダメージを受けた際の色をカスタマイズします。
 */
public class HitColorModule extends WVCModule {

    public final ColorSetting color;

    public HitColorModule() {
        super("HitColor", false);
        this.addSetting(color = new ColorSetting("Color", 255, 0, 0)); // デフォルト赤
    }
    
    // このモジュールの値は、Entityのレンダリングをフックしている箇所で参照されます。
}
