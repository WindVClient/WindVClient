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

    private final Minecraft mc = Minecraft.func_71410_x();
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
        if (mc.field_71441_e == null) return;
        Scoreboard scoreboard = mc.field_71441_e.func_96441_U();
        ScoreObjective objective = scoreboard.func_96539_a(1); // 1 = SIDEBAR

        if (objective == null) return;

        Collection<Score> scores = scoreboard.func_96534_i(objective);
        List<Score> list = scores.stream()
                .filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());

        if (list.isEmpty()) return;

        // スコアボードの幅を計算
        int maxWidth = mc.field_71466_p.func_78256_a(objective.func_96678_d());
        for (Score score : list) {
            ScorePlayerTeam team = scoreboard.func_96509_i(score.func_96653_e());
            String text = ScorePlayerTeam.func_96667_a(team, score.func_96653_e());
            maxWidth = Math.max(maxWidth, mc.field_71466_p.func_78256_a(text));
        }

        int height = list.size() * mc.field_71466_p.field_78288_b;
        
        // デフォルト位置の初期化 (右端基準)
        if (firstInit && this.getX() == 0 && this.getY() == 0) {
            this.setX(sr.func_78326_a() - 5); // 右端から5ピクセルの位置を基準にする
            this.setY(sr.func_78328_b() / 2 - height / 2);
            firstInit = false;
        }

        // x は「右端」の座標として扱う
        int xRight = this.getX();
        int yStart = this.getY();
        int xStart = xRight - maxWidth;

        // 背景描画
        net.minecraft.client.renderer.GlStateManager.func_179147_l();
        net.minecraft.client.renderer.GlStateManager.func_179120_a(770, 771, 1, 0);
        Gui.func_73734_a(xStart - 2, yStart - mc.field_71466_p.field_78288_b - 3, xRight + 2, yStart + height + 2, 0x60000000);

        // タイトル描画
        mc.field_71466_p.func_175063_a(objective.func_96678_d(), xStart + (maxWidth - mc.field_71466_p.func_78256_a(objective.func_96678_d())) / 2, yStart - mc.field_71466_p.field_78288_b - 1, 0xFFFFFF);

        // スコア項目描画
        int i = 0;
        for (int idx = list.size() - 1; idx >= 0; idx--) {
            Score score = list.get(idx);
            ScorePlayerTeam team = scoreboard.func_96509_i(score.func_96653_e());
            String text = ScorePlayerTeam.func_96667_a(team, score.func_96653_e());
            int y = yStart + (i * mc.field_71466_p.field_78288_b);
            
            mc.field_71466_p.func_175063_a(text, xStart, y, 0xFFFFFF);

            // 赤い数字（設定時のみ）
            if (showNumbers.isEnabled()) {
                String scoreText = "\u00A7c" + score.func_96652_c();
                mc.field_71466_p.func_175063_a(scoreText, xRight - mc.field_71466_p.func_78256_a(scoreText), y, 0xFFFFFF);
            }
            i++;
        }
        net.minecraft.client.renderer.GlStateManager.func_179084_k();
    }
}
