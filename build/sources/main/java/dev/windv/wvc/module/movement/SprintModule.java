package dev.windv.wvc.module.movement;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * 堅牢なスプリント・モジュール
 * ブロック衝突時のロルバを徹底的に防ぎます。
 */
public class SprintModule extends WVCModule {

    public SprintModule() {
        super("Sprint", true);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !this.isEnabled()) return;

        // 自分自身のプレイヤー（EntityPlayerSP）のみ処理する
        if (!(event.player instanceof EntityPlayerSP)) return;
        
        EntityPlayerSP player = (EntityPlayerSP) event.player;

        // 条件チェック
        boolean isMovingForward = player.movementInput.moveForward > 0.8F; // しっかり前進しているか
        boolean isHungry = player.getFoodStats().getFoodLevel() <= 6;
        boolean isCollided = player.isCollidedHorizontally; // 壁やブロックに接触しているか
        
        // 1.8.9 サーバーでロルバを起こさないための最重要条件：
        // 壁にぶつかっている間、スニーク中、メニュー開閉中などはダッシュを一切試みない
        boolean isGuiOpen = net.minecraft.client.Minecraft.getMinecraft().currentScreen != null;
        
        if (!isCollided && isMovingForward && !isHungry && !player.isSneaking() && !player.isUsingItem() && !isGuiOpen) {
            if (!player.isSprinting()) {
                player.setSprinting(true);
            }
        }
    }
}
