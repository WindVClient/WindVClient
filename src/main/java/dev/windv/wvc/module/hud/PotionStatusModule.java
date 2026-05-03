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

    private final Minecraft mc = Minecraft.getMinecraft();
    private static final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");

    public PotionStatusModule() {
        super("PotionStatus", true);
        this.setX(300);
        this.setY(200);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !this.isEnabled()) return;

        Collection<PotionEffect> effects = mc.thePlayer.getActivePotionEffects();
        if (effects.isEmpty()) return;

        int x = this.getX();
        int y = this.getY();

        for (PotionEffect effect : effects) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            String name = I18n.format(potion.getName());
            
            if (effect.getAmplifier() > 0) {
                name = name + " " + I18n.format("enchantment.level." + (effect.getAmplifier() + 1));
            }
            
            String duration = Potion.getDurationString(effect);
            String fullText = name + " " + duration;
            int textWidth = mc.fontRendererObj.getStringWidth(fullText);

            // --- アイコン描画 ---
            if (potion.hasStatusIcon()) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                mc.getTextureManager().bindTexture(inventoryBackground);
                
                int iconIndex = potion.getStatusIconIndex();
                // 1.8.9のアイコン位置計算 (18x18ピクセル)
                int u = iconIndex % 8 * 18;
                int v = 198 + iconIndex / 8 * 18;
                
                float scale = 0.6f; // アイコンを少し小さく
                GlStateManager.translate(x - textWidth - 14, y - 1, 0);
                GlStateManager.scale(scale, scale, scale);
                
                // Guiの描画メソッドを利用
                new Gui().drawTexturedModalRect(0, 0, u, v, 18, 18);
                GlStateManager.popMatrix();
            }

            // --- テキスト描画 ---
            int color = 0xFFFFFF;
            if (potion.isBadEffect()) color = 0xFF5555;
            mc.fontRendererObj.drawStringWithShadow(fullText, x - textWidth, y, color);
            
            y -= 12;
        }
    }
}
