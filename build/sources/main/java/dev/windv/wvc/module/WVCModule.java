package dev.windv.wvc.module;

import dev.windv.wvc.settings.KeybindSetting;
import dev.windv.wvc.settings.Setting;
import java.util.ArrayList;
import java.util.List;

/**
 * WVCモジュールの基底クラス
 * すべてのHUDモジュールはこのクラスを継承する。
 * ON/OFF切り替えとメタデータを提供する。
 */
public abstract class WVCModule {

    // モジュール名（内部識別用キー）
    private final String name;

    // 有効/無効フラグ
    private boolean enabled;

    // モジュール設定
    private final List<Setting> settings = new ArrayList<>();

    // HUD位置（EditHUD用）
    private int x = 5;
    private int y = 5;
    private final KeybindSetting keybind;

    /**
     * コンストラクタ
     * @param name    モジュール名 (内部キー)
     * @param enabled 初期状態（ON/OFF）
     */
    public WVCModule(String name, boolean enabled) {
        this.name    = name;
        this.enabled = enabled;
        this.keybind = new KeybindSetting(0);
        this.addSetting(keybind);
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    /**
     * モジュールを有効化した際に呼ばれる
     */
    public void onEnable() {}

    /**
     * モジュールを無効化した際に呼ばれる
     */
    public void onDisable() {}

    public KeybindSetting getKeybind() { return keybind; }

    // ゲッター / セッター
    public String getName()    { 
        // 日本語化は一旦保留とし、元の名前を返す
        return name; 
    }
    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) onEnable();
            else         onDisable();
        }
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    public void addSetting(Setting s) {
        this.settings.add(s);
    }

    public List<Setting> getSettings() {
        return settings;
    }
}
