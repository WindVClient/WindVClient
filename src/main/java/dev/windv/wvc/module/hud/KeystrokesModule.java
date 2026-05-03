package dev.windv.wvc.module.hud;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.ColorSetting;
import dev.windv.wvc.settings.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

/**
 * Keystrokes Mod
 * WASD + SpaceBar の入力をHUDに表示する。
 */
public class KeystrokesModule extends WVCModule {

    private final ColorSetting textColor;
    private final ColorSetting pressColor;

    public KeystrokesModule(boolean enabled) {
        super("Keystrokes", enabled);
        this.setX(10);
        this.setY(150);
        this.addSetting(textColor = new ColorSetting("Text Color", 255, 255, 255));
        this.addSetting(pressColor = new ColorSetting("Press Color", 79, 91, 255)); // Premium Blue
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !this.isEnabled()) return;

        Minecraft mc = Minecraft.getMinecraft();
        int x = this.getX();
        int y = this.getY();

        // Key Layout Configuration
        drawKey(mc.gameSettings.keyBindForward, x + 22, y, 20, 20);      // W
        drawKey(mc.gameSettings.keyBindLeft,    x,      y + 22, 20, 20); // A
        drawKey(mc.gameSettings.keyBindBack,    x + 22, y + 22, 20, 20); // S
        drawKey(mc.gameSettings.keyBindRight,   x + 44, y + 22, 20, 20); // D
        drawKey(mc.gameSettings.keyBindJump,    x,      y + 44, 64, 12); // Space (Wait! 20*3+4=64)
    }

    private void drawKey(KeyBinding key, int x, int y, int width, int height) {
        boolean pressed = key.isKeyDown();
        int color = pressed ? pressColor.getColor() : 0x44000000;
        
        // Draw Key Background
        Gui.drawRect(x, y, x + width, y + height, color);
        
        // Draw Key Name (Centered)
        String name = Keyboard.getKeyName(key.getKeyCode());
        if (key == Minecraft.getMinecraft().gameSettings.keyBindJump) name = "-------";
        
        WVCMod.INSTANCE.getFontRenderer().drawCenteredString(name, x + width / 2, y + height / 2 - 4, textColor.getColor());
    }
}
