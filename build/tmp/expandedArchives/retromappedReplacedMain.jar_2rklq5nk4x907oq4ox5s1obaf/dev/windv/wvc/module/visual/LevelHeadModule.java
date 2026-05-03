package dev.windv.wvc.module.visual;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Level Head / Name Tag Mod
 * 他のプレイヤーの頭上に情報を表示します。
 */
public class LevelHeadModule extends WVCModule {

    private final Minecraft mc = Minecraft.func_71410_x();

    public LevelHeadModule() {
        super("LevelHead", true);
    }

    @SubscribeEvent
    public void onRenderNameTag(RenderLivingEvent.Specials.Pre event) {
        if (!this.isEnabled() || !(event.entity instanceof EntityPlayer) || event.entity == mc.field_71439_g) return;

        EntityPlayer player = (EntityPlayer) event.entity;
        
        // NPCフィルター: タブリストに存在しないプレイヤー（NPC）は除外
        if (mc.func_147114_u() != null && mc.func_147114_u().func_175102_a(player.func_110124_au()) == null) {
            return;
        }
        
        // 描画位置の計算
        double x = event.x;
        double y = event.y + player.field_70131_O + 0.5;
        double z = event.z;
        
        // テキストの準備（デモとしてHPを表示。サーバー情報からレベル取得も可能）
        String text = "\u00A7eHP: \u00A7f" + (int)player.func_110143_aJ();
        
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        renderTextAboveHead(player, text, x, y, z);
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderTextAboveHead(EntityPlayer player, String text, double x, double y, double z) {
        float f = 1.6F;
        float f1 = 0.016666668F * f;
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b((float)x, (float)y, (float)z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.func_179114_b(-mc.func_175598_ae().field_78735_i, 0.0F, 1.0F, 0.0F);
        GlStateManager.func_179114_b(mc.func_175598_ae().field_78732_j, 1.0F, 0.0F, 0.0F);
        GlStateManager.func_179152_a(-f1, -f1, f1);
        GlStateManager.func_179140_f();
        GlStateManager.func_179132_a(false);
        GlStateManager.func_179097_i();
        GlStateManager.func_179147_l();
        GlStateManager.func_179120_a(770, 771, 1, 0);
        
        int i = mc.field_71466_p.func_78256_a(text) / 2;
        GlStateManager.func_179090_x();
        Gui.func_73734_a(-i - 1, -1, i + 1, 8, 0x80000000);
        GlStateManager.func_179098_w();
        
        mc.field_71466_p.func_78276_b(text, -i, 0, 0xFFFFFF);
        
        GlStateManager.func_179126_j();
        GlStateManager.func_179132_a(true);
        mc.field_71466_p.func_78276_b(text, -i, 0, 0xFFFFFF);
        GlStateManager.func_179145_e();
        GlStateManager.func_179084_k();
        GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.func_179121_F();
    }
}
