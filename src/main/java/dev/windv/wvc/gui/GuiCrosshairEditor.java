package dev.windv.wvc.gui;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.visual.CrosshairModule;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

/**
 * Crosshair Pixel Editor - Refined Edition
 */
public class GuiCrosshairEditor extends GuiScreen {

    private final CrosshairModule module;
    private final GuiScreen parent;
    
    private final int GRID_SIZE = 16;
    private final int CELL_SIZE = 15;
    private final int PANEL_W = 340;
    private final int PANEL_H = 300;

    public GuiCrosshairEditor(GuiScreen parent, CrosshairModule module) {
        this.parent = parent;
        this.module = module;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        
        int px = this.width / 2 - PANEL_W / 2;
        int py = this.height / 2 - PANEL_H / 2;
        
        drawRect(px, py, px + PANEL_W, py + PANEL_H, 0xEE1E1E1E);
        drawRect(px, py, px + PANEL_W, py + 2, 0xFF0078D4);
        
        fontRendererObj.drawString("Crosshair Editor (16x16)", px + 15, py + 12, 0xFFFFFF);

        int gx = px + 20;
        int gy = py + 35;
        boolean[] dots = module.getDots();
        
        for (int i = 0; i < dots.length; i++) {
            int x = i % GRID_SIZE;
            int y = i / GRID_SIZE;
            int xPos = gx + x * CELL_SIZE;
            int yPos = gy + y * CELL_SIZE;
            
            boolean hov = mouseX >= xPos && mouseX < xPos + CELL_SIZE && mouseY >= yPos && mouseY < yPos + CELL_SIZE;
            int color = dots[i] ? 0xFF0078D4 : (hov ? 0x44FFFFFF : 0x11FFFFFF);
            
            drawRect(xPos, yPos, xPos + CELL_SIZE - 1, yPos + CELL_SIZE - 1, color);
        }

        // プレビューと操作
        int optX = gx + (GRID_SIZE * CELL_SIZE) + 20;
        int optY = gy;
        
        fontRendererObj.drawString("Preview", optX, optY, 0xAAAAAA);
        drawRect(optX, optY + 12, optX + 40, optY + 52, 0xFF000000);
        for (int i = 0; i < dots.length; i++) {
            if (dots[i]) {
                int x = i % GRID_SIZE;
                int y = i / GRID_SIZE;
                drawRect(optX + 12 + x, optY + 24 + y, optX + 12 + x + 1, optY + 24 + y + 1, 0xFF00FF00);
            }
        }

        // クリアボタン
        int clX = optX;
        int clY = optY + 70;
        boolean clHov = mouseX >= clX && mouseX <= clX + 50 && mouseY >= clY && mouseY <= clY + 20;
        drawRect(clX, clY, clX + 50, clY + 20, clHov ? 0xFFCC4444 : 0xFF883333);
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString("Clear", clX + 25, clY + 6, 0xFFFFFF);

        fontRendererObj.drawString("ESC to Save", px + 15, py + PANEL_H - 18, 0x666666);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int px = this.width / 2 - PANEL_W / 2;
        int py = this.height / 2 - PANEL_H / 2;
        int gx = px + 20;
        int gy = py + 35;

        // グリッドクリック
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            int xPos = gx + (i % GRID_SIZE) * CELL_SIZE;
            int yPos = gy + (i / GRID_SIZE) * CELL_SIZE;
            if (mouseX >= xPos && mouseX < xPos + CELL_SIZE && mouseY >= yPos && mouseY <= yPos + CELL_SIZE) {
                module.setDot(i, !module.getDots()[i]);
                mc.thePlayer.playSound("random.click", 0.5F, 1.2F);
                return;
            }
        }

        // クリアボタンクリック
        int optX = gx + (GRID_SIZE * CELL_SIZE) + 20;
        if (mouseX >= optX && mouseX <= optX + 50 && mouseY >= gy + 70 && mouseY <= gy + 90) {
            for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) module.setDot(i, false);
            mc.thePlayer.playSound("random.click", 0.5F, 0.8F);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) { mc.displayGuiScreen(parent); return; }
        super.keyTyped(typedChar, keyCode);
    }
}
