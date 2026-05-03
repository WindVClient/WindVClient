package dev.windv.wvc.module.hud;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.render.HudRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * FPS表示モジュール
 * RenderGameOverlayEvent.TEXT でHUDにFPSを描画する。
 * 軽量化のためフレームごとにMC内部カウンターを参照するだけ。
 */
public class FpsModule extends WVCModule {

    private final dev.windv.wvc.settings.ModeSetting bracketMode;
    private final dev.windv.wvc.settings.ColorSetting textColor;

    public FpsModule(boolean enabled) {
        super("FPS", enabled);
        this.addSetting(bracketMode = new dev.windv.wvc.settings.ModeSetting("Brackets", 0, "None", "[ ]", "( )", "< >"));
        this.addSetting(textColor = new dev.windv.wvc.settings.ColorSetting("Text Color", 255, 255, 255));
        this.setX(2);
        this.setY(2);
    }

    /**
     * オーバーレイ描画イベントでFPSを表示する
     */
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !this.isEnabled()) return;

        int fps = Minecraft.func_175610_ah();
        String label = formatLabel("FPS: " + fps);

        WVCMod.INSTANCE.getFontRenderer().drawString(label, (float)this.getX(), (float)this.getY(), textColor.getColor());
    }

    private String formatLabel(String text) {
        switch (bracketMode.getMode()) {
            case "[ ]": return "[" + text + "]";
            case "( )": return "(" + text + ")";
            case "< >": return "<" + text + ">";
            default: return text;
        }
    }

    @Override
    public void onEnable() {
        WVCMod.LOGGER.debug("[WVC] FPSモジュール: 有効");
    }

    @Override
    public void onDisable() {
        WVCMod.LOGGER.debug("[WVC] FPSモジュール: 無効");
    }
}
