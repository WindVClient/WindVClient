package dev.windv.wvc.gui;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.module.visual.CrosshairModule;
import dev.windv.wvc.profile.ProfileManager;
import dev.windv.wvc.settings.BooleanSetting;
import dev.windv.wvc.settings.ColorSetting;
import dev.windv.wvc.settings.KeybindSetting;
import dev.windv.wvc.settings.Setting;
import dev.windv.wvc.settings.SliderSetting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * WVC Settings GUI - Top Tab Navigation System
 */
public class WVCGuiSettings extends GuiScreen {

    private String currentTab = "MODS";
    private String selectedCategory = "ALL";
    private WVCModule selectedModule = null;
    private KeybindSetting listeningKeybind = null;
    
    // スクロール制御
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private int detailScrollOffset = 0;
    private int detailMaxScroll = 0;

    // サイズ調整
    private static final int PANEL_W = 580;
    private static final int PANEL_H = 340; 
    private static final int TOP_BAR_H = 45;
    private static final int SIDEBAR_W = 120;
    private static final int CARD_W = 195;
    private static final int CARD_H = 75;
    private static final int CARD_GAP = 10;
    private static final int CARD_PAD = 15;

    private static final int COL_BG         = 0xEE141414;
    private static final int COL_SIDEBAR    = 0xFF0D0D0D;
    private static final int COL_CARD       = 0xFF1F1F1F;
    private static final int COL_CARD_HOVER = 0xFF292929;
    
    private int getAccentColor() { return WVCMod.INSTANCE.getConfig().getThemeColor(); }

    @Override
    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        func_146276_q_();

        int px = field_146294_l / 2 - PANEL_W / 2;
        int py = field_146295_m / 2 - PANEL_H / 2;

        // メインパネル背景
        drawRoundedRect(px, py, px + PANEL_W, py + PANEL_H, 12, COL_BG);

        // --- トップバー (MODS, SETTINGS, PROFILES) ---
        drawRoundedRectTop(px, py, px + PANEL_W, py + TOP_BAR_H, 12, COL_SIDEBAR);
        String[] tabs = {"MODS", "SETTINGS", "PROFILES"};
        int tabW = 100;
        for (int i = 0; i < tabs.length; i++) {
            int tx = px + 20 + i * (tabW + 10);
            int ty = py + 10;
            boolean hov = mouseX >= tx && mouseX <= tx + tabW && mouseY >= ty && mouseY <= ty + 25;
            boolean sel = currentTab.equals(tabs[i]);
            if (sel) {
                drawRoundedRect(tx, ty, tx + tabW, ty + 25, 6, getAccentColor());
                drawRoundedRect(tx + 20, ty + 28, tx + tabW - 20, ty + 30, 1, 0xFFFFFFFF);
            } else if (hov) {
                drawRoundedRect(tx, ty, tx + tabW, ty + 25, 6, 0x11FFFFFF);
            }
            WVCMod.INSTANCE.getFontRenderer().drawCenteredString(tabs[i], tx + tabW / 2, ty + 7, sel ? 0xFFFFFFFF : 0xFF888888);
        }

        // Edit HUD ボタン (右端)
        int ehW = 80; int ehH = 22;
        int ehX = px + PANEL_W - ehW - 15;
        int ehY = py + 12;
        boolean ehHov = mouseX >= ehX && mouseX <= ehX + ehW && mouseY >= ehY && mouseY <= ehY + ehH;
        drawRoundedRect(ehX, ehY, ehX + ehW, ehY + ehH, 6, ehHov ? adjustBrightness(getAccentColor(), 1.2f) : getAccentColor());
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString("Edit HUD", ehX + ehW / 2, ehY + 6, 0xFFFFFFFF);

        int contentX = px;
        int contentY = py + TOP_BAR_H;
        int contentW = PANEL_W;
        int contentH = PANEL_H - TOP_BAR_H;

