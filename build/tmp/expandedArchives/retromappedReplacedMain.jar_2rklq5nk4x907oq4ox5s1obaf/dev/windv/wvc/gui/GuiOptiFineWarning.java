package dev.windv.wvc.gui;

import dev.windv.wvc.WVCMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import java.io.IOException;
import java.net.URI;

public class GuiOptiFineWarning extends GuiScreen {

    @Override
    public void func_73866_w_() {
        this.field_146292_n.clear();
        int w = 180;
        int h = 26;
        int startY = field_146295_m / 2 + 10;
        // 日本語をユニコード化: "Download", "\u4eca\u306f\u3044\u3044" (今はいい), "\u4eca\u5f8c\u8868\u793a\u3057\u306a\u3044" (今後表示しない)
        this.field_146292_n.add(new WVCWarningButton(1, field_146294_l / 2 - w / 2, startY, w, h, "Download"));
        this.field_146292_n.add(new WVCWarningButton(2, field_146294_l / 2 - w / 2, startY + 32, w, h, "\u4eca\u306f\u3044\u3044"));
        this.field_146292_n.add(new WVCWarningButton(3, field_146294_l / 2 - w / 2, startY + 64, w, h, "\u4eca\u5f8c\u8868\u793a\u3057\u306a\u3044"));
    }

    @Override
    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        this.func_73733_a(0, 0, field_146294_l, field_146295_m, 0xFF050505, 0xFF121212);
        
        // "OptiFine\u304c\u5c0e\u5165\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002" (OptiFineが導入されていません。)
        String line1 = "OptiFine\u304c\u5c0e\u5165\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002";
        // "\u5c0e\u5165\u3059\u308b\u3053\u3068\u3067\u8efd\u91cf\u5316\u3055\u308c\u308b\u306e\u3067\u63a8\u5968\u3057\u3066\u3044\u307e\u3059\u3002" (導入することで軽量化されるので推奨しています。)
        String line2 = "\u5c0e\u5165\u3059\u308b\u3053\u3068\u3067\u8efd\u91cf\u5316\u3055\u308c\u308b\u306e\u3067\u63a8\u5968\u3057\u3066\u3044\u307e\u3059\u3002";
        
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString(line1, field_146294_l / 2, field_146295_m / 2 - 50, 0xFF5555);
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString(line2, field_146294_l / 2, field_146295_m / 2 - 32, 0xBBBBBB);
        
        super.func_73863_a(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void func_146284_a(GuiButton button) throws IOException {
        if (button.field_146127_k == 1) {
            String url = "https://optifine.net/adloadx?f=OptiFine_1.8.9_HD_U_M5.jar&x=91ea";
            try { java.awt.Desktop.getDesktop().browse(new URI(url)); } catch (Exception e) {}
        }
        if (button.field_146127_k == 2) field_146297_k.func_147108_a(new GuiWVCMainMenu());
        if (button.field_146127_k == 3) {
            WVCMod.INSTANCE.getConfig().setShowOptiFineWarning(false);
            field_146297_k.func_147108_a(new GuiWVCMainMenu());
        }
    }

    /**
     * WVCスタイルのプレミアムボタン
     */
    private static class WVCWarningButton extends GuiButton {
        public WVCWarningButton(int id, int x, int y, int w, int h, String text) {
            super(id, x, y, w, h, text);
        }

        @Override
        public void func_146112_a(net.minecraft.client.Minecraft mc, int mouseX, int mouseY) {
            if (this.field_146125_m) {
                this.field_146123_n = mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g;
                int accent = WVCMod.INSTANCE.getConfig().getThemeColor();
                int bgCol = field_146123_n ? (0x80 << 24 | (accent & 0xFFFFFF)) : 0x40000000;
                
                func_73734_a(field_146128_h, field_146129_i, field_146128_h + field_146120_f, field_146129_i + field_146121_g, bgCol);
                func_73734_a(field_146128_h, field_146129_i + field_146121_g - 1, field_146128_h + field_146120_f, field_146129_i + field_146121_g, field_146123_n ? 0xFFFFFFFF : accent);
                
                int textColor = field_146123_n ? 0xFFFFFFFF : 0xFFAAAAAA;
                WVCMod.INSTANCE.getFontRenderer().drawCenteredString(field_146126_j, field_146128_h + field_146120_f / 2, field_146129_i + (field_146121_g - 8) / 2, textColor);
            }
        }
    }
}
