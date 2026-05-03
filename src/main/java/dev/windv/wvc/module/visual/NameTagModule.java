package dev.windv.wvc.module.visual;

import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.BooleanSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * NameTag Mod
 * 相手のネームタグの視認性向上と、自分のネームタグ表示機能を提供。
 */
public class NameTagModule extends WVCModule {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting showSelf;
    private final BooleanSetting renderBackground;

    public NameTagModule() {
        super("NameTag", true);
        this.addSetting(showSelf = new BooleanSetting("Show Own NameTag", true));
        this.addSetting(renderBackground = new BooleanSetting("Background", true));
    }

    @SubscribeEvent
    public void onRenderName(RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
        if (!this.isEnabled()) return;

        EntityLivingBase entity = event.entity;
        if (!(entity instanceof EntityPlayer)) return;

        // 自分のネームタグ表示設定が有効で、かつ三人称視点の場合
        if (entity == mc.thePlayer && showSelf.isEnabled()) {
            if (mc.gameSettings.thirdPersonView != 0) {
                // デフォルトの描画をキャンセルして独自描画（または強制表示）
                // 1.8.9では自分自身のネームタグ描画は通常スキップされるため、ここで手動で描画する
                renderCustomNameTag((EntityPlayer) entity, event.x, event.y, event.z);
            }
        }
    }

    private void renderCustomNameTag(EntityPlayer player, double x, double y, double z) {
        String name = player.getDisplayName().getFormattedText();
        double distance = player.getDistanceSqToEntity(mc.getRenderViewEntity());
        
        // 描画位置の調整（頭の上）
        float scale = 0.02666667F;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + player.height + 0.5F, (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        int width = mc.fontRendererObj.getStringWidth(name) / 2;

        if (renderBackground.isEnabled()) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(-width - 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos(-width - 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos(width + 1, 8, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos(width + 1, -1, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
        }

        mc.fontRendererObj.drawString(name, -width, 0, 553648127);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        mc.fontRendererObj.drawString(name, -width, 0, -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
