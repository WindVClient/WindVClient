package dev.windv.wvc.render;

import dev.windv.wvc.WVCMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * WVC HUD 描画エンジン
 *
 * デザインコンセプト：サイバー系（青×紫）半透明パネル
 *
 * レイアウト：
 *   +------------------+
 *   | FPS: 120         |  <- ROW 0
 *   | CPS: 8           |  <- ROW 1
 *   | Ping: 30ms       |  <- ROW 2
 *   +------------------+
 *
 * 軽量化のため：
 *  - テクスチャバインドなし（純粋なGL四角形描画）
 *  - 不要なGLステート変更を最小限に
 *  - パネルはROW 0の描画時に1回だけ描く
 */
public class HudRenderer {

    // ロゴのリソースパス
    private static final ResourceLocation LOGO_LOC = new ResourceLocation("windvclient", "logo.png");

    // キャッシュ用
    private static net.minecraft.client.gui.ScaledResolution cachedSr;
    private static int lastWidth, lastHeight;

    // --- HUDカラーパレット（ARGB形式） ---
    // 背景パネル：紫がかった深いネイビー、半透明
    private static final int COLOR_PANEL_BG     = 0xBB0A0A1E;  // 濃紺ほぼ黒、75%不透明
    private static final int COLOR_PANEL_BORDER  = 0xFF3A00FF;  // 鮮やかな青紫のボーダー

    // テキストカラー
    private static final int COLOR_LABEL         = 0xFF8888FF;  // 薄い青紫（ラベル部分）
    private static final int COLOR_VALUE_GOOD    = 0xFF00EEFF;  // シアン（良い値）
    private static final int COLOR_VALUE_WARN    = 0xFFFFCC00;  // 黄色（警告値）
    private static final int COLOR_VALUE_BAD     = 0xFFFF3355;  // 赤（悪い値）

    // --- レイアウト定数 ---
    private static final int PADDING       = 4;   // パネル内余白(px)
    private static final int LINE_HEIGHT   = 10;  // 行の高さ(px)
    private static final int PANEL_WIDTH   = 72;  // パネル幅(px)
    private static final int BORDER_SIZE   = 1;   // ボーダー太さ(px)

    // HUD行の数（後から動的に変えられるようにする）
    private static int activeRows = 0;

    /**
     * 指定行にHUDテキストを描画する。
     * ROW=0の場合、パネル背景も描画する（フレームごとに一度だけ描く）。
     *
     * @param row   行番号（0始まり）
     * @param text  表示するテキスト（例: "FPS: 120"）
     */
    public static void renderHudLine(int row, String text) {
        Minecraft mc = Minecraft.func_71410_x();
        FontRenderer fr = mc.field_71466_p;

        int x = WVCMod.INSTANCE.getConfig().getHudX();
        int y = WVCMod.INSTANCE.getConfig().getHudY();

        // 行数カウントを更新（最大ROWを追跡してパネルサイズを決める）
        if (row >= activeRows) activeRows = row + 1;

        // ROW 0のときにパネル背景を描画
        if (row == 0) {
            int panelH = PADDING * 2 + LINE_HEIGHT * activeRows + (activeRows - 1) * 2;
            drawPanel(x, y, PANEL_WIDTH, panelH);
            // フレームごとにカウントをリセット
            activeRows = 0;
        }

        // テキストのY座標
        int textY = y + PADDING + row * (LINE_HEIGHT + 2);

        // テキストをコロンで分割して色付け描画
        int colonIdx = text.indexOf(':');
        if (colonIdx >= 0) {
            String label = text.substring(0, colonIdx + 1);
            String value = text.substring(colonIdx + 1);

            // ラベル（青紫）
            WVCMod.INSTANCE.getFontRenderer().drawString(label, x + PADDING, textY, COLOR_LABEL);

            // 値（内容に応じて色を変える）
            int valueColor = getValueColor(value.trim(), label);
            WVCMod.INSTANCE.getFontRenderer().drawString(value, x + PADDING + WVCMod.INSTANCE.getFontRenderer().getStringWidth(label), textY, valueColor);
        } else {
            WVCMod.INSTANCE.getFontRenderer().drawString(text, x + PADDING, textY, COLOR_VALUE_GOOD);
        }
    }

