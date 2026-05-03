package dev.windv.wvc.gui;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.BooleanSetting;
import dev.windv.wvc.settings.ColorSetting;
import dev.windv.wvc.settings.ModeSetting;
import dev.windv.wvc.settings.Setting;
import dev.windv.wvc.settings.SliderSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

/**
 * WVC Premium Module Options GUI
 */
public class WVCGuiModuleOptions extends GuiScreen {

    private final WVCModule module;
    private final GuiScreen parent;

    private int panelWidth = 440;
    private int panelHeight = 340;
    
    private static final int COLOR_GRADIENT_TOP = 0xFF1E1E2C;
    private static final int COLOR_GRADIENT_BOTTOM = 0xFF111119;
    private static final int COLOR_ACCENT = 0xFF4F5BFF;

    public WVCGuiModuleOptions(GuiScreen parent, WVCModule module) {
        this.parent = parent;
        this.module = module;
    }

    @Override
    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        this.func_146276_q_();

        int centerX = this.field_146294_l / 2;
        int centerY = this.field_146295_m / 2;
        int startX = centerX - panelWidth / 2;
        int startY = centerY - panelHeight / 2;
https://github.com/Polyfrost/PolyBlur/tree/main/src/dummy/java
        // Shadow and Main Panel
        drawRoundedRect(startX - 2, startY - 2, startX + panelWidth + 2, startY + panelHeight + 2, 16, 0x66000000);
        drawGradientRoundedRect(startX, startY, startX + panelWidth, startY + panelHeight, 14, COLOR_GRADIENT_TOP, COLOR_GRADIENT_BOTTOM);

        // Header with Underline
        WVCMod.INSTANCE.getFontRenderer().drawString(module.getName().toUpperCase() + " SETTINGS", startX + 25, startY + 20, 0xFFFFFF);
        Gui.func_73734_a(startX + 25, startY + 38, startX + 100, startY + 39, COLOR_ACCENT);

        // Draw Settings List
        int currentY = startY + 60;
        for (Setting setting : module.getSettings()) {
            if (setting instanceof SliderSetting) {
                drawSlider((SliderSetting) setting, startX + 25, currentY, panelWidth - 50, mouseX, mouseY);
                currentY += 50;
            } else if (setting instanceof BooleanSetting) {
                drawBoolean((BooleanSetting) setting, startX + 25, currentY, panelWidth - 50, mouseX, mouseY);
                currentY += 40;
            } else if (setting instanceof ModeSetting) {
                drawMode((ModeSetting) setting, startX + 25, currentY, panelWidth - 50, mouseX, mouseY);
                currentY += 40;
            } else if (setting instanceof ColorSetting) {
                drawColor((ColorSetting) setting, startX + 25, currentY, panelWidth - 50, mouseX, mouseY);
                currentY += 75; // More space for RGB sliders
            }
        }

        // Back Button with Glow
        int btnW = 90;
        int btnH = 26;
        int btnX = startX + panelWidth - btnW - 25;
        int btnY = startY + panelHeight - btnH - 20;
        boolean hovered = mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH;
        
        drawRoundedRect(btnX, btnY, btnX + btnW, btnY + btnH, 8, hovered ? 0xFF353545 : 0xFF2A2A36);
        if (hovered) drawRoundedRect(btnX - 1, btnY - 1, btnX + btnW + 1, btnY + btnH + 1, 9, 0x444F5BFF);
        
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString("SAVE & BACK", btnX + btnW / 2, btnY + 7, 0xFFFFFF);

