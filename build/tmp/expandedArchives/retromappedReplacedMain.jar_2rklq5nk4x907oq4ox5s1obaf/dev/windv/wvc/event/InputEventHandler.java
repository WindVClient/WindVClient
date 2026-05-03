package dev.windv.wvc.event;

import dev.windv.wvc.gui.WVCGuiSettings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

/**
 * 入力イベントハンドラー
 * キー入力を監視し、設定画面の起動などを制御する。
 */
public class InputEventHandler {

    /**
     * キー入力イベント
     */
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (!Keyboard.getEventKeyState()) return;
        
        int key = Keyboard.getEventKey();
        Minecraft mc = Minecraft.func_71410_x();

        // Right-Shift (Keyboard.KEY_RSHIFT) で設定画面を開く
        if (key == Keyboard.KEY_RSHIFT) {
            if (mc.field_71462_r == null) {
                mc.func_147108_a(new WVCGuiSettings());
            }
        }

        // 各モジュールのホットキーチェック
        if (mc.field_71462_r == null) {
            for (dev.windv.wvc.module.WVCModule m : dev.windv.wvc.WVCMod.INSTANCE.getModuleManager().getModules()) {
                if (m.getKeybind().getKeyCode() != 0 && m.getKeybind().getKeyCode() == key) {
                    m.toggle();
                    mc.field_71439_g.func_85030_a("random.click", 0.5F, 1.2F);
                }
            }
        }
    }
}
