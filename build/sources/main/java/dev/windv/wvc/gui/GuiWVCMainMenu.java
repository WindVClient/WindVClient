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
    public void initGui() {
        refreshButtons();
    }

    private void refreshButtons() {
        this.buttonList.clear();
        int w = 160;
        int h = 24;
        int spacing = 30;
        int startY = height / 2 - 20;

        if (!showCreatorMenu) {
            // メインメニュー
            this.buttonList.add(new WVCButton(1, width / 2 - w / 2, startY, w, h, "Singleplayer"));
            this.buttonList.add(new WVCButton(2, width / 2 - w / 2, startY + spacing, w, h, "Multiplayer"));
            this.buttonList.add(new WVCButton(0, width / 2 - w / 2, startY + spacing * 2, w, h, "Options..."));
            this.buttonList.add(new WVCButton(9, width / 2 - w / 2, startY + spacing * 3, w, h, "Creator >"));
            this.buttonList.add(new WVCButton(4, width / 2 - w / 2, startY + spacing * 4, w, h, "Quit Game"));
        } else {
            // Creator サブメニュー (メインと同じスタイル)
            this.buttonList.add(new WVCButton(10, width / 2 - w / 2, startY, w, h, "YouTube"));
            this.buttonList.add(new WVCButton(11, width / 2 - w / 2, startY + spacing, w, h, "Note"));
            this.buttonList.add(new WVCButton(12, width / 2 - w / 2, startY + spacing * 2, w, h, "GitHub"));
            this.buttonList.add(new WVCButton(14, width / 2 - w / 2, startY + spacing * 3, w, h, "Discord"));
            this.buttonList.add(new WVCButton(13, width / 2 - w / 2, startY + spacing * 4, w, h, "HomePage"));
            this.buttonList.add(new WVCButton(99, width / 2 - w / 2, startY + spacing * 5 + 10, w, h, "< Back"));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // バニラ感を排除したサイバー背景
        drawGradientRect(0, 0, width, height, 0xFF020205, 0xFF080815);
        
        // 装飾的なオーバーレイ
        drawRect(0, 0, width, height, 0x33000000);

        // ロゴ描画 (比率を 1:1 に固定し、大きく描画)
        int logoSize = 180;
        mc.getTextureManager().bindTexture(LOGO);
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, 1);
        // 比率が崩れないよう、正方形で描画（生成されたロゴは1:1のため）
        Gui.drawModalRectWithCustomSizedTexture(width / 2 - logoSize / 2, height / 2 - logoSize - 40, 0, 0, logoSize, logoSize, logoSize, logoSize);

        // クライアント情報 (Tahoma使用)
        String title = "Wind V Client v" + WVCMod.VERSION;
        String creator = "Created by WindV";
        WVCMod.INSTANCE.getFontRenderer().drawString(title, width - WVCMod.INSTANCE.getFontRenderer().getStringWidth(title) - 10, height - 25, 0x88FFFFFF);
        WVCMod.INSTANCE.getFontRenderer().drawString(creator, width - WVCMod.INSTANCE.getFontRenderer().getStringWidth(creator) - 10, height - 12, 0x44FFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        if (button.id == 1) mc.displayGuiScreen(new GuiSelectWorld(this));
        if (button.id == 2) mc.displayGuiScreen(new GuiMultiplayer(this));
        if (button.id == 4) mc.shutdown();
        
        if (button.id == 9) { showCreatorMenu = true; refreshButtons(); }
        if (button.id == 99) { showCreatorMenu = false; refreshButtons(); }

        // 外部リンク
        if (button.id == 10) openURL("https://www.youtube.com/channel/UChqgDqdlRJHMZjIorp5z3WQ");
        if (button.id == 11) openURL("https://note.com/wind_v_2233");
        if (button.id == 12) openURL("https://github.com/WindVClient");
        if (button.id == 13) openURL("https://windvclient.pages.dev");
        if (button.id == 14) openURL("https://discord.gg/cYxScVkeKy");
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
        public void drawButton(net.minecraft.client.Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                
                int accent = WVCMod.INSTANCE.getConfig().getThemeColor();
                int bgCol = hovered ? (0x80 << 24 | (accent & 0xFFFFFF)) : 0x40000000;
                
                // ボタン背景（半透明パネル）
                drawRect(xPosition, yPosition, xPosition + width, yPosition + height, bgCol);
                
                // 下線のアクセント
                drawRect(xPosition, yPosition + height - 1, xPosition + width, yPosition + height, hovered ? 0xFFFFFFFF : accent);
                
                int textColor = hovered ? 0xFFFFFFFF : 0xFFAAAAAA;
                // Tahomaフォントレンダラーで描画
                WVCMod.INSTANCE.getFontRenderer().drawCenteredString(displayString, xPosition + width / 2, yPosition + (height - 8) / 2, textColor);
            }
        }
    }
}
