package dev.windv.wvc.module.system;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

/**
 * Auto Text Hotkey
 * 特定のキーにコマンドを割り当てて自動送信します。
 */
public class AutoTextModule extends WVCModule {

    private final Minecraft mc = Minecraft.getMinecraft();

    public AutoTextModule() {
        super("AutoText", true);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (!this.isEnabled() || mc.currentScreen != null) return;

        // キーが押された瞬間のみ判定
        if (Keyboard.getEventKeyState()) {
            int key = Keyboard.getEventKey();

            // Lキー: /lobby
            if (key == Keyboard.KEY_L) {
                mc.thePlayer.sendChatMessage("/lobby");
            }
            
            // Pキー: /play pit (Hypixel等)
            if (key == Keyboard.KEY_P) {
                mc.thePlayer.sendChatMessage("/play pit");
            }
            
            // Gキー: /gg
            if (key == Keyboard.KEY_G) {
                mc.thePlayer.sendChatMessage("/gg");
            }
        }
    }
}
