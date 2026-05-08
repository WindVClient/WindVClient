package dev.windv.wvc.util;

import dev.windv.wvc.WVCMod;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private static final Map<String, Map<String, String>> translations = new HashMap<>();

    static {
        // --- English (en) ---
        Map<String, String> en = new HashMap<>();
        en.put("gui.mods", "MODS");
        en.put("gui.settings", "SETTINGS");
        en.put("gui.profiles", "PROFILES");
        en.put("gui.search", "Search...");
        en.put("gui.edit_hud", "Edit HUD");
        en.put("gui.back", "< Back");
        en.put("gui.details", "Details >");
        en.put("gui.global_settings", "Global Settings");
        en.put("gui.theme_color", "Theme Color");
        en.put("gui.language", "Language Settings");
        en.put("gui.lang_jp", "Japanese");
        en.put("gui.lang_en", "English");
        
        en.put("mod.fps", "FPS Display");
        en.put("mod.cps", "CPS Display");
        en.put("mod.ping", "Ping Display");
        en.put("mod.keystrokes", "Keystrokes");
        en.put("mod.armor", "Armor Status");
        en.put("mod.potion", "Potion Status");
        en.put("mod.reach", "Reach Display");
        en.put("mod.direction", "Direction HUD");
        en.put("mod.scoreboard", "Scoreboard");
        en.put("mod.location", "Location");
        en.put("mod.team", "Team Display");
        en.put("mod.timechanger", "Time Changer");
        en.put("mod.itemphysics", "Item Physics");
        en.put("mod.blockoverlay", "Block Overlay");
        en.put("mod.hitcolor", "Hit Color");
        en.put("mod.autoclicker", "Auto Clicker");
        en.put("mod.chatmod", "Chat Customizer");
        
        translations.put("en", en);

        // --- Japanese (jp) ---
        Map<String, String> jp = new HashMap<>();
        jp.put("gui.mods", "\u30e2\u30c3\u30c9"); // モッド
        jp.put("gui.settings", "\u8a2d\u5b9a"); // 設定
        jp.put("gui.profiles", "\u30d7\u30ed\u30d5\u30a1\u30a4\u30eb"); // プロファイル
        jp.put("gui.search", "\u691c\u7d22..."); // 検索...
        jp.put("gui.edithud", "HUD\u7de8\u96c6"); // HUD編集
        jp.put("gui.back", "< \u623b\u308b"); // 戻る
        jp.put("gui.details", "\u8a73\u7d30 >"); // 詳細 >
        jp.put("gui.global_settings", "\u5168\u4f53\u8a2d\u5b9a"); // 全体設定
        jp.put("gui.theme_color", "\u30c6\u30fc\u30de\u30ab\u30e9\u30fc"); // テーマカラー
        jp.put("gui.language", "\u8a00\u8a9e\u8a2d\u5b9a"); // 言語設定
        jp.put("gui.lang_jp", "\u65e5\u672c\u8a9e"); // 日本語
        jp.put("gui.lang_en", "\u82f1\u8a9e"); // 英語
        
        jp.put("mod.fps", "FPS\u8868\u793a");
        jp.put("mod.cps", "CPS\u8868\u793a");
        jp.put("mod.ping", "Ping\u8868\u793a");
        jp.put("mod.keystrokes", "\u30ad\u30fc\u30b9\u30c8\u30ed\u30fc\u30af");
        jp.put("mod.armor", "\u88c5\u5099\u8010\u4e45\u5024");
        jp.put("mod.potion", "\u30dd\u30fc\u30b7\u30e7\u30f3\u52b9\u679c");
        jp.put("mod.reach", "\u30ea\u30fc\u30c1\u8868\u793a");
        jp.put("mod.direction", "\u65b9\u89d2HUD");
        jp.put("mod.scoreboard", "\u30b9\u30b3\u30a2\u30dc\u30fc\u30c9");
        jp.put("mod.location", "\u73fe\u5728\u5730\u8868\u793a");
        jp.put("mod.team", "\u30c1\u30fc\u30e0\u8868\u793a");
        jp.put("mod.timechanger", "\u6642\u9593\u5909\u66f4");
        jp.put("mod.itemphysics", "\u30a2\u30a4\u30c6\u30e0\u7269\u7406");
        jp.put("mod.blockoverlay", "\u30d6\u30ed\u30c3\u30af\u67a0");
        jp.put("mod.hitcolor", "\u30d2\u30c3\u30c8\u30ab\u30e9\u30fc");
        jp.put("mod.autoclicker", "\u30aa\u30fc\u30c8\u30af\u30ea\u30c3\u30ab\u30fc");
        jp.put("mod.chatmod", "\u30c1\u30e3\u30c3\u30c8\u30ab\u30b9\u30bf\u30e0");

        translations.put("jp", jp);
    }

    public static String get(String key) {
        String lang = WVCMod.INSTANCE.getConfig().getLanguage();
        Map<String, String> map = translations.getOrDefault(lang, translations.get("en"));
        return map.getOrDefault(key, key);
    }
}
