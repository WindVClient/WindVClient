package dev.windv.wvc.module.system;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

/**
 * Japanese IME Support モジュール
 * 日本語入力時の文字消え・二重入力を防止し、入力を安定させます。
 * 1.8.9で候補ウィンドウが表示されない問題をウィンドウのリセットハックで解決します。
 */
public class JapaneseIMEModule extends WVCModule {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean isAwaitingReset = false;
    private int resetTimer = 0;

    public JapaneseIMEModule() {
        super("JapaneseIME", true);
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (!this.isEnabled()) return;

        GuiScreen gui = event.gui;
        
        // テキスト入力を伴う画面が開いた際にリピートイベントを強制有効化
        if (gui instanceof GuiChat || gui instanceof GuiEditSign || (gui != null && gui.getClass().getSimpleName().contains("GuiSettings"))) {
            Keyboard.enableRepeatEvents(true);
            // 候補ウィンドウを表示させるためのウィンドウハックを予約
            isAwaitingReset = true;
            resetTimer = 5; // 数フレーム待ってから実行
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!this.isEnabled() || event.phase != TickEvent.Phase.END) return;

        // 候補ウィンドウ表示ハックの実行
        if (isAwaitingReset) {
            if (resetTimer <= 0) {
                updateNativeWindow();
                isAwaitingReset = false;
            } else {
                resetTimer--;
            }
        }
    }

    /**
     * ウィンドウの状態を更新し、IMEの挙動をリセット/安定化させます。
     */
    private void updateNativeWindow() {
        try {
            if (Display.isActive() && !Display.isFullscreen()) {
                // ウィンドウのResizable属性を一瞬切り替えることで、OS側にIMEの再ロードを促し
                // 候補ウィンドウを表示させるためのハック
                Display.setResizable(false);
                Display.setResizable(true);
            }
        } catch (Exception e) {
            // Ignore
        }
    }
}
