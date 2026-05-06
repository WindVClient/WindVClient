package dev.windv.wvc.module.combat;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Velocity モジュール
 * 被弾時のノックバックを軽減します。
 */
public class VelocityModule extends WVCModule {

    public VelocityModule() {
        super("Velocity", false);
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!this.isEnabled()) return;
        
        // プレイヤー本人のノックバックを監視
        if (event.entityLiving == Minecraft.getMinecraft().thePlayer) {
            if (Minecraft.getMinecraft().thePlayer.hurtTime > 0 && Minecraft.getMinecraft().thePlayer.hurtTime == Minecraft.getMinecraft().thePlayer.maxHurtTime) {
                // ノックバックを 0 に設定 (100% 軽減)
                // 必要に応じて 0.5f などにすれば 50% 軽減になります
                Minecraft.getMinecraft().thePlayer.motionX = 0;
                Minecraft.getMinecraft().thePlayer.motionZ = 0;
            }
        }
    }
}
