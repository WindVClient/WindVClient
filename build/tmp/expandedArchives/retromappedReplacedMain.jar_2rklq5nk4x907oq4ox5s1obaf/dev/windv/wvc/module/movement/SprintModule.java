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
        boolean isMovingForward = player.field_71158_b.field_78900_b > 0.8F; // しっかり前進しているか
        boolean isHungry = player.func_71024_bL().func_75116_a() <= 6;
        boolean isCollided = player.field_70123_F; // 壁やブロックに接触しているか
        
        // 1.8.9 サーバーでロルバを起こさないための最重要条件：
        // 壁にぶつかっている間、スニーク中、メニュー開閉中などはダッシュを一切試みない
        boolean isGuiOpen = net.minecraft.client.Minecraft.func_71410_x().field_71462_r != null;
        
        if (!isCollided && isMovingForward && !isHungry && !player.func_70093_af() && !player.func_71039_bw() && !isGuiOpen) {
            if (!player.func_70051_ag()) {
                player.func_70031_b(true);
            }
        }
    }
}
