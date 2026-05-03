package dev.windv.wvc.module.system;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;

/**
 * Chat Confirmation モジュール
 * チャット内のURLをクリックした際に確認画面を表示します。
 */
public class ChatConfirmationModule extends WVCModule {

    private final Minecraft mc = Minecraft.getMinecraft();

    public ChatConfirmationModule() {
        super("ChatConfirmation", true);
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        if (!this.isEnabled() || mc.thePlayer == null || mc.currentScreen == null) return;

        // 左クリックが押されたかチェック
        if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
            if (mc.currentScreen instanceof GuiChat) {
                // マウス座標からチャットコンポーネントを取得
                // 1.8.9の座標系に変換
                int mouseX = Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth;
                int mouseY = mc.currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1;

                IChatComponent component = mc.ingameGUI.getChatGUI().getChatComponent(mouseX, mouseY);
                if (component != null && component.getChatStyle().getChatClickEvent() != null) {
                    ClickEvent clickEvent = component.getChatStyle().getChatClickEvent();
                    
                    if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
                        String url = clickEvent.getValue();
                        
                        // バニラの処理を上書きして確認画面を出す
                        mc.displayGuiScreen(new GuiConfirmOpenLink((result, id) -> {
                            if (result) {
                                try {
                                    java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            mc.displayGuiScreen(new GuiChat()); // チャット画面に戻す
                        }, url, 31102, true));
                    }
                }
            }
        }
    }
}