        if (currentTab.equals("MODS")) {
            // サイドバー表示 (カテゴリー)
            drawRoundedRectLeft(px, contentY, px + SIDEBAR_W, py + PANEL_H, 0, COL_SIDEBAR);
            String[] cats = {"ALL", "MOVEMENT", "HUD", "VISUAL", "SYSTEM"};
            for (int i = 0; i < cats.length; i++) {
                int cy = contentY + 20 + i * 35;
                boolean hov = mouseX >= px + 10 && mouseX <= px + SIDEBAR_W - 10 && mouseY >= cy && mouseY <= cy + 25;
                boolean sel = selectedCategory.equals(cats[i]);
                if (sel) drawRoundedRect(px + 10, cy, px + SIDEBAR_W - 10, cy + 25, 5, 0x22FFFFFF);
                else if (hov) drawRoundedRect(px + 10, cy, px + SIDEBAR_W - 10, cy + 25, 5, 0x11FFFFFF);
                WVCMod.INSTANCE.getFontRenderer().drawString(cats[i], px + 20, cy + 8, sel ? getAccentColor() : 0xFF888888);
            }

            if (selectedModule != null) {
                drawModuleSettings(selectedModule, px + SIDEBAR_W, contentY, PANEL_W - SIDEBAR_W, contentH, mouseX, mouseY);
                // Back Button
                int bx = px + SIDEBAR_W + 15; int by = py + TOP_BAR_H + 10;
                boolean bHov = mouseX >= bx && mouseX <= bx + 45 && mouseY >= by && mouseY <= by + 18;
                drawRoundedRect(bx, by, bx + 45, by + 18, 4, bHov ? 0x44FFFFFF : 0x22FFFFFF);
                WVCMod.INSTANCE.getFontRenderer().drawCenteredString("< Back", bx + 22, by + 4, 0xFFFFFF);
            } else {
                drawModuleList(px + SIDEBAR_W, contentY, PANEL_W - SIDEBAR_W, contentH, mouseX, mouseY);
            }
        } else if (currentTab.equals("SETTINGS")) {
            drawGlobalSettings(px, contentY, PANEL_W, contentH, mouseX, mouseY);
        } else if (currentTab.equals("PROFILES")) {
            drawProfiles(px, contentY, PANEL_W, contentH, mouseX, mouseY);
        }

