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

    private final Minecraft mc = Minecraft.func_71410_x();

    public ChatConfirmationModule() {
        super("ChatConfirmation", true);
    }

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        if (!this.isEnabled() || mc.field_71439_g == null || mc.field_71462_r == null) return;

        // 左クリックが押されたかチェック
        if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
            if (mc.field_71462_r instanceof GuiChat) {
                // マウス座標からチャットコンポーネントを取得
                // 1.8.9の座標系に変換
                int mouseX = Mouse.getEventX() * mc.field_71462_r.field_146294_l / mc.field_71443_c;
                int mouseY = mc.field_71462_r.field_146295_m - Mouse.getEventY() * mc.field_71462_r.field_146295_m / mc.field_71440_d - 1;

                IChatComponent component = mc.field_71456_v.func_146158_b().func_146236_a(mouseX, mouseY);
                if (component != null && component.func_150256_b().func_150235_h() != null) {
                    ClickEvent clickEvent = component.func_150256_b().func_150235_h();
                    
                    if (clickEvent.func_150669_a() == ClickEvent.Action.OPEN_URL) {
                        String url = clickEvent.func_150668_b();
                        
                        // バニラの処理を上書きして確認画面を出す
                        mc.func_147108_a(new GuiConfirmOpenLink((result, id) -> {
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
