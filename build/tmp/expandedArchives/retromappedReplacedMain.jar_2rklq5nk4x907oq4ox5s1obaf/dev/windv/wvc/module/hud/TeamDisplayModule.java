package dev.windv.wvc.module.hud;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.gui.GuiEditHUD;
import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * TeamDisplay モジュール
 * チームメイトの名前と体力をHUDに表示します。
 */
public class TeamDisplayModule extends WVCModule {

    public TeamDisplayModule() {
        super("TeamDisplay", true);
        this.setX(10);
        this.setY(180);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !this.isEnabled()) return;

        Minecraft mc = Minecraft.func_71410_x();
        if (mc.field_71439_g == null || mc.field_71441_e == null) return;

        // 自分の名前の色（チーム色）を取得
        String myFormatted = mc.field_71439_g.func_145748_c_().func_150254_d();
        String myColorCode = getPrimaryColorCode(myFormatted);
        if (myColorCode == null) return;

        List<EntityPlayer> teammates = new ArrayList<>();
        for (EntityPlayer player : mc.field_71441_e.field_73010_i) {
            if (player == mc.field_71439_g) continue;
            
            // プレイヤーの名前の色が自分と同じかチェック
            String playerFormatted = player.func_145748_c_().func_150254_d();
            String playerColorCode = getPrimaryColorCode(playerFormatted);
            
            // 色が一致し、且つNPCでない（Tabリストに存在する）プレイヤーを味方とする
            if (myColorCode.equals(playerColorCode) && !isNPC(player)) {
                teammates.add(player);
            }
        }

        int renderX = this.getX();
        int renderY = this.getY();

        if (teammates.isEmpty()) {
            // EditHUD時はダミーを表示
            if (mc.field_71462_r instanceof GuiEditHUD) {
                renderDummy(renderX, renderY);
            }
            return;
        }
        
        // ヘッダー
        // ヘッダー (§lを使わず太字風にするかプレーンに)
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow("Team Members", renderX, renderY, 0xFFFFFF);
        int offset = 14;

        for (EntityPlayer teammate : teammates) {
            float health = teammate.func_110143_aJ();
            String name = teammate.func_70005_c_();
            // ハート記号をユニコードエスケープ (\u2764) に変更
            String text = String.format("%s: %.1f \u2764", name, health);
            
            // 体力に応じて色を変えて描画
            int color = 0x00FF00; // 緑
            if (health < 10) color = 0xFFFF00; // 黄
            if (health < 5) color = 0xFF0000; // 赤
            
            WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow(text, renderX, renderY + offset, color);
            offset += 11;
        }
    }

    private String getPrimaryColorCode(String text) {
        if (text == null || text.length() < 2) return null;
        int index = text.indexOf('\u00a7');
        if (index != -1 && index + 1 < text.length()) {
            char code = text.charAt(index + 1);
            // 無効な色（白やリセットなど）を除外したい場合はここで調整可能
            if (code == 'r' || code == 'f') {
                // 次のカラーコードを探す
                int next = text.indexOf('\u00a7', index + 2);
                if (next != -1 && next + 1 < text.length()) return "\u00a7" + text.charAt(next + 1);
            }
            return "\u00a7" + code;
        }
        return null;
    }

    private boolean isNPC(EntityPlayer player) {
        // Tabリスト（PlayerInfo）に存在しないプレイヤーはNPCである可能性が高い
        return Minecraft.func_71410_x().func_147114_u().func_175102_a(player.func_110124_au()) == null;
    }

    private void renderDummy(int x, int y) {
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow("Team Members", x, y, 0xFFFFFF);
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow("Teammate1: 20.0 \u2764", x, y + 14, 0x00FF00);
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow("Teammate2: 12.5 \u2764", x, y + 25, 0xFFFF00);
    }
}
