package dev.windv.wvc.module.hud;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;

/**
 * Potion Status モジュール
 * 適用されているポーション効果をアイコンとテキストで表示します。
 */
public class PotionStatusModule extends WVCModule {

    private final Minecraft mc = Minecraft.func_71410_x();
    private static final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");

    public PotionStatusModule() {
        super("PotionStatus", true);
        this.setX(300);
        this.setY(200);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !this.isEnabled()) return;

        Collection<PotionEffect> effects = mc.field_71439_g.func_70651_bq();
        if (effects.isEmpty()) return;

        int x = this.getX();
        int y = this.getY();

        for (PotionEffect effect : effects) {
            Potion potion = Potion.field_76425_a[effect.func_76456_a()];
            String name = I18n.func_135052_a(potion.func_76393_a());
            
            if (effect.func_76458_c() > 0) {
                name = name + " " + I18n.func_135052_a("enchantment.level." + (effect.func_76458_c() + 1));
            }
            
            String duration = Potion.func_76389_a(effect);
            String fullText = name + " " + duration;
            int textWidth = mc.field_71466_p.func_78256_a(fullText);

            // --- アイコン描画 ---
            if (potion.func_76400_d()) {
                GlStateManager.func_179094_E();
                GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
                mc.func_110434_K().func_110577_a(inventoryBackground);
                
                int iconIndex = potion.func_76392_e();
                // 1.8.9のアイコン位置計算 (18x18ピクセル)
                int u = iconIndex % 8 * 18;
                int v = 198 + iconIndex / 8 * 18;
                
                float scale = 0.6f; // アイコンを少し小さく
                GlStateManager.func_179109_b(x - textWidth - 14, y - 1, 0);
                GlStateManager.func_179152_a(scale, scale, scale);
                
                // Guiの描画メソッドを利用
                new Gui().func_73729_b(0, 0, u, v, 18, 18);
                GlStateManager.func_179121_F();
            }

            // --- テキスト描画 ---
            int color = 0xFFFFFF;
            if (potion.func_76398_f()) color = 0xFF5555;
            mc.field_71466_p.func_175063_a(fullText, x - textWidth, y, color);
            
            y -= 12;
        }
    }
}
