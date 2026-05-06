package dev.windv.wvc.module.hud;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.gui.GuiEditHUD;
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
        // ElementType.TEXT に変更 (より確実)
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT || !this.isEnabled()) return;

        Collection<PotionEffect> effects = mc.thePlayer.getActivePotionEffects();
        
        int x = this.getX();
        int y = this.getY();

        // ポーション効果がないが、EditHUDが開いている場合はダミーを表示
        if (effects.isEmpty()) {
            if (mc.currentScreen instanceof GuiEditHUD) {
                renderDummy(x, y);
            }
            return;
        }

        for (PotionEffect effect : effects) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (potion == null) continue; // ポーションが存在しない場合はスキップ
            
            String name = I18n.format(potion.getName());
            
            if (effect.getAmplifier() > 0) {
                name = name + " " + I18n.format("enchantment.level." + (effect.getAmplifier() + 1));
            }
            
            String duration = Potion.getDurationString(effect);
            
            // --- アイコン描画 ---
            if (potion.hasStatusIcon()) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                mc.getTextureManager().bindTexture(inventoryBackground);
                
                int iconIndex = potion.getStatusIconIndex();
                int u = iconIndex % 8 * 18;
                int v = 198 + iconIndex / 8 * 18;
                
                float scale = 0.6f;
                GlStateManager.translate(x, y + 1, 0); // x座標から開始
                GlStateManager.scale(scale, scale, scale);
                
                new Gui().drawTexturedModalRect(0, 0, u, v, 18, 18);
                GlStateManager.popMatrix();
            }

            // --- テキスト描画 (アイコンの右側に左揃え) ---
            int color = 0xFFFFFF;
            if (potion.isBadEffect()) color = 0xFF5555;
            
            // アイコン幅(18*0.6=約11) + 余白(3) = 14ピクセル右から描画開始
            WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow(name, x + 14, y, color);
            WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow(duration, x + 14, y + 9, 0xAAAAAA);
            
            y += 22; // 下方向に並べる
        }
    }

    private void renderDummy(int x, int y) {
        // スピードII 0:30 のダミー表示
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow("Speed II", x + 14, y, 0xFFFFFF);
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow("0:30", x + 14, y + 9, 0xAAAAAA);
    }
}
