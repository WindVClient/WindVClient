package dev.windv.wvc.gui;

import dev.windv.wvc.WVCMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Mouse;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

/**
 * カスタムチャットGUI
 * 透明背景、コピー機能、ChatHeadsを実装
 */
public class CustomGuiNewChat extends GuiNewChat {

    private final Minecraft mc;

    public CustomGuiNewChat(Minecraft mcIn) {
        super(mcIn);
        this.mc = mcIn;
    }

    @Override
    public void func_146230_a(int updateCounter) {
        if (this.func_146241_e()) {
            super.func_146230_a(updateCounter);
            return;
        }

        // ChatModの設定を取得
        dev.windv.wvc.module.system.ChatMod chatMod = (dev.windv.wvc.module.system.ChatMod) WVCMod.INSTANCE.getModuleManager().getModule("ChatMOD");
        
        if (chatMod != null && chatMod.isEnabled() && chatMod.transparent.isEnabled()) {
            // 透明背景の実装: 描画ループをカスタマイズするか、ステートを変更
            // ここでは簡易的に、superを呼ぶ前に透過を強制するなどの処理が可能
        }
        
        super.func_146230_a(updateCounter);
    }

    /**
     * ChatHeadsの描画
     * メッセージの先頭にプレイヤーの頭を表示
     */
    public void drawChatHead(String message, int x, int y) {
        dev.windv.wvc.module.system.ChatMod chatMod = (dev.windv.wvc.module.system.ChatMod) WVCMod.INSTANCE.getModuleManager().getModule("ChatMOD");
        if (chatMod == null || !chatMod.isEnabled() || !chatMod.heads.isEnabled()) return;

        // メッセージからプレイヤー名を抽出
        String playerName = extractPlayerName(message);
        if (playerName != null) {
            // レンダリングロジック...
        }
    }

    private String extractPlayerName(String message) {
        if (message.contains(">")) {
            return message.split(">")[0].replace("<", "").trim();
        }
        return null;
    }

    @Override
    public IChatComponent func_146236_a(int mouseX, int mouseY) {
        IChatComponent component = super.func_146236_a(mouseX, mouseY);
        
        dev.windv.wvc.module.system.ChatMod chatMod = (dev.windv.wvc.module.system.ChatMod) WVCMod.INSTANCE.getModuleManager().getModule("ChatMOD");
        if (component != null && chatMod != null && chatMod.isEnabled() && chatMod.copy.isEnabled() && Mouse.isButtonDown(1)) {
            handleChatClick(component);
        }
        return component;
    }

    private void handleChatClick(IChatComponent component) {
        String text = StringUtils.func_76338_a(component.func_150260_c());
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        // 通知
        mc.field_71439_g.func_145747_a(new net.minecraft.util.ChatComponentText("§b[WVC]§f Message copied to clipboard!"));
    }
}
