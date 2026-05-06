package dev.windv.wvc.module.system;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

/**
 * Japanese IME Support モジュール
 * 1.8.9のLWJGL2において日本語入力（IME候補ウィンドウ）を強制的に表示・安定化させます。
 */
public class JapaneseIMEModule extends WVCModule {

    private final Minecraft mc = Minecraft.func_71410_x();
    private boolean isAwaitingReset = false;
    private int resetTimer = 0;

    public JapaneseIMEModule() {
        super("JapaneseIME", true);
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (!this.isEnabled()) return;

        if (isInputGui(event.gui)) {
            // テキスト入力画面が開いた際、OSのリピートイベントを有効化
            Keyboard.enableRepeatEvents(true);
            triggerReset(2); // 少し待ってから実行
        }
    }

    @SubscribeEvent
    public void onKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
        if (!this.isEnabled() || !isInputGui(mc.field_71462_r)) return;

        // 全角/半角キーなどのIME切り替えキーが押された際、即座にウィンドウをリセットしてIMEウィンドウを呼び出す
        int key = Keyboard.getEventKey();
        // 0x29 (半角/全角), 0x70 (カナ), 0x79 (変換), 0x7B (無変換) 等
        if (key == 0x29 || key == 144 || key == Keyboard.KEY_KANJI || key == 0) {
            triggerReset(0);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!this.isEnabled() || event.phase != TickEvent.Phase.END) return;

        if (isAwaitingReset) {
            if (resetTimer <= 0) {
                updateNativeWindow();
                isAwaitingReset = false;
            } else {
                resetTimer--;
            }
        }
    }

    private boolean isInputGui(GuiScreen gui) {
        return gui instanceof GuiChat || gui instanceof GuiEditSign || (gui != null && gui.getClass().getSimpleName().contains("GuiSettings"));
    }

    private void triggerReset(int delay) {
        isAwaitingReset = true;
        resetTimer = delay;
    }

    private void updateNativeWindow() {
        try {
            if (Display.isActive() && !Display.isFullscreen()) {
                // ウィンドウのResizable属性をトグルしてOS側のIMEを強制アクティブ化
                Display.setResizable(false);
                Display.setResizable(true);
            }
        } catch (Exception e) {
            // Ignore
        }
    }
}
