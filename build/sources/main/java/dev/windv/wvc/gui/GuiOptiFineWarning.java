package dev.windv.wvc.gui;

import dev.windv.wvc.WVCMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import java.io.IOException;
import java.net.URI;

public class GuiOptiFineWarning extends GuiScreen {

    @Override
    public void initGui() {
        this.buttonList.clear();
        int w = 180;
        int h = 26;
        int startY = height / 2 + 10;
        // 日本語をユニコード化: "Download", "\u4eca\u306f\u3044\u3044" (今はいい), "\u4eca\u5f8c\u8868\u793a\u3057\u306a\u3044" (今後表示しない)
        this.buttonList.add(new WVCWarningButton(1, width / 2 - w / 2, startY, w, h, "Download"));
        this.buttonList.add(new WVCWarningButton(2, width / 2 - w / 2, startY + 32, w, h, "\u4eca\u306f\u3044\u3044"));
        this.buttonList.add(new WVCWarningButton(3, width / 2 - w / 2, startY + 64, w, h, "\u4eca\u5f8c\u8868\u793a\u3057\u306a\u3044"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawGradientRect(0, 0, width, height, 0xFF050505, 0xFF121212);
        
        // "OptiFine\u304c\u5c0e\u5165\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002" (OptiFineが導入されていません。)
        String line1 = "OptiFine\u304c\u5c0e\u5165\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002";
        // "\u5c0e\u5165\u3059\u308b\u3053\u3068\u3067\u8efd\u91cf\u5316\u3055\u308c\u308b\u306e\u3067\u63a8\u5968\u3057\u3066\u3044\u307e\u3059\u3002" (導入することで軽量化されるので推奨しています。)
        String line2 = "\u5c0e\u5165\u3059\u308b\u3053\u3068\u3067\u8efd\u91cf\u5316\u3055\u308c\u308b\u306e\u3067\u63a8\u5968\u3057\u3066\u3044\u307e\u3059\u3002";
        
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString(line1, width / 2, height / 2 - 50, 0xFF5555);
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString(line2, width / 2, height / 2 - 32, 0xBBBBBB);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            String url = "https://optifine.net/adloadx?f=OptiFine_1.8.9_HD_U_M5.jar&x=91ea";
            try { java.awt.Desktop.getDesktop().browse(new URI(url)); } catch (Exception e) {}
        }
        if (button.id == 2) mc.displayGuiScreen(new GuiWVCMainMenu());
        if (button.id == 3) {
            WVCMod.INSTANCE.getConfig().setShowOptiFineWarning(false);
            mc.displayGuiScreen(new GuiWVCMainMenu());
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
        public void drawButton(net.minecraft.client.Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                int accent = WVCMod.INSTANCE.getConfig().getThemeColor();
                int bgCol = hovered ? (0x80 << 24 | (accent & 0xFFFFFF)) : 0x40000000;
                
                drawRect(xPosition, yPosition, xPosition + width, yPosition + height, bgCol);
                drawRect(xPosition, yPosition + height - 1, xPosition + width, yPosition + height, hovered ? 0xFFFFFFFF : accent);
                
                int textColor = hovered ? 0xFFFFFFFF : 0xFFAAAAAA;
                WVCMod.INSTANCE.getFontRenderer().drawCenteredString(displayString, xPosition + width / 2, yPosition + (height - 8) / 2, textColor);
            }
        }
    }
}
