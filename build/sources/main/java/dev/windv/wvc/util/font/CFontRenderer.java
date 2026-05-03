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

        GlStateManager.color(red, green, blue, alpha);
        
        double currentX = x * 2;
        double currentY = y * 2;

        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 0.5);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(tex.getGlTextureId());

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
                GL11.glScaled(2.0, 2.0, 2.0); // CFont is scaled at 0.5, so we scale back for vanilla
                net.minecraft.client.Minecraft.getMinecraft().fontRendererObj.drawString(String.valueOf(c), (float) (currentX / 2.0), (float) (currentY / 2.0), color, false);
                GL11.glPopMatrix();
                currentX += net.minecraft.client.Minecraft.getMinecraft().fontRendererObj.getCharWidth(c) * 2;
                GlStateManager.bindTexture(tex.getGlTextureId());
                GlStateManager.enableBlend();
                GL11.glBegin(GL11.GL_QUADS);
            }
        }
        GL11.glEnd();
        
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        // バニラのFontRendererが期待する状態を壊さないように、ここで無理にunbindしない

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
                width += net.minecraft.client.Minecraft.getMinecraft().fontRendererObj.getCharWidth(c) * 2;
            }
        }
        return width / 2;
    }
}
