package dev.windv.wvc.module.movement;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Zoom モジュール
 * Cキーでズームし、マウスホイールで倍率を調整可能。
 */
public class ZoomModule extends WVCModule {

    private final Minecraft mc = Minecraft.func_71410_x();
    private boolean isZooming = false;
    private float originalFov;
    private float currentZoomFov = 20.0F;
    private static final float MIN_FOV = 2.0F;
    private static final float MAX_FOV = 60.0F;

    public ZoomModule(boolean enabled) {
        super("Zoom", enabled);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!this.isEnabled()) return;
        if (event.phase != TickEvent.Phase.START) return;

        // Cキーが押されているか
        if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
            if (!isZooming) {
                originalFov = mc.field_71474_y.field_74334_X;
                mc.field_71474_y.field_74326_T = true;
                isZooming = true;
                currentZoomFov = 20.0F; // 初期ズーム倍率
            }
            // FOVを適用
            mc.field_71474_y.field_74334_X = currentZoomFov;
        } else {
            if (isZooming) {
                mc.field_71474_y.field_74334_X = originalFov;
                mc.field_71474_y.field_74326_T = false;
                isZooming = false;
            }
        }
    }

    @SubscribeEvent
    public void onMouse(MouseEvent event) {
        if (!this.isEnabled() || !isZooming) return;

        // ホイールが回転したか
        if (event.dwheel != 0) {
            // ズーム倍率を調整（ホイール上：拡大/FOV減、ホイール下：縮小/FOV増）
            float amount = (event.dwheel > 0) ? -2.0F : 2.0F;
            currentZoomFov += amount;
            
            // 範囲制限
            if (currentZoomFov < MIN_FOV) currentZoomFov = MIN_FOV;
            if (currentZoomFov > MAX_FOV) currentZoomFov = MAX_FOV;
            
            // ホイールによるスロット切り替えをキャンセル
            event.setCanceled(true);
        }
    }
}
