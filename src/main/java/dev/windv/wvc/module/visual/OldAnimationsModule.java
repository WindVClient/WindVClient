package dev.windv.wvc.module.visual;

import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.BooleanSetting;

/**
 * Old Animations - 1.7時代の剣ガードやヒットアニメーションを再現
 */
public class OldAnimationsModule extends WVCModule {

    private final BooleanSetting oldBlock;
    private final BooleanSetting oldSwing;
    private final BooleanSetting oldEating;
    private final BooleanSetting oldBow;

    public OldAnimationsModule(boolean enabled) {
        super("OldAnimations", enabled);
        this.addSetting(oldBlock = new BooleanSetting("1.7 Blocking", true));
        this.addSetting(oldSwing = new BooleanSetting("1.7 Swing", true));
        this.addSetting(oldEating = new BooleanSetting("1.7 Eating", true));
        this.addSetting(oldBow = new BooleanSetting("1.7 Bow", true));
    }

    public boolean isOldBlock() {
        return isEnabled() && oldBlock.isEnabled();
    }

    public boolean isOldSwing() {
        return isEnabled() && oldSwing.isEnabled();
    }
    
    public boolean isOldEating() {
        return isEnabled() && oldEating.isEnabled();
    }
    
    public boolean isOldBow() {
        return isEnabled() && oldBow.isEnabled();
    }
}
