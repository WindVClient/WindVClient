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
        if (event.entityLiving == Minecraft.func_71410_x().field_71439_g) {
            if (Minecraft.func_71410_x().field_71439_g.field_70737_aN > 0 && Minecraft.func_71410_x().field_71439_g.field_70737_aN == Minecraft.func_71410_x().field_71439_g.field_70738_aO) {
                // ノックバックを 0 に設定 (100% 軽減)
                // 必要に応じて 0.5f などにすれば 50% 軽減になります
                Minecraft.func_71410_x().field_71439_g.field_70159_w = 0;
                Minecraft.func_71410_x().field_71439_g.field_70179_y = 0;
            }
        }
    }
}
