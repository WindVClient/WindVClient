package dev.windv.wvc.gui;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.Setting;
import dev.windv.wvc.settings.SliderSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * EditHUD GUI - Drag and drop HUD elements
 */
public class WVCGuiEditHUD extends GuiScreen {

    private WVCModule dragging = null;
    private int dragStartX, dragStartY;
    private double initialX, initialY;

    private final List<WVCModule> hudModules = new ArrayList<>();

    public WVCGuiEditHUD() {
        // Collect modules that have X and Y settings
        for (WVCModule m : WVCMod.INSTANCE.getModuleManager().getModules()) {
            if (m.isEnabled() && getXSetting(m) != null && getYSetting(m) != null) {
                hudModules.add(m);
            }
        }
    }

    @Override
    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        // Draw a dark translucent overlay
        this.func_73733_a(0, 0, this.field_146294_l, this.field_146295_m, 0x66000000, 0xAA000000);
        
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString("EDIT HUD MODE", this.field_146294_l / 2, 10, 0xFFFFFF);
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString("DRAG ELEMENTS TO POSITION THEM", this.field_146294_l / 2, 22, 0x66FFFFFF);

        for (WVCModule m : hudModules) {
            SliderSetting sx = getXSetting(m);
            SliderSetting sy = getYSetting(m);
            
            int x = sx.getValueInt();
            int y = sy.getValueInt();
            int w = 60; // Estimated width
            int h = 12; // Estimated height (Keystrokes might be larger)
            
            if (m.getName().equals("Keystrokes")) {
                w = 64;
                h = 56;
            }

            boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
            
            // Draw Highlight Box
            func_73734_a(x - 2, y - 2, x + w + 2, y + h + 2, (m == dragging) ? 0xAA4F5BFF : (hovered ? 0x664F5BFF : 0x334F5BFF));
            WVCMod.INSTANCE.getFontRenderer().drawString(m.getName(), x, y - 10, 0xAAFFFFFF);
            
            // Update position if dragging
            if (m == dragging) {
                int dx = mouseX - dragStartX;
                int dy = mouseY - dragStartY;
                sx.setValue(initialX + dx);
                sy.setValue(initialY + dy);
            }
        }
        
        super.func_73863_a(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void func_73864_a(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (WVCModule m : hudModules) {
            int x = getXSetting(m).getValueInt();
            int y = getYSetting(m).getValueInt();
            int w = (m.getName().equals("Keystrokes")) ? 64 : 60;
            int h = (m.getName().equals("Keystrokes")) ? 56 : 12;

            if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
                dragging = m;
                dragStartX = mouseX;
                dragStartY = mouseY;
                initialX = getXSetting(m).getValue();
                initialY = getYSetting(m).getValue();
                return;
            }
        }
        super.func_73864_a(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void func_146286_b(int mouseX, int mouseY, int state) {
        dragging = null;
        super.func_146286_b(mouseX, mouseY, state);
    }

    private SliderSetting getXSetting(WVCModule m) {
        for (Setting s : m.getSettings()) if (s.name.equals("X Offset")) return (SliderSetting) s;
        return null;
    }

    private SliderSetting getYSetting(WVCModule m) {
        for (Setting s : m.getSettings()) if (s.name.equals("Y Offset")) return (SliderSetting) s;
        return null;
    }
}
