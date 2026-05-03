package dev.windv.wvc.settings;

/**
 * カラー設定（RGB/HSBで調整可能）
 */
public class ColorSetting extends Setting {
    private int red, green, blue;

    public ColorSetting(String name, int r, int g, int b) {
        super(name);
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    public int getColor() {
        return (255 << 24) | ((red & 255) << 16) | ((green & 255) << 8) | (blue & 255);
    }

    public void setColor(int color) {
        this.red = (color >> 16) & 0xFF;
        this.green = (color >> 8) & 0xFF;
        this.blue = color & 0xFF;
    }

    public int getRed() { return red; }
    public void setRed(int red) { this.red = red; }

    public int getGreen() { return green; }
    public void setGreen(int green) { this.green = green; }

    public int getBlue() { return blue; }
    public void setBlue(int blue) { this.blue = blue; }
}
