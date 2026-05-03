package dev.windv.wvc.module.hud;

import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.BooleanSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Armor Status モジュール
 * 装備中の防具と手持ちアイテムの耐久値を表示します。
 */
public class ArmorStatusModule extends WVCModule {

    private final Minecraft mc = Minecraft.func_71410_x();
    private final BooleanSetting showPercent;
    private final BooleanSetting showMax;

    public ArmorStatusModule() {
        super("ArmorStatus", true);
        this.setX(2);
        this.setY(100);
        this.addSetting(showPercent = new BooleanSetting("Show Percent", false));
        this.addSetting(showMax = new BooleanSetting("Show Max", false));
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !this.isEnabled()) return;

        renderArmorStatus(this.getX(), this.getY());
    }

    private void renderArmorStatus(int x, int y) {
        // 装備品と手持ちアイテム（合計5スロット）
        for (int i = 0; i <= 4; i++) {
            ItemStack is = null;
            if (i == 4) {
                is = mc.field_71439_g.func_70694_bm();
            } else {
                is = mc.field_71439_g.field_71071_by.func_70440_f(i);
            }

            if (is != null) {
                GlStateManager.func_179094_E();
                RenderHelper.func_74520_c();
                mc.func_175599_af().func_180450_b(is, x, y);
                
                if (is.func_77984_f()) {
                    int damage = is.func_77958_k() - is.func_77952_i();
                    String s;
                    
                    if (showPercent.isEnabled()) {
                        s = (int)((double)damage / is.func_77958_k() * 100) + "%";
                    } else if (showMax.isEnabled()) {
                        s = damage + "/" + is.func_77958_k();
                    } else {
                        s = String.valueOf(damage);
                    }
                    
                    float scale = 0.5f;
                    GlStateManager.func_179094_E();
                    GlStateManager.func_179109_b(x + 16, y + 12, 0);
                    GlStateManager.func_179152_a(scale, scale, scale);
                    
                    int color = 0xFFFFFF;
                    double percent = (double) damage / is.func_77958_k();
                    if (percent < 0.2) color = 0xFF5555;
                    else if (percent < 0.5) color = 0xFFFF55;
                    
                    mc.field_71466_p.func_175063_a(s, 0, 0, color);
                    GlStateManager.func_179121_F();
                }
                
                RenderHelper.func_74518_a();
                GlStateManager.func_179121_F();
                y += 18;
            }
        }
    }
}
