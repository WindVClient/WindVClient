package dev.windv.wvc.module.hud;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.ColorSetting;
import dev.windv.wvc.settings.ModeSetting;
import dev.windv.wvc.settings.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Ping Counter - Optimized for Hypixel
 */
public class PingModule extends WVCModule {

    private final ModeSetting bracketMode;
    private final ColorSetting textColor;
    
    private long lastUpdate = 0L;
    private int cachedPing = 0;

    public PingModule(boolean enabled) {
        super("Ping", enabled);
        this.setX(2);
        this.setY(22);
        this.addSetting(bracketMode = new ModeSetting("Brackets", 0, "None", "[ ]", "( )", "< >"));
        this.addSetting(textColor = new ColorSetting("Text Color", 255, 255, 255));
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (!isEnabled()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        long now = System.currentTimeMillis();
        if (now - lastUpdate >= 1000L) {
            lastUpdate = now;
            cachedPing = fetchPing(mc);
        }

        String label = formatLabel("Ping: " + cachedPing + "ms");
        WVCMod.INSTANCE.getFontRenderer().drawString(label, (float)this.getX(), (float)this.getY(), textColor.getColor());
    }

    private int fetchPing(Minecraft mc) {
        if (mc.isSingleplayer()) return 0;
        
        try {
            // 接続が確立され、プレイヤー情報が完全に準備できるまで待機
            if (mc.getNetHandler() != null && mc.thePlayer != null && mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()) != null) {
                NetworkPlayerInfo info = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
                if (info != null) return info.getResponseTime();
            }
            
            // 予備の取得方法
            if (mc.getCurrentServerData() != null) return (int) mc.getCurrentServerData().pingToServer;
        } catch (Exception ignored) {}
        
        return cachedPing;
    }

    private String formatLabel(String text) {
        switch (bracketMode.getMode()) {
            case "[ ]": return "[" + text + "]";
            case "( )": return "(" + text + ")";
            case "< >": return "<" + text + ">";
            default: return text;
        }
    }
}
