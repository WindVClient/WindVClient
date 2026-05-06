package dev.windv.wvc.util.font;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class CFontRenderer extends CFont {

    public CFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
    }

    public float drawStringWithShadow(String text, double x, double y, int color) {
        float shadowWidth = drawString(text, x + 0.5D, y + 0.5D, color, true);
        return Math.max(shadowWidth, drawString(text, x, y, color, false));
    }

    public float drawString(String text, float x, float y, int color) {
        return drawString(text, x, y, color, false);
    }

    public float drawCenteredString(String text, float x, float y, int color) {
        return drawString(text, x - getStringWidth(text) / 2f, y, color);
    }

    public float drawString(String text, double x, double y, int color, boolean shadow) {
        x -= 1;
        if (text == null) return 0.0f;
        if (color == 553648127) color = 16777215;
        if ((color & 0xFC000000) == 0) color |= 0xFF000000;
        if (shadow) color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;

        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;

        GlStateManager.func_179131_c(red, green, blue, alpha);
        
        double currentX = x * 2;
        double currentY = y * 2;

        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 0.5);
        GlStateManager.func_179147_l();
        GlStateManager.func_179112_b(770, 771);
        GlStateManager.func_179098_w();
        GlStateManager.func_179144_i(tex.func_110552_b());

        GL11.glBegin(GL11.GL_QUADS);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < charData.length) {
                drawChar(charData, c, (float) currentX, (float) currentY);
                currentX += charData[c].width - 8 + charOffset;
            } else {
                // Unicode fallback: Use Minecraft's default font renderer for non-ASCII characters
                GL11.glEnd();
                GL11.glPushMatrix();
                GL11.glScaled(2.0, 2.0, 2.0);
                // Ensure color is correctly applied for vanilla draw
                GlStateManager.func_179131_c(red, green, blue, alpha);
                net.minecraft.client.Minecraft.func_71410_x().field_71466_p.func_175065_a(String.valueOf(c), (float) (currentX / 2.0), (float) (currentY / 2.0), color, false);
                GL11.glPopMatrix();
                
                // Restore state for CFont
                currentX += net.minecraft.client.Minecraft.func_71410_x().field_71466_p.func_78263_a(c) * 2;
                GlStateManager.func_179144_i(tex.func_110552_b());
                GlStateManager.func_179147_l();
                GlStateManager.func_179131_c(red, green, blue, alpha);
                GL11.glBegin(GL11.GL_QUADS);
            }
        }
        GL11.glEnd();
        
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        return (float) x + getStringWidth(text);
    }

    @Override
    public int getStringWidth(String text) {
        if (text == null) return 0;
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < charData.length) {
                width += charData[c].width - 8 + charOffset;
            } else {
                // Unicode fallback width calculation
                width += net.minecraft.client.Minecraft.func_71410_x().field_71466_p.func_78263_a(c) * 2;
            }
        }
        return width / 2;
    }
}
