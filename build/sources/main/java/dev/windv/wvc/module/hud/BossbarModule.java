package dev.windv.wvc.module.hud;

import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.BooleanSetting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Bossbar Customizer
 * ボスバー（ウィザー・ドラゴン）の表示を制御します。
 */
public class BossbarModule extends WVCModule {

    private final BooleanSetting hideBossbar;

    public BossbarModule() {
        super("Bossbar", true);
        this.addSetting(hideBossbar = new BooleanSetting("Hide Completely", true));
    }

    @SubscribeEvent
    public void onRenderBossHealth(RenderGameOverlayEvent.Pre event) {
        if (!this.isEnabled()) return;

        if (event.type == RenderGameOverlayEvent.ElementType.BOSSHEALTH) {
            if (hideBossbar.isEnabled()) {
                event.setCanceled(true);
            }
        }
    }
}
