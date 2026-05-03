package dev.windv.wvc.module.visual;

import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.BooleanSetting;
import dev.windv.wvc.settings.ColorSetting;
import dev.windv.wvc.settings.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Crosshair Static
 * OpenGLの状態リークを修正し、弓の使用時も安定して動作するように改善。
 */
public class CrosshairModule extends WVCModule {

    private final Minecraft mc = Minecraft.getMinecraft();
    
    private final BooleanSetting customMode;
    private final SliderSetting scale;
    private final SliderSetting length;
    private final SliderSetting thickness;
    private final SliderSetting gap;
    private final ColorSetting color;

    private final boolean[] dots = new boolean[256];

    public CrosshairModule() {
        super("Crosshair", true);
        this.addSetting(customMode = new BooleanSetting("Custom Mode", false));
        this.addSetting(scale = new SliderSetting("Pixel Scale", 1.0, 0.1, 5.0, false));
        this.addSetting(length = new SliderSetting("Length", 4.0, 0.5, 20.0, false));
        this.addSetting(thickness = new SliderSetting("Thickness", 1.0, 0.5, 5.0, false));
        this.addSetting(gap = new SliderSetting("Gap", 2.0, 0.0, 15.0, false));
        this.addSetting(color = new ColorSetting("Color", 0, 255, 0));

        // 初期十字
        dots[119] = dots[120] = dots[121] = true;
        dots[104] = dots[136] = true;
    }

    @SubscribeEvent
    public void onRenderCrosshair(RenderGameOverlayEvent.Pre event) {
        if (!this.isEnabled()) return;

        if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            event.setCanceled(true); // バニラの描画をキャンセル
            
            ScaledResolution sr = new ScaledResolution(mc);
            double centerX = sr.getScaledWidth_double() / 2.0;
            double centerY = sr.getScaledHeight_double() / 2.0;
            
            // レンダリング状態の保存
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            
            // 色の設定
            int c = color.getColor();
            float r = (float)(c >> 16 & 255) / 255.0F;
            float g = (float)(c >> 8 & 255) / 255.0F;
            float b = (float)(c & 255) / 255.0F;
            float a = (float)(c >> 24 & 255) / 255.0F;
            GlStateManager.color(r, g, b, a);

            if (customMode.isEnabled()) {
                double s = scale.getValue();
                int grid = 16;
                double offset = (grid * s) / 2.0;
                
                for (int i = 0; i < 256; i++) {
                    if (dots[i]) {
                        int dx_idx = i % grid;
                        int dy_idx = i / grid;
                        double xPos = centerX - offset + (dx_idx * s);
                        double yPos = centerY - offset + (dy_idx * s);
                        drawRectDouble(xPos, yPos, xPos + s, yPos + s);
                    }
                }
            } else {
                double l = length.getValue();
                double t = thickness.getValue() / 2.0;
                double gp = gap.getValue();

                drawRectDouble(centerX - t, centerY - gp - l, centerX + t, centerY - gp);
                drawRectDouble(centerX - t, centerY + gp, centerX + t, centerY + gp + l);
                drawRectDouble(centerX - gp - l, centerY - t, centerX - gp, centerY + t);
                drawRectDouble(centerX + gp, centerY - t, centerX + gp + l, centerY + t);
            }
            
            // 状態のリセット（重要：これがないとハートが緑になったりする）
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private void drawRectDouble(double left, double top, double right, double bottom) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
    }

    public boolean[] getDots() { return dots; }
    public void setDot(int index, boolean state) {
        if (index >= 0 && index < dots.length) {
            dots[index] = state;
            // ドットが編集されたら自動的にカスタムモードをオンにする
            customMode.setEnabled(true);
        }
    }
}
