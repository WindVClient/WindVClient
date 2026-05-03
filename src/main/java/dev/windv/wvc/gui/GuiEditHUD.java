package dev.windv.wvc.gui;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Edit HUD 画面
 * マウスドラッグでHUDの位置を変更できます。
 */
public class GuiEditHUD extends GuiScreen {

    private WVCModule draggingModule = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // 背景を薄暗くする
        this.drawDefaultBackground();
        
        // ガイド線の表示
        this.drawVerticalLine(this.width / 2, -1, this.height, 0x40FFFFFF);
        this.drawHorizontalLine(-1, this.width, this.height / 2, 0x40FFFFFF);

        // 有効なHUDモジュールをループ
        for (WVCModule m : WVCMod.INSTANCE.getModuleManager().getModules()) {
            // HUD関連かつ有効なもののみ（簡易的に名前や種類で判別）
            if (!m.isEnabled() || !isHUDModule(m)) continue;

            int x = m.getX();
            int y = m.getY();
            
            // モジュールごとにサイズを調整
            int w = mc.fontRendererObj.getStringWidth(m.getName()) + 10;
            int h = 12;
            
            if (m.getName().equals("Keystrokes")) { w = 70; h = 60; }
            if (m.getName().equals("ArmorStatus")) { w = 40; h = 80; }
            if (m.getName().equals("PotionStatus")) { w = 80; h = 40; }
            if (m.getName().equals("Scoreboard")) { w = 100; h = 120; x = x - w; } // xは右端なので左端を計算
            if (m.getName().equals("DirectionHUD")) { w = 40; h = 20; x = x - 20; }
            
            // モジュールごとに枠を表示
            boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
            int borderColor = hovered ? 0xFF00FF00 : 0x80FFFFFF;
            
            drawRect(x - 2, y - 2, x + w + 2, y + h + 2, 0x20FFFFFF);
            this.drawHorizontalLine(x - 2, x + w + 2, y - 2, borderColor);
            this.drawHorizontalLine(x - 2, x + w + 2, y + h + 2, borderColor);
            this.drawVerticalLine(x - 2, y - 2, y + h + 2, borderColor);
            this.drawVerticalLine(x + w + 2, y - 2, y + h + 2, borderColor);
            
            mc.fontRendererObj.drawStringWithShadow(m.getName(), x, y, 0xFFFFFF);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (WVCModule m : WVCMod.INSTANCE.getModuleManager().getModules()) {
                if (!m.isEnabled() || !isHUDModule(m)) continue;
                
                int x = m.getX();
                int y = m.getY();
                
                int w = mc.fontRendererObj.getStringWidth(m.getName()) + 10;
                int h = 12;
                if (m.getName().equals("Keystrokes")) { w = 70; h = 60; }
                if (m.getName().equals("ArmorStatus")) { w = 40; h = 80; }
                if (m.getName().equals("PotionStatus")) { w = 80; h = 40; }
                if (m.getName().equals("Scoreboard")) { w = 100; h = 120; x = x - w; }
                if (m.getName().equals("DirectionHUD")) { w = 40; h = 20; x = x - 20; }

                if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
                    draggingModule = m;
                    dragOffsetX = mouseX - x;
                    dragOffsetY = mouseY - y;
                    return;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        draggingModule = null;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (draggingModule != null) {
            int newX = mouseX - dragOffsetX;
            int newY = mouseY - dragOffsetY;
            
            // スナップ閾値
            int snap = 5;
            
            // モジュールサイズ取得（snapping用に必要）
            int w = mc.fontRendererObj.getStringWidth(draggingModule.getName()) + 10;
            int h = 12;
            if (draggingModule.getName().equals("Keystrokes")) { w = 70; h = 60; }
            if (draggingModule.getName().equals("ArmorStatus")) { w = 40; h = 80; }
            if (draggingModule.getName().equals("PotionStatus")) { w = 80; h = 40; }
            if (draggingModule.getName().equals("Scoreboard")) { w = 100; h = 120; }
            if (draggingModule.getName().equals("DirectionHUD")) { w = 40; h = 20; }

            // --- 水平スナップ ---
            // 左端
            if (Math.abs(newX) < snap) newX = 0;
            // 中央
            else if (Math.abs((newX + w / 2) - this.width / 2) < snap) newX = this.width / 2 - w / 2;
            // 右端
            else if (Math.abs((newX + w) - this.width) < snap) newX = this.width - w;

            // --- 垂直スナップ ---
            // 上端
            if (Math.abs(newY) < snap) newY = 0;
            // 中央
            else if (Math.abs((newY + h / 2) - this.height / 2) < snap) newY = this.height / 2 - h / 2;
            // 下端
            else if (Math.abs((newY + h) - this.height) < snap) newY = this.height - h;

            if (draggingModule.getName().equals("Scoreboard")) {
                draggingModule.setX(newX + w); // 右端を保存
            } else {
                draggingModule.setX(newX);
            }
            draggingModule.setY(newY);
        }
    }

    /**
     * そのモジュールがHUD要素かどうかを判定
     */
    private boolean isHUDModule(WVCModule m) {
        String n = m.getName();
        return n.equals("FPS") || n.equals("CPS") || n.equals("Ping") || n.equals("Keystrokes") || 
               n.equals("ArmorStatus") || n.equals("PotionStatus") || n.equals("ReachDisplay") || n.equals("DirectionHUD") || n.equals("Scoreboard");
    }

    @Override
    public void onGuiClosed() {
        // GUIを閉じる際に座標を保存
        WVCMod.INSTANCE.getProfileManager().saveCurrentProfile();
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false; // ゲームを止めない
    }
}