        super.func_73863_a(mouseX, mouseY, partialTicks);
    }

    private void drawProfiles(int x, int y, int w, int h, int mouseX, int mouseY) {
        int sx = x + 40;
        int cy = y + 30;
        ProfileManager pm = WVCMod.INSTANCE.getProfileManager();
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow("Profile Management", sx, cy, 0xFFFFFF);
        cy += 40;

        List<String> profiles = pm.getProfileList();
        for (String p : profiles) {
            boolean sel = pm.getCurrentProfile().equals(p);
            boolean hov = mouseX >= sx && mouseX <= sx + 200 && mouseY >= cy && mouseY <= cy + 35;
            drawRoundedRect(sx, cy, sx + 200, cy + 35, 8, sel ? getAccentColor() : 0xFF222222);
            if (!sel && hov) drawRoundedRect(sx, cy, sx + 200, cy + 35, 8, 0x33FFFFFF);
            WVCMod.INSTANCE.getFontRenderer().drawString(p, sx + 15, cy + 12, 0xFFFFFFFF);
            if (sel) WVCMod.INSTANCE.getFontRenderer().drawString("(Active)", sx + 140, cy + 12, 0xAAAAAA);
            cy += 42;
        }

        int btnX = sx; int btnY = cy + 10;
        boolean bHov = mouseX >= btnX && mouseX <= btnX + 130 && mouseY >= btnY && mouseY <= btnY + 28;
        drawRoundedRect(btnX, btnY, btnX + 130, btnY + 28, 6, bHov ? 0x44FFFFFF : 0x22FFFFFF);
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString("+ Create New", btnX + 65, btnY + 8, 0xFFFFFF);
    }

    private void drawGlobalSettings(int x, int y, int w, int h, int mouseX, int mouseY) {
        int sx = x + 40;
        int cy = y + 30;
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow("Client Settings", sx, cy, 0xFFFFFF);
        cy += 50;

        WVCMod.INSTANCE.getFontRenderer().drawString("UI Color Theme", sx, cy, 0xBBFFFFFF);
        cy += 25;

        Object[][] themes = {{0xFF0078D4, "Blue"}, {0xFFD40000, "Red"}, {0xFF00D455, "Green"}, {0xFFFF007F, "Pink"}, {0xFFFFD700, "Gold"}, {0xFF7F00FF, "Purple"}};
        for (int i = 0; i < themes.length; i++) {
            int tx = sx + (i % 3) * 140;
            int ty = cy + (i / 3) * 45;
            int themeCol = (int) themes[i][0];
            String themeName = (String) themes[i][1];
            boolean hov = mouseX >= tx && mouseX <= tx + 120 && mouseY >= ty && mouseY <= ty + 30;
            boolean sel = getAccentColor() == themeCol;
            drawRoundedRect(tx, ty, tx + 120, ty + 30, 8, sel ? getAccentColor() : 0xFF222222);
            if (!sel && hov) drawRoundedRect(tx, ty, tx + 120, ty + 30, 8, 0x33FFFFFF);
            WVCMod.INSTANCE.getFontRenderer().drawCenteredString(themeName, tx + 60, ty + 9, 0xFFFFFFFF);
            drawRoundedRect(tx + 8, ty + 8, tx + 12, ty + 22, 2, themeCol);
        }
    }

    private void drawModuleList(int x, int y, int w, int h, int mouseX, int mouseY) {
        List<WVCModule> mods = getFilteredModules();
        int lx = x + CARD_PAD;
        int ly = y + CARD_PAD;
        int totalH = ((mods.size() + 1) / 2) * (CARD_H + CARD_GAP) + CARD_PAD;
        maxScrollOffset = Math.max(0, totalH - h);

        enableScissor(x, y, w, h);
        for (int i = 0; i < mods.size(); i++) {
            WVCModule m = mods.get(i);
            int cx = lx + (i % 2) * (CARD_W + CARD_GAP);
            int cy = ly + (i / 2) * (CARD_H + CARD_GAP) - scrollOffset;
            if (cy + CARD_H < y || cy > y + h) continue;

            boolean hov = mouseX >= cx && mouseX <= cx + CARD_W && mouseY >= cy && mouseY <= cy + CARD_H;
            drawRoundedRect(cx, cy, cx + CARD_W, cy + CARD_H, 10, hov ? COL_CARD_HOVER : COL_CARD);
            if (m.isEnabled()) drawRoundedRect(cx + 6, cy + 6, cx + 10, cy + CARD_H - 6, 2, getAccentColor());
            
            WVCMod.INSTANCE.getFontRenderer().drawString(m.getName(), cx + 18, cy + 12, 0xFFFFFFFF);
            WVCMod.INSTANCE.getFontRenderer().drawString("Details >", cx + CARD_W - 60, cy + CARD_H - 18, hov ? getAccentColor() : 0xFF666666);
            drawToggleSwitch(cx + CARD_W - 45, cy + 12, m.isEnabled(), false);
        }
        disableScissor();
        drawScrollBar(x + w - 5, y + 5, h - 10, scrollOffset, maxScrollOffset);
    }

    private void drawModuleSettings(WVCModule m, int x, int y, int w, int h, int mouseX, int mouseY) {
        int sx = x + 30;
        int cy = y + 40 - detailScrollOffset;
        enableScissor(x, y, w, h);
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow(m.getName() + " Settings", sx, cy, 0xFFFFFF);
        cy += 35;
        if (m instanceof CrosshairModule) {
            boolean hov = mouseX >= sx && mouseX <= sx + 110 && mouseY >= cy && mouseY <= cy + 22;
            drawRoundedRect(sx, cy, sx + 110, cy + 22, 6, hov ? adjustBrightness(getAccentColor(), 1.2f) : getAccentColor());
            WVCMod.INSTANCE.getFontRenderer().drawCenteredString("Edit Pixels", sx + 55, cy + 6, 0xFFFFFF);
            cy += 40;
        }
        for (Setting s : m.getSettings()) {
            if (s instanceof BooleanSetting) {
                WVCMod.INSTANCE.getFontRenderer().drawString(s.getName(), sx, cy + 4, 0xFFFFFF);
                drawToggleSwitch(x + w - 60, cy, ((BooleanSetting) s).isEnabled(), false);
                cy += 32;
            } else if (s instanceof SliderSetting) {
                drawSlider((SliderSetting) s, sx, cy, w - 100, mouseX, mouseY);
                cy += 45;
            } else if (s instanceof ColorSetting) {
                drawColorPicker((ColorSetting) s, sx, cy, mouseX, mouseY);
                cy += 165;
            } else if (s instanceof KeybindSetting) {
                drawKeybind((KeybindSetting) s, sx, cy, mouseX, mouseY);
                cy += 35;
            }
        }
        detailMaxScroll = Math.max(0, (cy + detailScrollOffset) - (y + h - 15));
        disableScissor();
        drawScrollBar(x + w - 5, y + 5, h - 10, detailScrollOffset, detailMaxScroll);
    }

    private void drawScrollBar(int x, int y, int h, int offset, int max) {
        if (max <= 0) return;
        int thumbH = Math.max(20, h * h / (h + max));
        int thumbY = y + (int)((h - thumbH) * (float) offset / max);
        drawRoundedRect(x, y, x + 3, y + h, 1, 0x11FFFFFF);
        drawRoundedRect(x, thumbY, x + 3, thumbY + thumbH, 1, 0x55FFFFFF);
    }

    private void drawSlider(SliderSetting s, int x, int y, int w, int mouseX, int mouseY) {
        WVCMod.INSTANCE.getFontRenderer().drawString(s.getName() + ": " + Math.round(s.getValue() * 10.0)/10.0, x, y, 0xFFFFFF);
        int barY = y + 16;
        drawRoundedRect(x, barY, x + w, barY + 4, 2, 0x22FFFFFF);
        double p = (s.getValue() - s.getMin()) / (s.getMax() - s.getMin());
        drawRoundedRect(x, barY, x + (int)(w * p), barY + 4, 2, getAccentColor());
        drawRoundedRect(x + (int)(w * p) - 4, barY - 4, x + (int)(w * p) + 4, barY + 8, 4, 0xFFFFFFFF);
    }

    private void drawKeybind(KeybindSetting s, int x, int y, int mouseX, int mouseY) {
        WVCMod.INSTANCE.getFontRenderer().drawString(s.getName(), x, y + 4, 0xFFFFFF);
        int bx = x + 100; int bw = 80; int bh = 18;
        boolean hov = mouseX >= bx && mouseX <= bx + bw && mouseY >= y && mouseY <= y + bh;
        drawRoundedRect(bx, y, bx + bw, y + bh, 4, hov ? 0x44FFFFFF : 0x22FFFFFF);
        String text = (listeningKeybind == s) ? "Listening..." : s.getKeyName();
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString(text, bx + bw / 2, y + 5, 0xFFFFFF);
    }

    private void drawColorPicker(ColorSetting s, int x, int y, int mouseX, int mouseY) {
        WVCMod.INSTANCE.getFontRenderer().drawString(s.getName(), x, y, 0xFFFFFF);
        int px = x; int py = y + 18; int sz = 115;
        float[] h = Color.RGBtoHSB((s.getColor() >> 16) & 0xFF, (s.getColor() >> 8) & 0xFF, s.getColor() & 0xFF, null);
        for (int i = 0; i < sz; i++) for (int j = 0; j < sz; j++) func_73734_a(px + i, py + j, px + i + 1, py + j + 1, Color.HSBtoRGB(h[0], (float)i/sz, 1.0f - (float)j/sz));
        int hx = px + sz + 15;
        for (int i = 0; i < sz; i++) func_73734_a(hx, py + i, hx + 15, py + i + 1, Color.HSBtoRGB((float)i/sz, 1.0f, 1.0f));
        int tx = px + (int)(h[1]*sz); int ty = py + (int)((1.0f-h[2])*sz);
        drawRoundedRect(tx-2, ty-2, tx+2, ty+2, 2, 0xFFFFFFFF);
        int hy = py + (int)(h[0]*sz);
        func_73734_a(hx-2, hy-1, hx + 17, hy+1, 0xFFFFFFFF);
    }

    @Override
    protected void func_73864_a(int mouseX, int mouseY, int button) throws IOException {
        int px = field_146294_l / 2 - PANEL_W / 2;
        int py = field_146295_m / 2 - PANEL_H / 2;
        
        // Top Bar Tabs
        String[] tabs = {"MODS", "SETTINGS", "PROFILES"};
        for (int i = 0; i < tabs.length; i++) {
            int tx = px + 20 + i * 110;
            if (mouseX >= tx && mouseX <= tx + 100 && mouseY >= py + 10 && mouseY <= py + 35) {
                currentTab = tabs[i];
                selectedModule = null; scrollOffset = 0;
                field_146297_k.field_71439_g.func_85030_a("random.click", 0.5F, 1.0F); return;
            }
        }

        // Edit HUD button
        if (mouseX >= px + PANEL_W - 95 && mouseY >= py + 12 && mouseY <= py + 34) {
            field_146297_k.func_147108_a(new GuiEditHUD()); return;
        }

        if (currentTab.equals("MODS")) {
            // Sidebar Cats
            String[] cats = {"ALL", "MOVEMENT", "HUD", "VISUAL", "SYSTEM"};
            for (int i = 0; i < cats.length; i++) {
                int cy = py + TOP_BAR_H + 20 + i * 35;
                if (mouseX >= px + 10 && mouseX <= px + SIDEBAR_W - 10 && mouseY >= cy && mouseY <= cy + 25) {
                    selectedCategory = cats[i]; selectedModule = null; scrollOffset = 0;
                    field_146297_k.field_71439_g.func_85030_a("random.click", 0.5F, 1.0F); return;
                }
            }
            if (selectedModule != null) {
                // Back Button
                int bx = px + SIDEBAR_W + 15; int by = py + TOP_BAR_H + 10;
                if (mouseX >= bx && mouseX <= bx + 45 && mouseY >= by && mouseY <= by + 18) {
                    selectedModule = null; detailScrollOffset = 0; return;
                }
                handleInt(selectedModule, mouseX, mouseY, true);
            } else {
                List<WVCModule> mods = getFilteredModules();
                int lx = px + SIDEBAR_W + CARD_PAD;
                int ly = py + TOP_BAR_H + CARD_PAD;
                for (int i = 0; i < mods.size(); i++) {
                    int cx = lx + (i % 2) * (CARD_W + CARD_GAP);
                    int cy = ly + (i / 2) * (CARD_H + CARD_GAP) - scrollOffset;
                    if (mouseX >= cx && mouseX <= cx + CARD_W && mouseY >= cy && mouseY <= cy + CARD_H) {
                        if (mouseX >= cx + CARD_W - 50 && mouseY <= cy + 35) mods.get(i).toggle();
                        else { selectedModule = mods.get(i); detailScrollOffset = 0; }
                        field_146297_k.field_71439_g.func_85030_a("random.click", 0.5F, 1.0F); return;
                    }
                }
            }
        } else if (currentTab.equals("SETTINGS")) {
            int sx = px + 40; int cy = py + TOP_BAR_H + 80;
            Object[][] themes = {{0xFF0078D4, "Blue"}, {0xFFD40000, "Red"}, {0xFF00D455, "Green"}, {0xFFFF007F, "Pink"}, {0xFFFFD700, "Gold"}, {0xFF7F00FF, "Purple"}};
            for (int i = 0; i < themes.length; i++) {
                int tx = sx + (i % 3) * 140; int ty = cy + (i / 3) * 45;
                if (mouseX >= tx && mouseX <= tx + 120 && mouseY >= ty && mouseY <= ty + 30) {
                    WVCMod.INSTANCE.getConfig().setThemeColor((int) themes[i][0]);
                    field_146297_k.field_71439_g.func_85030_a("random.click", 0.5F, 1.2F); return;
                }
            }
        } else if (currentTab.equals("PROFILES")) {
            int sx = px + 40; int cy = py + TOP_BAR_H + 70;
            ProfileManager pm = WVCMod.INSTANCE.getProfileManager();
            List<String> profiles = pm.getProfileList();
            for (String p : profiles) {
                if (mouseX >= sx && mouseX <= sx + 200 && mouseY >= cy && mouseY <= cy + 35) {
                    pm.switchProfile(p); field_146297_k.field_71439_g.func_85030_a("random.click", 0.5F, 1.0F); return;
                }
                cy += 42;
            }
            if (mouseX >= sx && mouseX <= sx + 130 && mouseY >= cy + 10 && mouseY <= cy + 38) {
                pm.createProfile("Profile " + (profiles.size() + 1));
                field_146297_k.field_71439_g.func_85030_a("random.click", 0.5F, 1.5F); return;
            }
        }
        super.func_73864_a(mouseX, mouseY, button);
    }

    @Override protected void func_146273_a(int x, int y, int b, long t) { if (selectedModule != null) handleInt(selectedModule, x, y, false); }

    private void handleInt(WVCModule m, int mx, int my, boolean f) {
        int sx = field_146294_l/2 - PANEL_W/2 + SIDEBAR_W + 30;
        int sy = field_146295_m/2 - PANEL_H/2 + TOP_BAR_H + 40 - detailScrollOffset;
        sy += 35;
        if (m instanceof CrosshairModule) {
            if (f && mx >= sx && mx <= sx + 110 && my >= sy && my <= sy + 22) {
                field_146297_k.func_147108_a(new GuiCrosshairEditor(this, (CrosshairModule) m)); return;
            }
            sy += 40;
        }
        for (Setting s : m.getSettings()) {
            if (s instanceof BooleanSetting) {
                if (f && mx >= field_146294_l/2 + PANEL_W/2 - 60 && my >= sy && my <= sy + 20) ((BooleanSetting) s).toggle();
                sy += 32;
            } else if (s instanceof SliderSetting) {
                SliderSetting ss = (SliderSetting) s;
                int bw = PANEL_W - SIDEBAR_W - 100;
                if (mx >= sx && mx <= sx + bw && my >= sy + 16 && my <= sy + 31) ss.setValue(ss.getMin() + (double)(mx - sx) / bw * (ss.getMax() - ss.getMin()));
                sy += 45;
            } else if (s instanceof ColorSetting) {
                ColorSetting cs = (ColorSetting) s;
                float[] h = Color.RGBtoHSB((cs.getColor() >> 16) & 0xFF, (cs.getColor() >> 8) & 0xFF, cs.getColor() & 0xFF, null);
                if (mx >= sx && mx <= sx + 115 && my >= sy + 18 && my <= sy + 133) cs.setColor(Color.HSBtoRGB(h[0], (float)(mx-sx)/115, 1.0f - (float)(my-(sy+18))/115));
                else if (mx >= sx + 130 && mx <= sx + 145 && my >= sy + 18 && my <= sy + 133) cs.setColor(Color.HSBtoRGB((float)(my-(sy+18))/115, h[1], h[2]));
                sy += 165;
            } else if (s instanceof KeybindSetting) {
                int bx = sx + 100; int bw = 80; int bh = 18;
                if (f && mx >= bx && mx <= bx + bw && my >= sy && my <= sy + bh) listeningKeybind = (KeybindSetting) s;
                sy += 35;
            }
        }
    }

    private void drawRoundedRect(int x1, int y1, int x2, int y2, int r, int col) {
        Gui.func_73734_a(x1 + r, y1, x2 - r, y2, col);
        Gui.func_73734_a(x1, y1 + r, x2, y2 - r, col);
        fillCircle(x1 + r, y1 + r, r, col); fillCircle(x2 - r, y1 + r, r, col);
        fillCircle(x1 + r, y2 - r, r, col); fillCircle(x2 - r, y2 - r, r, col);
    }

    private void drawRoundedRectTop(int x1, int y1, int x2, int y2, int r, int col) {
        Gui.func_73734_a(x1, y1 + r, x2, y2, col);
        Gui.func_73734_a(x1 + r, y1, x2 - r, y1 + r, col);
        fillCircle(x1 + r, y1 + r, r, col); fillCircle(x2 - r, y1 + r, r, col);
    }

    private void drawRoundedRectLeft(int x1, int y1, int x2, int y2, int r, int col) {
        Gui.func_73734_a(x1 + r, y1, x2, y2, col);
        Gui.func_73734_a(x1, y1 + r, x1 + r, y2 - r, col);
        fillCircle(x1 + r, y1 + r, r, col); fillCircle(x1 + r, y2 - r, r, col);
    }

    private void fillCircle(int cx, int cy, int r, int col) {
        GL11.glEnable(GL11.GL_BLEND); GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f((col >> 16 & 0xFF)/255f, (col >> 8 & 0xFF)/255f, (col & 0xFF)/255f, (col >> 24 & 0xFF)/255f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN); GL11.glVertex2f(cx, cy);
        for (int i = 0; i <= 360; i += 12) GL11.glVertex2f((float)(cx + Math.cos(Math.toRadians(i)) * r), (float)(cy + Math.sin(Math.toRadians(i)) * r));
        GL11.glEnd(); GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void drawToggleSwitch(int x, int y, boolean on, boolean hovered) {
        int tw = 34; int th = 18;
        int tc = on ? getAccentColor() : 0xFF333333;
        drawRoundedRect(x, y, x + tw, y + th, 9, tc);
        int tx = on ? x + tw - 15 : x + 3;
        drawRoundedRect(tx, y + 3, tx + 12, y + 15, 6, 0xFFFFFFFF);
    }

    private void enableScissor(int x, int y, int width, int height) {
        ScaledResolution sr = new ScaledResolution(field_146297_k);
        int s = sr.func_78325_e();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x * s, field_146297_k.field_71440_d - (y + height) * s, width * s, height * s);
    }

    private void disableScissor() { GL11.glDisable(GL11.GL_SCISSOR_TEST); }
    private List<WVCModule> getFilteredModules() {
        List<WVCModule> all = WVCMod.INSTANCE.getModuleManager().getModules();
        if (selectedCategory.equals("ALL")) return all;
        List<WVCModule> res = new ArrayList<>();
        for (WVCModule m : all) if (getCategoryLabel(m).equalsIgnoreCase(selectedCategory)) res.add(m);
        return res;
    }
    private String getCategoryLabel(WVCModule m) {
        String n = m.getName();
        if (n.equals("Sprint") || n.equals("Zoom") || n.equals("ToggleSneak")) return "MOVEMENT";
        if (n.equals("FPS") || n.equals("Ping") || n.equals("CPS") || n.equals("Keystrokes") || n.equals("ArmorStatus") || n.equals("PotionStatus") || n.equals("ReachDisplay") || n.equals("DirectionHUD") || n.equals("Bossbar") || n.equals("Scoreboard")) return "HUD";
        if (n.equals("JapaneseIME") || n.equals("ScreenshotManager") || n.equals("ChatConfirmation") || n.equals("AutoText")) return "SYSTEM";
        return "VISUAL";
    }
    private int adjustBrightness(int color, float factor) {
        int r = Math.min(255, (int)(((color >> 16) & 0xFF) * factor));
        int g = Math.min(255, (int)(((color >> 8) & 0xFF) * factor));
        int b = Math.min(255, (int)((color & 0xFF) * factor));
        return (color & 0xFF000000) | (r << 16) | (g << 8) | b;
    }
    @Override public void func_146274_d() throws IOException {
        super.func_146274_d();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) { 
            if (selectedModule != null) detailScrollOffset = Math.max(0, Math.min(detailScrollOffset - wheel / 3, detailMaxScroll));
            else scrollOffset = Math.max(0, Math.min(scrollOffset - wheel / 3, maxScrollOffset)); 
        }
    }
    @Override protected void func_73869_a(char typedChar, int keyCode) throws IOException {
        if (listeningKeybind != null) {
            if (keyCode == Keyboard.KEY_ESCAPE) listeningKeybind.setKeyCode(0);
            else listeningKeybind.setKeyCode(keyCode);
            listeningKeybind = null; return;
        }
        super.func_73869_a(typedChar, keyCode);
    }
    @Override public void func_146281_b() { WVCMod.INSTANCE.getProfileManager().saveCurrentProfile(); super.func_146281_b(); }
    @Override public boolean func_73868_f() { return false; }
}
