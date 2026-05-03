package dev.windv.wvc.module.movement;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Toggle Sneak モジュール
 * スニーク状態を維持します。メニュー開閉時も継続可能です。
 */
public class ToggleSneakModule extends WVCModule {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean isSneaking = false;

    public ToggleSneakModule() {
        super("ToggleSneak", false);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (!this.isEnabled() || mc.currentScreen != null) return;

        // スニークキーが押された瞬間を検知
        if (mc.gameSettings.keyBindSneak.isPressed()) {
            isSneaking = !isSneaking;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !this.isEnabled() || event.player != mc.thePlayer) return;

        // Hypixel 安全対策: インベントリやメニューを開いている間はスニークを禁止する
        boolean isGuiOpen = mc.currentScreen != null && !(mc.currentScreen instanceof net.minecraft.client.gui.GuiChat);
        
        if (isSneaking && !isGuiOpen) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        } else {
            // GUIが開いている、またはトグルがオフの場合、かつキーが物理的に押されていない場合は解除
            if (!mc.gameSettings.keyBindSneak.isKeyDown()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            }
        }
    }

    @Override
    public void onDisable() {
        isSneaking = false;
        // モジュールオフ時にスニークを解除
        if (mc.thePlayer != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
    }
}
