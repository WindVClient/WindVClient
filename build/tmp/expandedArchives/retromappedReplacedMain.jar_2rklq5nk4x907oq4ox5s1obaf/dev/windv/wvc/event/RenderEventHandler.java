package dev.windv.wvc.event;

import dev.windv.wvc.render.HudRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 共通描画イベントハンドラー
 * HUD（プレイ中）とGUI（インベントリ等）の両方でロゴを描画する。
 */
public class RenderEventHandler {

    /**
     * HUD描画イベント（プレイ画面）
     */
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        // Logo is no longer rendered on HUD (Inventory only)
    }

    /**
     * GUI描画イベント（インベントリ等）
     */
    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        // Show logo ONLY in inventory screens (GuiContainer)
        if (event.gui instanceof net.minecraft.client.gui.inventory.GuiContainer) {
            dev.windv.wvc.render.HudRenderer.renderLogo();
        }
    }

    /**
     * GUIオープンイベント
     * バニラのメインメニューをWVC専用メニューに差し替える
     */
    @SubscribeEvent
    public void onGuiOpen(net.minecraftforge.client.event.GuiOpenEvent event) {
        if (event.gui instanceof net.minecraft.client.gui.GuiMainMenu) {
            event.gui = new dev.windv.wvc.gui.GuiWVCMainMenu();
        }
    }
}
