package dev.windv.wvc.module.system;

import dev.windv.wvc.gui.CustomGuiNewChat;
import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.BooleanSetting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * ChatMOD モジュール
 * チャットの背景透明化、コピー、ChatHeadsを管理
 */
public class ChatMod extends WVCModule {

    private boolean initialized = false;
    
    public final BooleanSetting transparent;
    public final BooleanSetting copy;
    public final BooleanSetting heads;

    public ChatMod() {
        super("ChatMOD", true);
        this.addSetting(transparent = new BooleanSetting("Transparent BG", true));
        this.addSetting(copy = new BooleanSetting("Right-click Copy", true));
        this.addSetting(heads = new BooleanSetting("ChatHeads", true));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!initialized && Minecraft.func_71410_x().field_71456_v != null) {
            try {
                // 難読化名 field_73840_e (persistantChatGUI) にも対応
                net.minecraftforge.fml.relauncher.ReflectionHelper.setPrivateValue(
                    net.minecraft.client.gui.GuiIngame.class, 
                    Minecraft.func_71410_x().field_71456_v, 
                    new CustomGuiNewChat(Minecraft.func_71410_x()), 
                    "persistantChatGUI", "field_73840_e"
                );
                initialized = true;
            } catch (Exception e) {
                initialized = true;
            }
        }
    }
    
    // 背景透明化の設定値などを保持
}
