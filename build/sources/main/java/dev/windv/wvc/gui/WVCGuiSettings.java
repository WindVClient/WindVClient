package dev.windv.wvc.gui;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.module.visual.CrosshairModule;
import dev.windv.wvc.profile.ProfileManager;
import dev.windv.wvc.settings.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class WVCGuiSettings extends GuiScreen {

    private static final int PANEL_W = 640;
    private static final int PANEL_H = 400;
    private static final int TOP_BAR_H = 50;
    private static final int SIDEBAR_W = 130;
    private static final int CARD_W = 210;
    private static final int CARD_H = 80;
    private static final int CARD_GAP = 12;
    private static final int CARD_PAD = 15;

    private static final int COL_SIDEBAR = 0xFF121212;
    private static final int COL_BG = 0xFF181818;
    private static final int COL_CARD = 0xFF202020;
    private static final int COL_CARD_HOVER = 0xFF282828;

    private String currentTab = "MODS";
    private String currentCategory = "ALL";
    private String searchQuery = "";
    private boolean listeningSearch = false;
    private WVCModule selectedModule = null;
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private int detailScrollOffset = 0;
    private int detailMaxScroll = 0;

    @Override
    public void initGui() {
        currentTab = dev.windv.wvc.util.LanguageManager.get("gui.mods");
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int px = width / 2 - PANEL_W / 2;
        int py = height / 2 - PANEL_H / 2;

        drawDefaultBackground();
        drawRect(px, py, px + PANEL_W, py + PANEL_H, COL_BG);

        // --- トップバー ---
        drawRect(px, py, px + PANEL_W, py + TOP_BAR_H, COL_SIDEBAR);
        String[] tabs = {
            dev.windv.wvc.util.LanguageManager.get("gui.mods"),
            dev.windv.wvc.util.LanguageManager.get("gui.settings"),
            dev.windv.wvc.util.LanguageManager.get("gui.profiles")
        };
        int tabW = 110;
        for (int i = 0; i < tabs.length; i++) {
            int tx = px + 25 + i * (tabW + 15);
            int ty = py + 12;
            boolean sel = currentTab.equals(tabs[i]);
            if (sel) drawRect(tx, ty, tx + tabW, ty + 38, getAccentColor());
            WVCMod.INSTANCE.getFontRenderer().drawCenteredString(tabs[i], tx + tabW / 2, ty + 9, sel ? 0xFFFFFFFF : 0xFF888888);
        }

        // --- Edit HUD ---
        int ehX = px + PANEL_W - 110;
        drawRect(ehX, py + 13, ehX + 90, py + 37, getAccentColor());
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString(dev.windv.wvc.util.LanguageManager.get("gui.edit_hud"), ehX + 45, py + 21, 0xFFFFFFFF);

        // --- 検索 ---
        int sx = px + PANEL_W - 265;
        drawRect(sx, py + 13, sx + 140, py + 37, 0x22FFFFFF);
        String hint = dev.windv.wvc.util.LanguageManager.get("gui.search");
        String display = searchQuery.isEmpty() ? (listeningSearch ? "" : hint) : searchQuery;
        WVCMod.INSTANCE.getFontRenderer().drawString(display, sx + 10, py + 21, searchQuery.isEmpty() ? 0xFFAAAAAA : 0xFFFFFFFF);

        // --- コンテンツ ---
        int contentY = py + TOP_BAR_H;
        int contentH = PANEL_H - TOP_BAR_H;

        if (selectedModule != null) {
            drawModuleSettings(selectedModule, px, contentY, PANEL_W, contentH, mouseX, mouseY);
            boolean backHov = mouseX >= px + 10 && mouseX <= px + 70 && mouseY >= contentY + 10 && mouseY <= contentY + 30;
            WVCMod.INSTANCE.getFontRenderer().drawString(dev.windv.wvc.util.LanguageManager.get("gui.back"), px + 15, contentY + 15, backHov ? getAccentColor() : 0xFFBBBBBB);
        } else {
            if (currentTab.equals(dev.windv.wvc.util.LanguageManager.get("gui.mods"))) {
                drawRect(px, contentY, px + SIDEBAR_W, py + PANEL_H, COL_SIDEBAR);
                drawCategorySidebar(px, contentY, SIDEBAR_W, contentH, mouseX, mouseY);
                drawModuleList(px + SIDEBAR_W, contentY, PANEL_W - SIDEBAR_W, contentH, mouseX, mouseY);
            } else if (currentTab.equals(dev.windv.wvc.util.LanguageManager.get("gui.settings"))) {
                drawGlobalSettings(px, contentY, PANEL_W, contentH, mouseX, mouseY);
            } else if (currentTab.equals(dev.windv.wvc.util.LanguageManager.get("gui.profiles"))) {
                drawProfiles(px, contentY, PANEL_W, contentH, mouseX, mouseY);
            }
        }
        
        handleScrolling(mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawModuleList(int x, int y, int w, int h, int mx, int my) {
        List<WVCModule> mods = getFilteredModules();
        int lx = x + CARD_PAD;
        int ly = y + CARD_PAD;
        maxScrollOffset = Math.max(0, (((mods.size() + 1) / 2) * (CARD_H + CARD_GAP) + CARD_PAD) - h);
        enableScissor(x, y, w, h);
        for (int i = 0; i < mods.size(); i++) {
            WVCModule m = mods.get(i);
            int cx = lx + (i % 2) * (CARD_W + CARD_GAP);
            int cy = ly + (i / 2) * (CARD_H + CARD_GAP) - scrollOffset;
            if (cy + CARD_H < y || cy > y + h) continue;
            boolean hov = mx >= cx && mx <= cx + CARD_W && my >= cy && my <= cy + CARD_H;
            drawRect(cx, cy, cx + CARD_W, cy + CARD_H, hov ? COL_CARD_HOVER : COL_CARD);
            if (m.isEnabled()) drawRect(cx, cy, cx + 4, cy + CARD_H, getAccentColor());
            WVCMod.INSTANCE.getFontRenderer().drawString(m.getName(), cx + 18, cy + 15, 0xFFFFFFFF);
            drawToggleSwitch(cx + CARD_W - 45, cy + 15, m.isEnabled());
        }
        disableScissor();
    }

    private void drawModuleSettings(WVCModule m, int x, int y, int w, int h, int mx, int my) {
        int sx = x + 100;
        int sy = y + 45 - detailScrollOffset;
        enableScissor(x, y, w, h);
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow(m.getName(), sx, sy, 0xFFFFFF);
        sy += 40;
        if (m instanceof CrosshairModule) {
            drawRect(sx, sy, sx + 120, sy + 24, getAccentColor());
            WVCMod.INSTANCE.getFontRenderer().drawCenteredString("Edit Pixels", sx + 60, sy + 8, 0xFFFFFFFF);
            sy += 45;
        }
        for (Setting s : m.getSettings()) {
            if (s instanceof BooleanSetting) {
                WVCMod.INSTANCE.getFontRenderer().drawString(s.getName(), sx, sy + 5, 0xFFFFFFFF);
                drawToggleSwitch(x + w - 100, sy, ((BooleanSetting) s).isEnabled());
                sy += 35;
            } else if (s instanceof SliderSetting) {
                drawSlider((SliderSetting) s, sx, sy, w - 200, mx, my);
                sy += 50;
            } else if (s instanceof ColorSetting) {
                drawColorPicker((ColorSetting) s, sx, sy, w - 200, mx, my);
                sy += 160;
            }
        }
        detailMaxScroll = Math.max(0, sy + detailScrollOffset - (y + h - 20));
        disableScissor();
    }

    private void drawColorPicker(ColorSetting s, int x, int y, int w, int mx, int my) {
        WVCMod.INSTANCE.getFontRenderer().drawString(s.getName(), x, y, 0xFFFFFFFF);
        float[] hsb = Color.RGBtoHSB((s.getColor() >> 16) & 0xFF, (s.getColor() >> 8) & 0xFF, s.getColor() & 0xFF, null);
        int px = x; int py = y + 20; int boxSize = 100;

        // プレビュー
        drawRect(px, py, px + 25, py + boxSize, s.getColor() | 0xFF000000);

        // --- S/B ボックス: 列ごとにHSB計算して正確な色を描画 ---
        int bx = px + 35;
        for (int col = 0; col < boxSize; col++) {
            float sat = (float) col / (boxSize - 1);
            for (int row = 0; row < boxSize; row++) {
                float bri = 1.0f - (float) row / (boxSize - 1);
                int c = Color.HSBtoRGB(hsb[0], sat, bri) | 0xFF000000;
                drawRect(bx + col, py + row, bx + col + 1, py + row + 1, c);
            }
        }

        // S/B インジケーター（現在位置を白い点で表示）
        int indX = bx + (int)(hsb[1] * (boxSize - 1));
        int indY = py + (int)((1 - hsb[2]) * (boxSize - 1));
        drawRect(indX - 3, indY - 1, indX + 3, indY + 1, 0xFFFFFFFF);
        drawRect(indX - 1, indY - 3, indX + 1, indY + 3, 0xFFFFFFFF);

        // --- 縦型 Hue バー ---
        int hx = bx + boxSize + 10;
        for (int i = 0; i < boxSize; i++) {
            int hCol = Color.HSBtoRGB((float) i / (boxSize - 1), 1.0f, 1.0f) | 0xFF000000;
            drawRect(hx, py + i, hx + 15, py + i + 1, hCol);
        }
        int hy = py + (int)(hsb[0] * (boxSize - 1));
        drawRect(hx - 2, hy - 1, hx + 17, hy + 1, 0xFFFFFFFF);
    }

    private void handleColorClick(ColorSetting s, int x, int y, int w, int mx, int my) {
        int py = y + 20; int boxSize = 100; int bx = x + 35; int hx = bx + boxSize + 10;
        float[] hsb = Color.RGBtoHSB((s.getColor() >> 16) & 0xFF, (s.getColor() >> 8) & 0xFF, s.getColor() & 0xFF, null);

        if (mx >= hx && mx <= hx + 15 && my >= py && my <= py + boxSize) {
            hsb[0] = MathHelper.clamp_float((float)(my - py) / boxSize, 0, 0.999f);
        } else if (mx >= bx && mx <= bx + boxSize && my >= py && my <= py + boxSize) {
            hsb[1] = MathHelper.clamp_float((float)(mx - bx) / boxSize, 0, 1);
            hsb[2] = MathHelper.clamp_float(1.0f - (float)(my - py) / boxSize, 0, 1);
        } else { return; }
        s.setColor(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (listeningSearch) {
            if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RETURN) { listeningSearch = false; }
            else if (keyCode == Keyboard.KEY_BACK) { if (!searchQuery.isEmpty()) searchQuery = searchQuery.substring(0, searchQuery.length() - 1); }
            else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) { searchQuery += typedChar; }
            scrollOffset = 0; return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mx, int my, int btn) throws IOException {
        int px = width / 2 - PANEL_W / 2; int py = height / 2 - PANEL_H / 2;
        int ehX = px + PANEL_W - 110;
        if (mx >= ehX && mx <= ehX + 90 && my >= py + 13 && my <= py + 37) { mc.displayGuiScreen(new GuiEditHUD()); return; }
        int sx = px + PANEL_W - 265;
        if (mx >= sx && mx <= sx + 140 && my >= py + 13 && my <= py + 37) { listeningSearch = true; return; } else { listeningSearch = false; }
        
        for (int i = 0; i < 3; i++) {
            int tx = px + 25 + i * 125;
            if (mx >= tx && mx <= tx + 110 && my >= py + 12 && my <= py + 38) {
                String[] ts = {dev.windv.wvc.util.LanguageManager.get("gui.mods"), dev.windv.wvc.util.LanguageManager.get("gui.settings"), dev.windv.wvc.util.LanguageManager.get("gui.profiles")};
                currentTab = ts[i]; selectedModule = null; return;
            }
        }
        if (selectedModule != null) {
            if (mx >= px + 10 && mx <= px + 70 && my >= py + TOP_BAR_H + 10 && my <= py + TOP_BAR_H + 30) { selectedModule = null; return; }
            handleSettingsInt(selectedModule, px + 100, py + TOP_BAR_H + 45, PANEL_W - 200, mx, my, true);
        } else {
            if (currentTab.equals(dev.windv.wvc.util.LanguageManager.get("gui.mods"))) handleModuleListClick(px, py + TOP_BAR_H, mx, my);
            else if (currentTab.equals(dev.windv.wvc.util.LanguageManager.get("gui.settings"))) handleGlobalClick(px + 40, py + TOP_BAR_H + 40, mx, my);
        }
        super.mouseClicked(mx, my, btn);
    }

    @Override
    protected void mouseClickMove(int mx, int my, int btn, long time) {
        if (selectedModule != null) {
            int px = width / 2 - PANEL_W / 2; int py = height / 2 - PANEL_H / 2;
            handleSettingsInt(selectedModule, px + 100, py + TOP_BAR_H + 45, PANEL_W - 200, mx, my, false);
        }
    }

    private void handleSettingsInt(WVCModule m, int x, int y, int w, int mx, int my, boolean click) {
        int cy = y + 40 - detailScrollOffset;
        if (m instanceof CrosshairModule) cy += 45;
        for (Setting s : m.getSettings()) {
            if (s instanceof BooleanSetting) {
                if (click && mx >= x + w && mx <= x + w + 30 && my >= cy && my <= cy + 20) ((BooleanSetting) s).toggle();
                cy += 35;
            } else if (s instanceof SliderSetting) {
                if (mx >= x && mx <= x + w && my >= cy + 15 && my <= cy + 35) {
                    float val = (float)(mx - x) / w;
                    ((SliderSetting) s).setValue(((SliderSetting) s).getMin() + val * (((SliderSetting) s).getMax() - ((SliderSetting) s).getMin()));
                }
                cy += 50;
            } else if (s instanceof ColorSetting) { handleColorClick((ColorSetting) s, x, cy, w, mx, my); cy += 160; }
        }
    }

    private void handleModuleListClick(int px, int py, int mx, int my) {
        int catsY = py + 25;
        String[] cats = {"ALL", "MOVEMENT", "HUD", "VISUAL", "SYSTEM"};
        for (String c : cats) {
            if (mx >= px && mx <= px + SIDEBAR_W && my >= catsY && my <= catsY + 30) { currentCategory = c; return; }
            catsY += 35;
        }
        List<WVCModule> mods = getFilteredModules();
        for (int i = 0; i < mods.size(); i++) {
            int cx = px + SIDEBAR_W + CARD_PAD + (i % 2) * (CARD_W + CARD_GAP);
            int cy = py + CARD_PAD + (i / 2) * (CARD_H + CARD_GAP) - scrollOffset;
            if (mx >= cx && mx <= cx + CARD_W && my >= cy && my <= cy + CARD_H) {
                if (mx >= cx + CARD_W - 50) mods.get(i).toggle();
                else selectedModule = mods.get(i);
                return;
            }
        }
    }

    private void handleGlobalClick(int x, int y, int mx, int my) {
        int sy = y + 75;
        for (int i = 0; i < 6; i++) {
            if (mx >= x + i*45 && mx <= x + i*45 + 35 && my >= sy && my <= sy + 35) {
                WVCMod.INSTANCE.getConfig().setThemeColor(new int[]{0xFF0078D4, 0xFFD40000, 0xFF00D455, 0xFFFF007F, 0xFFFFD700, 0xFF7F00FF}[i]); return;
            }
        }
        if (mx >= x && mx <= x + 90 && my >= sy + 95 && my <= sy + 119) WVCMod.INSTANCE.getConfig().setLanguage("en");
        if (mx >= x + 105 && mx <= x + 195 && my >= sy + 95 && my <= sy + 119) WVCMod.INSTANCE.getConfig().setLanguage("jp");
    }

    private List<WVCModule> getFilteredModules() {
        return WVCMod.INSTANCE.getModuleManager().getModules().stream()
            .filter(m -> currentCategory.equals("ALL") || m.getClass().getPackage().getName().contains(currentCategory.toLowerCase()))
            .filter(m -> searchQuery.isEmpty() || m.getName().toLowerCase().contains(searchQuery.toLowerCase()))
            .collect(Collectors.toList());
    }

    private void handleScrolling(int mx, int my) {
        int wheel = Mouse.getDWheel();
        if (wheel != 0) {
            if (selectedModule != null) detailScrollOffset = MathHelper.clamp_int(detailScrollOffset - (wheel / 2), 0, detailMaxScroll);
            else scrollOffset = MathHelper.clamp_int(scrollOffset - (wheel / 2), 0, maxScrollOffset);
        }
    }

    private void drawGlobalSettings(int x, int y, int w, int h, int mx, int my) {
        int sx = x + 40; int sy = y + 40;
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow("Global Settings", sx, sy, 0xFFFFFF);
        sy += 45; WVCMod.INSTANCE.getFontRenderer().drawString("Theme Color", sx, sy, 0xBBFFFFFF);
        sy += 30; int[] colors = {0xFF0078D4, 0xFFD40000, 0xFF00D455, 0xFFFF007F, 0xFFFFD700, 0xFF7F00FF};
        for (int i = 0; i < 6; i++) {
            drawRect(sx + i*45, sy, sx + i*45 + 35, sy + 35, colors[i]);
            if (getAccentColor() == colors[i]) drawRect(sx + i*45 + 4, sy + 4, sx + i*45 + 31, sy + 31, 0x66FFFFFF);
        }
        sy += 70; WVCMod.INSTANCE.getFontRenderer().drawString("Language", sx, sy, 0xFFFFFF);
        sy += 25; boolean en = WVCMod.INSTANCE.getConfig().getLanguage().equals("en");
        drawRect(sx, sy, sx + 90, sy + 24, en ? getAccentColor() : 0x22FFFFFF);
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString("English", sx + 45, sy + 8, 0xFFFFFFFF);
        boolean jp = WVCMod.INSTANCE.getConfig().getLanguage().equals("jp");
        drawRect(sx + 105, sy, sx + 195, sy + 24, jp ? getAccentColor() : 0x22FFFFFF);
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString("\u65e5\u672c\u8a9e", sx + 150, sy + 8, 0xFFFFFFFF);
    }

    private void drawProfiles(int x, int y, int w, int h, int mx, int my) {
        int sx = x + 40; int sy = y + 40;
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow("Profiles", sx, sy, 0xFFFFFF);
        sy += 45; ProfileManager pm = WVCMod.INSTANCE.getProfileManager();
        for (String p : pm.getProfileList()) {
            boolean sel = pm.getCurrentProfile().equals(p);
            drawRect(sx, sy, sx + 220, sy + 35, sel ? getAccentColor() : 0x22FFFFFF);
            WVCMod.INSTANCE.getFontRenderer().drawString(p, sx + 20, sy + 13, 0xFFFFFFFF);
            sy += 45;
        }
    }

    private void drawCategorySidebar(int x, int y, int w, int h, int mx, int my) {
        String[] cats = {"ALL", "MOVEMENT", "HUD", "VISUAL", "SYSTEM"};
        int cy = y + 25;
        for (String c : cats) {
            boolean sel = currentCategory.equals(c);
            if (sel) { drawRect(x, cy, x + SIDEBAR_W, cy + 30, 0x15FFFFFF); drawRect(x, cy, x + 3, cy + 30, getAccentColor()); }
            WVCMod.INSTANCE.getFontRenderer().drawString(c, x + 20, cy + 10, sel ? getAccentColor() : 0xFF777777);
            cy += 35;
        }
    }

    private void drawToggleSwitch(int x, int y, boolean on) {
        drawRect(x, y, x + 30, y + 15, on ? getAccentColor() : 0xFF333333);
        drawRect(on ? x + 16 : x + 2, y + 2, on ? x + 28 : x + 14, y + 13, 0xFFFFFFFF);
    }

    private void drawSlider(SliderSetting s, int x, int y, int w, int mx, int my) {
        WVCMod.INSTANCE.getFontRenderer().drawString(s.getName() + ": " + String.format("%.1f", s.getValue()), x, y, 0xFFFFFFFF);
        drawRect(x, y + 22, x + w, y + 25, 0x44FFFFFF);
        float p = (float) ((s.getValue() - s.getMin()) / (s.getMax() - s.getMin()));
        drawRect(x, y + 22, x + (int)(w * p), y + 25, getAccentColor());
        drawRect(x + (int)(w * p) - 4, y + 18, x + (int)(w * p) + 4, y + 29, 0xFFFFFFFF);
    }

    private int getAccentColor() { return WVCMod.INSTANCE.getConfig().getThemeColor(); }
    private void enableScissor(int x, int y, int w, int h) {
        int f = new ScaledResolution(mc).getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x * f, (mc.displayHeight - (y + h) * f), w * f, h * f);
    }
    private void disableScissor() { GL11.glDisable(GL11.GL_SCISSOR_TEST); }
    @Override public boolean doesGuiPauseGame() { return false; }
}