    /**
     * 半透明パネルをGL11で描画する（テクスチャなし）
     */
    private static void drawPanel(int x, int y, int w, int h) {
        GlStateManager.func_179094_E();
        GlStateManager.func_179090_x();
        GlStateManager.func_179147_l();
        GlStateManager.func_179112_b(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // ボーダー（外枠）を描画
        drawRect(x - BORDER_SIZE, y - BORDER_SIZE,
                 x + w + BORDER_SIZE, y + h + BORDER_SIZE,
                 COLOR_PANEL_BORDER);

        // 背景パネルを描画
        drawRect(x, y, x + w, y + h, COLOR_PANEL_BG);

        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
        GlStateManager.func_179121_F();
    }

    /**
     * 矩形を描画するユーティリティ（ARGB色）
     */
    private static void drawRect(int x1, int y1, int x2, int y2, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >>  8) & 0xFF) / 255.0f;
        float b = ( color        & 0xFF) / 255.0f;

        GL11.glColor4f(r, g, b, a);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y2);
        GL11.glVertex2f(x2, y2);
        GL11.glVertex2f(x2, y1);
        GL11.glEnd();
    }

    /**
     * 画面左下にロゴを描画する。
     * HUDだけでなくGUI（インベントリ）等でも呼び出せるように設計。
     */
    public static void renderLogo() {
        Minecraft mc = Minecraft.func_71410_x();
        
        // 解像度キャッシュ更新
        if (cachedSr == null || mc.field_71443_c != lastWidth || mc.field_71440_d != lastHeight) {
            cachedSr = new net.minecraft.client.gui.ScaledResolution(mc);
            lastWidth = mc.field_71443_c;
            lastHeight = mc.field_71440_d;
        }
        
        int guiWidth = cachedSr.func_78326_a();
        int guiHeight = cachedSr.func_78328_b();

        // ロゴの比率を 2:1 に戻す
        int logoW = 120;
        int logoH = 60;
        int margin = 5;

        // 左下に配置
        int x = margin;
        int y = guiHeight - logoH - margin;

        GlStateManager.func_179094_E();
        GlStateManager.func_179147_l();
        GlStateManager.func_179112_b(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);

        mc.func_110434_K().func_110577_a(LOGO_LOC);
        
        // 2:1 の比率で描画
        net.minecraft.client.gui.Gui.func_146110_a(x, y, 0, 0, logoW, logoH, logoW, logoH);

        GlStateManager.func_179084_k();
        GlStateManager.func_179121_F();
    }

    /**
     * 値の内容に応じてテキストカラーを返す
     * FPS: 60以上=シアン, 30以上=黄, 以下=赤
     * CPS: 常にシアン
     * Ping: 100ms以下=シアン, 200ms以下=黄, 以上=赤
     */
    private static int getValueColor(String value, String label) {
        try {
            if (label.equalsIgnoreCase("FPS:")) {
                int fps = Integer.parseInt(value.trim());
                if (fps >= 60) return COLOR_VALUE_GOOD;
                if (fps >= 30) return COLOR_VALUE_WARN;
                return COLOR_VALUE_BAD;
            }
            if (label.equalsIgnoreCase("Ping:")) {
                // "30ms" から数字だけ取り出す
                String numStr = value.trim().replace("ms", "").replace("LAN", "-1");
                int ping = Integer.parseInt(numStr);
                if (ping < 0)   return COLOR_VALUE_GOOD;  // LAN
                if (ping <= 100) return COLOR_VALUE_GOOD;
                if (ping <= 200) return COLOR_VALUE_WARN;
                return COLOR_VALUE_BAD;
            }
        } catch (NumberFormatException ignored) {}
        return COLOR_VALUE_GOOD;
    }
}
