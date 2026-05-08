package dev.windv.wvc.config;

import dev.windv.wvc.WVCMod;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * WVC 設定管理クラス
 * Forgeのconfigurationシステムを利用して設定を永続化する
 */
public class WVCConfig {

    private final Configuration config;

    // HUDのデフォルト設定
    private static final String CATEGORY_HUD = "hud";

    // 各モジュールのON/OFF状態
    private boolean fpsEnabled;
    private boolean cpsEnabled;
    private boolean pingEnabled;

    // HUDの位置
    private int hudX;
    private int hudY;

    // HUDの透明度（0.0〜1.0）
    private float hudOpacity;

    // クライアント全体のアクセントカラー
    private int themeColor;

    // 現在選択されているプロファイル名
    private String selectedProfile;

    // OptiFine警告の表示設定
    private boolean showOptiFineWarning;

    // 言語設定 (en / jp)
    private String language;

    public WVCConfig(File configFile) {
        config = new Configuration(configFile);
    }

    /**
     * 設定ファイルを読み込む
     */
    public void load() {
        try {
            config.load();

            // HUDモジュールのON/OFF
            fpsEnabled  = config.getBoolean("fps_enabled",  CATEGORY_HUD, true,  "FPS表示を有効にする");
            cpsEnabled  = config.getBoolean("cps_enabled",  CATEGORY_HUD, true,  "CPS表示を有効にする");
            pingEnabled = config.getBoolean("ping_enabled", CATEGORY_HUD, true,  "Ping表示を有効にする");

            // HUD位置
            hudX = config.getInt("hud_x", CATEGORY_HUD, 2, 0, 10000, "HUDのX座標");
            hudY = config.getInt("hud_y", CATEGORY_HUD, 2, 0, 10000, "HUDのY座標");

            // 透明度
            hudOpacity = config.getFloat("hud_opacity", CATEGORY_HUD, 0.75f, 0.0f, 1.0f, "HUDの不透明度");

            // テーマカラー (デフォルト: Blue)
            themeColor = config.getInt("theme_color", "client", 0xFF0078D4, Integer.MIN_VALUE, Integer.MAX_VALUE, "クライアント全体のアクセントカラー");

            // プロファイル名
            selectedProfile = config.getString("selected_profile", "client", "Default", "現在選択されているプロファイル名");

            // OptiFine警告表示
            showOptiFineWarning = config.getBoolean("show_optifine_warning", "client", true, "OptiFineが未導入の際に警告を表示する");

            // 言語設定
            language = config.getString("language", "client", "en", "クライアントの表示言語 (en / jp)");

        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
        WVCMod.LOGGER.info("[WVC] 設定を読み込みました");
    }

    /**
     * 設定を保存する
     */
    public void save() {
        config.getCategory(CATEGORY_HUD).get("fps_enabled").set(fpsEnabled);
        config.getCategory(CATEGORY_HUD).get("cps_enabled").set(cpsEnabled);
        config.getCategory(CATEGORY_HUD).get("ping_enabled").set(pingEnabled);
        config.getCategory(CATEGORY_HUD).get("hud_x").set(hudX);
        config.getCategory(CATEGORY_HUD).get("hud_y").set(hudY);
        config.getCategory(CATEGORY_HUD).get("hud_opacity").set(hudOpacity);
        config.get("client", "theme_color", 0xFF0078D4).set(themeColor);
        config.get("client", "selected_profile", "Default").set(selectedProfile);
        config.get("client", "show_optifine_warning", true).set(showOptiFineWarning);
        config.get("client", "language", "jp").set(language);
        config.save();
    }

    // ゲッター / セッター
    public boolean isFpsEnabled()  { return fpsEnabled; }
    public boolean isCpsEnabled()  { return cpsEnabled; }
    public boolean isPingEnabled() { return pingEnabled; }
    public int getHudX()           { return hudX; }
    public int getHudY()           { return hudY; }
    public float getHudOpacity()   { return hudOpacity; }

    public void setFpsEnabled(boolean v)  { fpsEnabled  = v; save(); }
    public void setCpsEnabled(boolean v)  { cpsEnabled  = v; save(); }
    public void setPingEnabled(boolean v) { pingEnabled = v; save(); }
    public void setHudX(int v)            { hudX = v; save(); }
    public void setHudY(int v)            { hudY = v; save(); }
    public void setHudOpacity(float v)    { hudOpacity = v; save(); }
    public int getThemeColor()            { return themeColor; }
    public void setThemeColor(int v)      { themeColor = v; save(); }
    public String getSelectedProfile()    { return selectedProfile; }
    public void setSelectedProfile(String v) { selectedProfile = v; save(); }

    public boolean isShowOptiFineWarning() { return showOptiFineWarning; }
    public void setShowOptiFineWarning(boolean v) { showOptiFineWarning = v; save(); }

    public String getLanguage() { return language; }
    public void setLanguage(String v) { language = v; save(); }
}
