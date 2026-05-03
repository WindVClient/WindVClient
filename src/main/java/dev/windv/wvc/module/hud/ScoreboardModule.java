package dev.windv.wvc.module.hud;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scoreboard Displayer
 * スコアボードをシンプルにし、赤い数字を非表示にします。
 */
public class ScoreboardModule extends WVCModule {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final dev.windv.wvc.settings.BooleanSetting showNumbers;

    public ScoreboardModule() {
        super("Scoreboard", true);
        this.setX(0);
        this.setY(0);
        this.addSetting(showNumbers = new dev.windv.wvc.settings.BooleanSetting("Show Numbers", false));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        try {
            net.minecraftforge.client.GuiIngameForge.renderObjective = false;
        } catch (Throwable ignored) {}
    }

    @Override
    public void onDisable() {
        super.onDisable();
        try {
            net.minecraftforge.client.GuiIngameForge.renderObjective = true;
        } catch (Throwable ignored) {}
    }

    @SubscribeEvent
    public void onRenderPost(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !this.isEnabled()) return;
        
        try {
            // 他のMODや設定で上書きされるのを防ぐため、描画直前に強制的にオフにする
            net.minecraftforge.client.GuiIngameForge.renderObjective = false;
        } catch (Throwable ignored) {}

        renderCustomScoreboard(event.resolution);
    }

    private boolean firstInit = true;

    private void renderCustomScoreboard(ScaledResolution sr) {
        if (mc.theWorld == null) return;
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1); // 1 = SIDEBAR

        if (objective == null) return;

        Collection<Score> scores = scoreboard.getSortedScores(objective);
        List<Score> list = scores.stream()
                .filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());

        if (list.isEmpty()) return;

        // スコアボードの幅を計算
        int maxWidth = mc.fontRendererObj.getStringWidth(objective.getDisplayName());
        for (Score score : list) {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            String text = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
            maxWidth = Math.max(maxWidth, mc.fontRendererObj.getStringWidth(text));
        }

        int height = list.size() * mc.fontRendererObj.FONT_HEIGHT;
        
        // デフォルト位置の初期化 (右端基準)
        if (firstInit && this.getX() == 0 && this.getY() == 0) {
            this.setX(sr.getScaledWidth() - 5); // 右端から5ピクセルの位置を基準にする
            this.setY(sr.getScaledHeight() / 2 - height / 2);
            firstInit = false;
        }

        // x は「右端」の座標として扱う
        int xRight = this.getX();
        int yStart = this.getY();
        int xStart = xRight - maxWidth;

        // 背景描画
        net.minecraft.client.renderer.GlStateManager.enableBlend();
        net.minecraft.client.renderer.GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Gui.drawRect(xStart - 2, yStart - mc.fontRendererObj.FONT_HEIGHT - 3, xRight + 2, yStart + height + 2, 0x60000000);

        // タイトル描画
        mc.fontRendererObj.drawStringWithShadow(objective.getDisplayName(), xStart + (maxWidth - mc.fontRendererObj.getStringWidth(objective.getDisplayName())) / 2, yStart - mc.fontRendererObj.FONT_HEIGHT - 1, 0xFFFFFF);

        // スコア項目描画
        int i = 0;
        for (int idx = list.size() - 1; idx >= 0; idx--) {
            Score score = list.get(idx);
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            String text = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
            int y = yStart + (i * mc.fontRendererObj.FONT_HEIGHT);
            
            mc.fontRendererObj.drawStringWithShadow(text, xStart, y, 0xFFFFFF);

            // 赤い数字（設定時のみ）
            if (showNumbers.isEnabled()) {
                String scoreText = "\u00A7c" + score.getScorePoints();
                mc.fontRendererObj.drawStringWithShadow(scoreText, xRight - mc.fontRendererObj.getStringWidth(scoreText), y, 0xFFFFFF);
            }
            i++;
        }
        net.minecraft.client.renderer.GlStateManager.disableBlend();
    }
}
