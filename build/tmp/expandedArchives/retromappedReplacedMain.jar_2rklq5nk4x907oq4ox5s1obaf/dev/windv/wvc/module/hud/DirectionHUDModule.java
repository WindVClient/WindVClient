package dev.windv.wvc.module.hud;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Direction HUD モジュール
 * 画面上部に方位磁針（コンパス）を表示します。
 */
public class DirectionHUDModule extends WVCModule {

    private final Minecraft mc = Minecraft.func_71410_x();
    private final Gui gui = new Gui();

    public DirectionHUDModule() {
        super("DirectionHUD", true);
        this.setX(200);
        this.setY(5);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !this.isEnabled()) return;

        int centerX = this.getX();
        int y = this.getY();

        // プレイヤーの向きを取得 (0-360)
        float yaw = MathHelper.func_76142_g(mc.field_71439_g.field_70177_z);
        if (yaw < 0) yaw += 360;

        String[] directions = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
        
        // メインの方位を表示
        String dirText = directions[Math.round(yaw / 45) % 8];
        gui.func_73732_a(mc.field_71466_p, dirText, centerX, y, 0xFFFFFF);
        
        // 角度（数値）も小さく表示
        String angleText = Math.round(yaw) + "\u00B0";
        gui.func_73732_a(mc.field_71466_p, angleText, centerX, y + 10, 0xAAAAAA);
    }
}
