package dev.windv.wvc.settings;

/**
 * モード選択設定（複数の選択肢から一つを選択）
 */
public class ModeSetting extends Setting {
    private final String[] modes;
    private int index;

    public ModeSetting(String name, int defaultIndex, String... modes) {
        super(name);
        this.modes = modes;
        this.index = defaultIndex;
    }

    public String getMode() {
        return modes[index];
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void cycle() {
        index = (index + 1) % modes.length;
    }

    public String[] getModes() {
        return modes;
    }
}
