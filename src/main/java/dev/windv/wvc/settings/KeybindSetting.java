package dev.windv.wvc.settings;

import org.lwjgl.input.Keyboard;

/**
 * Keybind Setting
 * モジュールに割り当てるキーを管理します。
 */
public class KeybindSetting extends Setting {

    private int keyCode;

    public KeybindSetting(int defaultKey) {
        super("Keybind");
        this.keyCode = defaultKey;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public String getKeyName() {
        if (keyCode == 0) return "None";
        return Keyboard.getKeyName(keyCode);
    }
}
