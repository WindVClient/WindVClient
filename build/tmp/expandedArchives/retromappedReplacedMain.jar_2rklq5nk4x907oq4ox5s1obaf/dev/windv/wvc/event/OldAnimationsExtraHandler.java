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
        
        Minecraft mc = Minecraft.func_71410_x();
        if (mc.field_71439_g == null) return;

        OldAnimationsModule mod = (OldAnimationsModule) WVCMod.INSTANCE.getModuleManager().getModule("OldAnimations");
        if (mod == null || !mod.isEnabled()) return;

        boolean isUsingItem = mc.field_71439_g.func_71039_bw();

        // --- B. アイテム使用中のスイング強制 (Punch while using) ---
        if (isUsingItem) {
            ItemStack usingItem = mc.field_71439_g.func_71011_bu();
            boolean isFood = usingItem != null && (usingItem.func_77975_n() == EnumAction.EAT || usingItem.func_77975_n() == EnumAction.DRINK);
            boolean isBow = usingItem != null && usingItem.func_77975_n() == EnumAction.BOW;

            // 各設定が有効な場合のみスイングを許可
            if ((isFood && mod.isOldEating()) || (isBow && mod.isOldBow()) || (!isFood && !isBow && mod.isOldBlock())) {
                if (mc.field_71474_y.field_74312_F.func_151470_d()) {
                    mc.field_71439_g.func_71038_i();
                }
            }
        }

        // --- C. 弓を放った瞬間の腕振り (Bow & Swing) ---
        if (wasUsingItem && !isUsingItem && mod.isOldBow()) {
            if (mc.field_71474_y.field_74312_F.func_151470_d()) {
                mc.field_71439_g.func_71038_i();
            }
        }

        wasUsingItem = isUsingItem;
    }
}
