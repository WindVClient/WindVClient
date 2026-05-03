package dev.windv.wvc.event;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.visual.OldAnimationsModule;
import net.minecraft.client.Minecraft;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Old Animations Extra Handler
 * Bow & Swing（弓を放った瞬間の腕振り）などの追加動作を制御します。
 */
public class OldAnimationsExtraHandler {

    private boolean wasUsingItem = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        OldAnimationsModule mod = (OldAnimationsModule) WVCMod.INSTANCE.getModuleManager().getModule("OldAnimations");
        if (mod == null || !mod.isEnabled()) return;

        boolean isUsingItem = mc.thePlayer.isUsingItem();

        // --- B. アイテム使用中のスイング強制 (Punch while using) ---
        if (isUsingItem) {
            ItemStack usingItem = mc.thePlayer.getItemInUse();
            boolean isFood = usingItem != null && (usingItem.getItemUseAction() == EnumAction.EAT || usingItem.getItemUseAction() == EnumAction.DRINK);
            boolean isBow = usingItem != null && usingItem.getItemUseAction() == EnumAction.BOW;

            // 各設定が有効な場合のみスイングを許可
            if ((isFood && mod.isOldEating()) || (isBow && mod.isOldBow()) || (!isFood && !isBow && mod.isOldBlock())) {
                if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                    mc.thePlayer.swingItem();
                }
            }
        }

        // --- C. 弓を放った瞬間の腕振り (Bow & Swing) ---
        if (wasUsingItem && !isUsingItem && mod.isOldBow()) {
            if (mc.gameSettings.keyBindAttack.isKeyDown()) {
                mc.thePlayer.swingItem();
            }
        }

        wasUsingItem = isUsingItem;
    }
}