        super.func_73863_a(mouseX, mouseY, partialTicks);
    }

    private void drawSlider(SliderSetting setting, int x, int y, int width, int mouseX, int mouseY) {
        WVCMod.INSTANCE.getFontRenderer().drawString(setting.name, x, y, 0xCCFFFFFF);
        
        int sliderY = y + 18;
        int sliderH = 3;
        
        // Track
        drawRoundedRect(x, sliderY, x + width, sliderY + sliderH, 1, 0xFF0A0A0F);
        
        // Bar
        double percent = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
        int barWidth = (int) (width * percent);
        drawRoundedRect(x, sliderY, x + barWidth, sliderY + sliderH, 1, COLOR_ACCENT);
        
        // Glowy Knob
        int knobX = x + barWidth;
        drawCircle(knobX, sliderY + 1, 5, 0xFFFFFFFF);
        drawCircle(knobX, sliderY + 1, 7, 0x33FFFFFF); // Outer glow
        
        // Value Text
        String valText = String.format("%.1f", setting.getValue());
        WVCMod.INSTANCE.getFontRenderer().drawString(valText, x + width - 30, y, COLOR_ACCENT);

        // Interaction
        if (org.lwjgl.input.Mouse.isButtonDown(0)) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= sliderY - 8 && mouseY <= sliderY + 12) {
                double val = ((double) (mouseX - x) / width) * (setting.getMax() - setting.getMin()) + setting.getMin();
                setting.setValue(val);
            }
        }
    }

    private void drawBoolean(BooleanSetting setting, int x, int y, int width, int mouseX, int mouseY) {
        WVCMod.INSTANCE.getFontRenderer().drawString(setting.name, x, y, 0xCCFFFFFF);
        
        int switchW = 34;
        int switchH = 16;
        int switchX = x + width - switchW;
        int switchY = y - 2;
        
        boolean hovered = mouseX >= switchX && mouseX <= switchX + switchW && mouseY >= switchY && mouseY <= switchY + switchH;
        
        // Switch Track
        drawRoundedRect(switchX, switchY, switchX + switchW, switchY + switchH, 8, setting.isEnabled() ? 0xFF00FF88 : 0xFF353545);
        
        // Knob
        int knobX = setting.isEnabled() ? switchX + switchW - 12 : switchX + 4;
        drawCircle(knobX + 4, switchY + 8, 6, 0xFFFFFFFF);
    }

    private void drawMode(ModeSetting setting, int x, int y, int width, int mouseX, int mouseY) {
        WVCMod.INSTANCE.getFontRenderer().drawString(setting.name, x, y, 0xCCFFFFFF);
        
        int boxW = 80;
        int boxH = 16;
        int boxX = x + width - boxW;
        int boxY = y - 2;
        
        boolean hovered = mouseX >= boxX && mouseX <= boxX + boxW && mouseY >= boxY && mouseY <= boxY + boxH;
        
        drawRoundedRect(boxX, boxY, boxX + boxW, boxY + boxH, 4, hovered ? 0xFF353545 : 0xFF23232E);
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString(setting.getMode(), boxX + boxW / 2, boxY + 4, 0xFFFFFF);
    }

    private void drawColor(ColorSetting setting, int x, int y, int width, int mouseX, int mouseY) {
        WVCMod.INSTANCE.getFontRenderer().drawString(setting.name, x, y, 0xCCFFFFFF);
        
        // Color Preview Circle
        drawCircle(x + width - 10, y + 4, 6, setting.getColor());

        int sliderW = width - 40;
        int startX = x + 10;
        
        // R, G, B mini sliders
        setting.setRed(drawMiniSlider("R", startX, y + 15, sliderW, setting.getRed(), 0xFFFF4444, mouseX, mouseY));
        setting.setGreen(drawMiniSlider("G", startX, y + 32, sliderW, setting.getGreen(), 0xFF44FF44, mouseX, mouseY));
        setting.setBlue(drawMiniSlider("B", startX, y + 49, sliderW, setting.getBlue(), 0xFF4444FF, mouseX, mouseY));
    }

    private int drawMiniSlider(String label, int x, int y, int width, int value, int color, int mouseX, int mouseY) {
        WVCMod.INSTANCE.getFontRenderer().drawString(label, x - 10, y, 0x88FFFFFF);
        
        int sliderY = y + 3;
        int sliderH = 2;
        
        drawRoundedRect(x, sliderY, x + width, sliderY + sliderH, 1, 0xFF0A0A0F);
        int barW = (int) ((value / 255.0) * width);
        drawRoundedRect(x, sliderY, x + barW, sliderY + sliderH, 1, color);
        
        drawCircle(x + barW, sliderY + 1, 3, 0xFFFFFFFF);

        if (org.lwjgl.input.Mouse.isButtonDown(0)) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y - 2 && mouseY <= y + 8) {
                return (int) (((double) (mouseX - x) / width) * 255);
            }
        }
        return value;
    }

    @Override
    protected void func_73864_a(int mouseX, int mouseY, int mouseButton) throws IOException {
        int centerX = this.field_146294_l / 2;
        int centerY = this.field_146295_m / 2;
        int startX = centerX - panelWidth / 2;
        int startY = centerY - panelHeight / 2;

        // Back button check
        int btnW = 90;
        int btnH = 26;
        int btnX = startX + panelWidth - btnW - 25;
        int btnY = startY + panelHeight - btnH - 20;

        if (mouseX >= btnX && mouseX <= btnX + btnW && mouseY >= btnY && mouseY <= btnY + btnH) {
            this.field_146297_k.func_147108_a(parent);
            return;
        }

        // Settings interaction
        int currentY = startY + 60;
        for (Setting setting : module.getSettings()) {
            if (setting instanceof BooleanSetting) {
                int switchW = 34;
                int switchH = 16;
                int switchX = startX + 25 + (panelWidth - 50) - switchW;
                int switchY = currentY - 2;
                if (mouseX >= switchX && mouseX <= switchX + switchW && mouseY >= switchY && mouseY <= switchY + switchH) {
                    ((BooleanSetting) setting).toggle();
                    return;
                }
                currentY += 40;
            } else if (setting instanceof SliderSetting) {
                currentY += 50;
            } else if (setting instanceof ModeSetting) {
                int boxW = 80;
                int boxH = 16;
                int boxX = startX + 25 + (panelWidth - 50) - boxW;
                int boxY = currentY - 2;
                if (mouseX >= boxX && mouseX <= boxX + boxW && mouseY >= boxY && mouseY <= boxY + boxH) {
                    ((ModeSetting) setting).cycle();
                    return;
                }
                currentY += 40;
            } else if (setting instanceof ColorSetting) {
                currentY += 75;
            }
        }

        super.func_73864_a(mouseX, mouseY, mouseButton);
    }

    // --- Premium Drawing Utilities (Mirroring WVCGuiSettings) ---

    private void drawRoundedRect(int x, int y, int x1, int y1, int radius, int color) {
        Gui.func_73734_a(x + radius, y, x1 - radius, y1, color);
        Gui.func_73734_a(x, y + radius, x + radius, y1 - radius, color);
        Gui.func_73734_a(x1 - radius, y + radius, x1, y1 - radius, color);
        drawCircle(x + radius, y + radius, radius, color);
        drawCircle(x1 - radius, y + radius, radius, color);
        drawCircle(x + radius, y1 - radius, radius, color);
        drawCircle(x1 - radius, y1 - radius, radius, color);
    }

    private void drawGradientRoundedRect(int x, int y, int x1, int y1, int radius, int col1, int col2) {
        this.func_73733_a(x + radius, y, x1 - radius, y1, col1, col2);
        Gui.func_73734_a(x, y + radius, x + radius, y1 - radius, col1);
        Gui.func_73734_a(x1 - radius, y + radius, x1, y1 - radius, col1);
        drawCircle(x + radius, y + radius, radius, col1);
        drawCircle(x1 - radius, y + radius, radius, col1);
        drawCircle(x + radius, y1 - radius, radius, col2);
        drawCircle(x1 - radius, y1 - radius, radius, col2);
    }

    private void drawCircle(int x, int y, int radius, int color) {
        float f = (float)(color >> 24 & 255) / 255.0F;
        float f1 = (float)(color >> 16 & 255) / 255.0F;
        float f2 = (float)(color >> 8 & 255) / 255.0F;
        float f3 = (float)(color & 255) / 255.0F;
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_POLYGON);
        for (int i = 0; i <= 360; i++) {
            double angle = Math.PI * i / 180;
            GL11.glVertex2d(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius);
        }
        GL11.glEnd();
    }
}
