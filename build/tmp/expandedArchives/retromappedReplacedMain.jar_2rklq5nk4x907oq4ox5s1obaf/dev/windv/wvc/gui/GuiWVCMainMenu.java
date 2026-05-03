package dev.windv.wvc.gui;

import dev.windv.wvc.WVCMod;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.net.URI;

/**
 * Wind V Client プレミアムメインメニュー - 極限カスタムスタイル
 */
public class GuiWVCMainMenu extends GuiScreen {

    private static final ResourceLocation LOGO = new ResourceLocation("windvclient", "logo.png");
    private boolean showCreatorMenu = false;

    @Override
    public void func_73866_w_() {
        refreshButtons();
    }

    private void refreshButtons() {
        this.field_146292_n.clear();
        int w = 160;
        int h = 24;
        int spacing = 30;
        int startY = field_146295_m / 2 - 20;

        if (!showCreatorMenu) {
            // メインメニュー
            this.field_146292_n.add(new WVCButton(1, field_146294_l / 2 - w / 2, startY, w, h, "Singleplayer"));
            this.field_146292_n.add(new WVCButton(2, field_146294_l / 2 - w / 2, startY + spacing, w, h, "Multiplayer"));
            this.field_146292_n.add(new WVCButton(0, field_146294_l / 2 - w / 2, startY + spacing * 2, w, h, "Options..."));
            this.field_146292_n.add(new WVCButton(9, field_146294_l / 2 - w / 2, startY + spacing * 3, w, h, "Creator >"));
            this.field_146292_n.add(new WVCButton(4, field_146294_l / 2 - w / 2, startY + spacing * 4, w, h, "Quit Game"));
        } else {
            // Creator サブメニュー (メインと同じスタイル)
            this.field_146292_n.add(new WVCButton(10, field_146294_l / 2 - w / 2, startY, w, h, "YouTube"));
            this.field_146292_n.add(new WVCButton(11, field_146294_l / 2 - w / 2, startY + spacing, w, h, "Note"));
            this.field_146292_n.add(new WVCButton(12, field_146294_l / 2 - w / 2, startY + spacing * 2, w, h, "GitHub"));
            this.field_146292_n.add(new WVCButton(14, field_146294_l / 2 - w / 2, startY + spacing * 3, w, h, "Discord"));
            this.field_146292_n.add(new WVCButton(13, field_146294_l / 2 - w / 2, startY + spacing * 4, w, h, "HomePage"));
            this.field_146292_n.add(new WVCButton(99, field_146294_l / 2 - w / 2, startY + spacing * 5 + 10, w, h, "< Back"));
        }
    }

    @Override
    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        // バニラ感を排除したサイバー背景
        func_73733_a(0, 0, field_146294_l, field_146295_m, 0xFF020205, 0xFF080815);
        
        // 装飾的なオーバーレイ
        func_73734_a(0, 0, field_146294_l, field_146295_m, 0x33000000);

        // ロゴ描画 (比率を 1:1 に固定し、大きく描画)
        int logoSize = 180;
        field_146297_k.func_110434_K().func_110577_a(LOGO);
        GlStateManager.func_179147_l();
        GlStateManager.func_179131_c(1, 1, 1, 1);
        // 比率が崩れないよう、正方形で描画（生成されたロゴは1:1のため）
        Gui.func_146110_a(field_146294_l / 2 - logoSize / 2, field_146295_m / 2 - logoSize - 40, 0, 0, logoSize, logoSize, logoSize, logoSize);

        // クライアント情報 (Tahoma使用)
        String title = "Wind V Client v" + WVCMod.VERSION;
        String creator = "Created by WindV";
        WVCMod.INSTANCE.getFontRenderer().drawString(title, field_146294_l - WVCMod.INSTANCE.getFontRenderer().getStringWidth(title) - 10, field_146295_m - 25, 0x88FFFFFF);
        WVCMod.INSTANCE.getFontRenderer().drawString(creator, field_146294_l - WVCMod.INSTANCE.getFontRenderer().getStringWidth(creator) - 10, field_146295_m - 12, 0x44FFFFFF);

        super.func_73863_a(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void func_146284_a(GuiButton button) throws IOException {
        if (button.field_146127_k == 0) field_146297_k.func_147108_a(new GuiOptions(this, field_146297_k.field_71474_y));
        if (button.field_146127_k == 1) field_146297_k.func_147108_a(new GuiSelectWorld(this));
        if (button.field_146127_k == 2) field_146297_k.func_147108_a(new GuiMultiplayer(this));
        if (button.field_146127_k == 4) field_146297_k.func_71400_g();
        
        if (button.field_146127_k == 9) { showCreatorMenu = true; refreshButtons(); }
        if (button.field_146127_k == 99) { showCreatorMenu = false; refreshButtons(); }

        // 外部リンク
        if (button.field_146127_k == 10) openURL("https://www.youtube.com/channel/UChqgDqdlRJHMZjIorp5z3WQ");
        if (button.field_146127_k == 11) openURL("https://note.com/wind_v_2233");
        if (button.field_146127_k == 12) openURL("https://github.com/WindVClient");
        if (button.field_146127_k == 13) openURL("https://windvclient.pages.dev");
        if (button.field_146127_k == 14) openURL("https://discord.gg/cYxScVkeKy");
    }

    private void openURL(String url) {
        try {
            Class<?> desktopClass = Class.forName("java.awt.Desktop");
            Object desktop = desktopClass.getMethod("getDesktop").invoke(null);
            desktopClass.getMethod("browse", URI.class).invoke(desktop, new URI(url));
        } catch (Exception e) {
            try { Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url); } catch (Exception ex) {}
        }
    }

    /**
     * WVC専用 プレミアムボタンクラス
     */
    private static class WVCButton extends GuiButton {
        public WVCButton(int id, int x, int y, int w, int h, String text) {
            super(id, x, y, w, h, text);
        }

        @Override
        public void func_146112_a(net.minecraft.client.Minecraft mc, int mouseX, int mouseY) {
            if (this.field_146125_m) {
                this.field_146123_n = mouseX >= this.field_146128_h && mouseY >= this.field_146129_i && mouseX < this.field_146128_h + this.field_146120_f && mouseY < this.field_146129_i + this.field_146121_g;
                
                int accent = WVCMod.INSTANCE.getConfig().getThemeColor();
                int bgCol = field_146123_n ? (0x80 << 24 | (accent & 0xFFFFFF)) : 0x40000000;
                
                // ボタン背景（半透明パネル）
                func_73734_a(field_146128_h, field_146129_i, field_146128_h + field_146120_f, field_146129_i + field_146121_g, bgCol);
                
                // 下線のアクセント
                func_73734_a(field_146128_h, field_146129_i + field_146121_g - 1, field_146128_h + field_146120_f, field_146129_i + field_146121_g, field_146123_n ? 0xFFFFFFFF : accent);
                
                int textColor = field_146123_n ? 0xFFFFFFFF : 0xFFAAAAAA;
                // Tahomaフォントレンダラーで描画
                WVCMod.INSTANCE.getFontRenderer().drawCenteredString(field_146126_j, field_146128_h + field_146120_f / 2, field_146129_i + (field_146121_g - 8) / 2, textColor);
            }
        }
    }
}
