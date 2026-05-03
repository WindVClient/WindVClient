package dev.windv.wvc.module.hud;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.BooleanSetting;
import dev.windv.wvc.settings.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

/**
 * CPS Module with Advanced Settings
 */
public class CpsModule extends WVCModule {

    private final dev.windv.wvc.settings.BooleanSetting countLeft;
    private final dev.windv.wvc.settings.BooleanSetting countRight;
    private final dev.windv.wvc.settings.ModeSetting bracketMode;
    private final dev.windv.wvc.settings.ColorSetting textColor;

    // Click data
    private static final int MAX_CLICKS = 200;
    private final long[] leftClickTimes = new long[MAX_CLICKS];
    private final long[] rightClickTimes = new long[MAX_CLICKS];
    private int leftHead = 0;
    private int rightHead = 0;
    private boolean prevLeft = false;
    private boolean prevRight = false;

    public CpsModule(boolean enabled) {
        super("CPS", enabled);
        this.setX(2);
        this.setY(12);
        this.addSetting(countLeft = new dev.windv.wvc.settings.BooleanSetting("Count Left", true));
        this.addSetting(countRight = new dev.windv.wvc.settings.BooleanSetting("Count Right", true));
        this.addSetting(bracketMode = new dev.windv.wvc.settings.ModeSetting("Brackets", 0, "None", "[ ]", "( )", "< >"));
        this.addSetting(textColor = new dev.windv.wvc.settings.ColorSetting("Text Color", 255, 255, 255));
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !isEnabled()) return;

        boolean leftNow = Mouse.isButtonDown(0);
        boolean rightNow = Mouse.isButtonDown(1);
        long now = System.currentTimeMillis();

        if (leftNow && !prevLeft) recordClick(now, true);
        if (rightNow && !prevRight) recordClick(now, false);

        prevLeft = leftNow;
        prevRight = rightNow;

        int leftCps = countRecentClicks(now, true);
        int rightCps = countRecentClicks(now, false);
        
        String display;
        if (countLeft.isEnabled() && countRight.isEnabled()) {
            display = leftCps + " | " + rightCps;
        } else if (countLeft.isEnabled()) {
            display = "L: " + leftCps;
        } else if (countRight.isEnabled()) {
            display = "R: " + rightCps;
        } else {
            display = "CPS: 0";
        }

        String label = formatLabel(display);
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

    private void recordClick(long time, boolean left) {
        if (left) {
            leftClickTimes[Math.abs(leftHead % MAX_CLICKS)] = time;
            leftHead++;
        } else {
            rightClickTimes[Math.abs(rightHead % MAX_CLICKS)] = time;
            rightHead++;
        }
    }

    private int countRecentClicks(long now, boolean left) {
        int count = 0;
        long threshold = now - 1000L;
        long[] times = left ? leftClickTimes : rightClickTimes;
        int head = left ? leftHead : rightHead;
        
        int total = Math.min(head, MAX_CLICKS);
        for (int i = 0; i < total; i++) {
            if (times[i] > threshold) count++;
        }
        return count;
    }
}
