package dev.windv.wvc.module.combat;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.util.Random;

/**
 * AutoClicker モジュール
 * 左クリックを自動で連打します。
 */
public class AutoClickerModule extends WVCModule {

    private final Random random = new Random();
    private long lastClickTime = 0;
    private long nextDelay = 0;

    public AutoClickerModule() {
        super("AutoClicker", false);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!this.isEnabled() || event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        
        // 画面が開いておらず、かつ左ボタンが押しっぱなしの時に動作
        if (mc.currentScreen == null && Mouse.isButtonDown(0)) {
            if (System.currentTimeMillis() - lastClickTime >= nextDelay) {
                // クリックの実行 (KeyBindingの状態を操作)
                int key = mc.gameSettings.keyBindAttack.getKeyCode();
                KeyBinding.setKeyBindState(key, true);
                KeyBinding.onTick(key);
                KeyBinding.setKeyBindState(key, false);

                lastClickTime = System.currentTimeMillis();
                // 8~14 CPS 程度のランダムな遅延 (1000ms / CPS)
                nextDelay = 1000 / (8 + random.nextInt(7));
            }
        }
    }
}
