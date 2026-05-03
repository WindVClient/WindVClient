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

    private final Minecraft mc = Minecraft.getMinecraft();
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
                is = mc.thePlayer.getHeldItem();
            } else {
                is = mc.thePlayer.inventory.armorItemInSlot(i);
            }

            if (is != null) {
                GlStateManager.pushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                mc.getRenderItem().renderItemAndEffectIntoGUI(is, x, y);
                
                if (is.isItemStackDamageable()) {
                    int damage = is.getMaxDamage() - is.getItemDamage();
                    String s;
                    
                    if (showPercent.isEnabled()) {
                        s = (int)((double)damage / is.getMaxDamage() * 100) + "%";
                    } else if (showMax.isEnabled()) {
                        s = damage + "/" + is.getMaxDamage();
                    } else {
                        s = String.valueOf(damage);
                    }
                    
                    float scale = 0.5f;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x + 16, y + 12, 0);
                    GlStateManager.scale(scale, scale, scale);
                    
                    int color = 0xFFFFFF;
                    double percent = (double) damage / is.getMaxDamage();
                    if (percent < 0.2) color = 0xFF5555;
                    else if (percent < 0.5) color = 0xFFFF55;
                    
                    mc.fontRendererObj.drawStringWithShadow(s, 0, 0, color);
                    GlStateManager.popMatrix();
                }
                
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();
                y += 18;
            }
        }
    }
}
