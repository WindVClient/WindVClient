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

    private final Minecraft mc = Minecraft.func_71410_x();
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
        if (entity == mc.field_71439_g && showSelf.isEnabled()) {
            if (mc.field_71474_y.field_74320_O != 0) {
                // デフォルトの描画をキャンセルして独自描画（または強制表示）
                // 1.8.9では自分自身のネームタグ描画は通常スキップされるため、ここで手動で描画する
                renderCustomNameTag((EntityPlayer) entity, event.x, event.y, event.z);
            }
        }
    }

    private void renderCustomNameTag(EntityPlayer player, double x, double y, double z) {
        String name = player.func_145748_c_().func_150254_d();
        double distance = player.func_70068_e(mc.func_175606_aa());
        
        // 描画位置の調整（頭の上）
        float scale = 0.02666667F;
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b((float) x, (float) y + player.field_70131_O + 0.5F, (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.func_179114_b(-mc.func_175598_ae().field_78735_i, 0.0F, 1.0F, 0.0F);
        GlStateManager.func_179114_b(mc.func_175598_ae().field_78732_j, 1.0F, 0.0F, 0.0F);
        GlStateManager.func_179152_a(-scale, -scale, scale);
        GlStateManager.func_179140_f();
        GlStateManager.func_179132_a(false);
        GlStateManager.func_179097_i();
        GlStateManager.func_179147_l();
        GlStateManager.func_179120_a(770, 771, 1, 0);

        int width = mc.field_71466_p.func_78256_a(name) / 2;

        if (renderBackground.isEnabled()) {
            Tessellator tessellator = Tessellator.func_178181_a();
            WorldRenderer worldrenderer = tessellator.func_178180_c();
            GlStateManager.func_179090_x();
            worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
            worldrenderer.func_181662_b(-width - 1, -1, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
            worldrenderer.func_181662_b(-width - 1, 8, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
            worldrenderer.func_181662_b(width + 1, 8, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
            worldrenderer.func_181662_b(width + 1, -1, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
            tessellator.func_78381_a();
            GlStateManager.func_179098_w();
        }

        mc.field_71466_p.func_78276_b(name, -width, 0, 553648127);
        GlStateManager.func_179126_j();
        GlStateManager.func_179132_a(true);
        mc.field_71466_p.func_78276_b(name, -width, 0, -1);
        GlStateManager.func_179145_e();
        GlStateManager.func_179084_k();
        GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.func_179121_F();
    }
}
