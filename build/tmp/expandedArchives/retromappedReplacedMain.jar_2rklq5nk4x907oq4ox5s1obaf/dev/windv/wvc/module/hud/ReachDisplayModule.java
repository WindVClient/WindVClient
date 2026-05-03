package dev.windv.wvc.module.hud;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.DecimalFormat;

/**
 * Reach Display モジュール
 * 最後に攻撃が届いた際の距離を表示します。
 */
public class ReachDisplayModule extends WVCModule {

    private final Minecraft mc = Minecraft.func_71410_x();
    private double lastReach = 0;
    private long lastAttackTime = 0;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public ReachDisplayModule() {
        super("ReachDisplay", true);
        this.setX(200);
        this.setY(150);
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        if (event.entityPlayer != mc.field_71439_g || !this.isEnabled()) return;

        // 攻撃が当たった瞬間の視線情報を取得
        MovingObjectPosition mop = mc.field_71476_x;
        if (mop != null && mop.field_72313_a == MovingObjectPosition.MovingObjectType.ENTITY) {
            Entity target = mop.field_72308_g;
            if (target != null) {
                // プレイヤーの目線（EyePos）からの距離を計算
                lastReach = mc.field_71439_g.func_174824_e(1.0F).func_72438_d(mop.field_72307_f);
                lastAttackTime = System.currentTimeMillis();
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !this.isEnabled()) return;

        // 最後に攻撃してから2秒間表示
        if (System.currentTimeMillis() - lastAttackTime > 2000) return;

        String text = "Reach: " + df.format(lastReach) + " blocks";
        mc.field_71466_p.func_175063_a(text, this.getX(), this.getY(), 0xFFFFFF);
    }
}
