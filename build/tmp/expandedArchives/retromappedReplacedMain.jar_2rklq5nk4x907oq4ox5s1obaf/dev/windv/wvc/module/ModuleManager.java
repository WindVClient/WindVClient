package dev.windv.wvc.module;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.config.WVCConfig;
import dev.windv.wvc.module.hud.ArmorStatusModule;
import dev.windv.wvc.module.hud.BossbarModule;
import dev.windv.wvc.module.hud.CpsModule;
import dev.windv.wvc.module.hud.DirectionHUDModule;
import dev.windv.wvc.module.hud.FpsModule;
import dev.windv.wvc.module.hud.KeystrokesModule;
import dev.windv.wvc.module.hud.LocationModule;
import dev.windv.wvc.module.hud.PingModule;
import dev.windv.wvc.module.hud.PotionStatusModule;
import dev.windv.wvc.module.hud.ReachDisplayModule;
import dev.windv.wvc.module.hud.ScoreboardModule;
import dev.windv.wvc.module.hud.TeamDisplayModule;
import dev.windv.wvc.module.movement.SprintModule;
import dev.windv.wvc.module.movement.ToggleSneakModule;
import dev.windv.wvc.module.movement.ZoomModule;
import dev.windv.wvc.module.system.AutoTextModule;
import dev.windv.wvc.module.system.ChatConfirmationModule;
import dev.windv.wvc.module.system.JapaneseIMEModule;
import dev.windv.wvc.module.system.ScreenshotManagerModule;
import dev.windv.wvc.module.visual.CrosshairModule;
import dev.windv.wvc.module.visual.EntityCullingModule;
import dev.windv.wvc.module.visual.FullbrightModule;
import dev.windv.wvc.module.visual.MotionBlurModule;
import dev.windv.wvc.module.visual.NameTagModule;
import dev.windv.wvc.module.visual.OldAnimationsModule;
import dev.windv.wvc.module.visual.PerformanceModule;
import dev.windv.wvc.module.visual.BlockOverlayModule;
import dev.windv.wvc.module.visual.TimeChangerModule;
import dev.windv.wvc.module.visual.HitColorModule;
import dev.windv.wvc.module.visual.ItemPhysicsModule;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WVCモジュールマネージャー
 */
public class ModuleManager {

    private final List<WVCModule> modules = new ArrayList<>();

    public void registerModules() {
        WVCConfig cfg = WVCMod.INSTANCE.getConfig();

        // --- HUD系 ---
        register(new FpsModule(cfg.isFpsEnabled()));
        register(new CpsModule(cfg.isCpsEnabled()));
        register(new PingModule(cfg.isPingEnabled()));
        register(new KeystrokesModule(true));
        register(new ArmorStatusModule());
        register(new PotionStatusModule());
        register(new ReachDisplayModule());
        register(new DirectionHUDModule());
        register(new BossbarModule());
        register(new ScoreboardModule());
        register(new LocationModule());
        register(new TeamDisplayModule());

        // --- 移動系 ---
        register(new SprintModule());
        register(new ZoomModule(true));
        register(new ToggleSneakModule());

        // --- ビジュアル系 ---
        register(new CrosshairModule());
        register(new FullbrightModule(false));
        register(new MotionBlurModule(false));
        register(new EntityCullingModule());
        register(new NameTagModule());
        register(new OldAnimationsModule(true));
        register(new PerformanceModule(true));
        register(new BlockOverlayModule());
        register(new TimeChangerModule());
        register(new HitColorModule());
        register(new ItemPhysicsModule());

        // --- システム系 ---
        register(new JapaneseIMEModule());
        register(new dev.windv.wvc.module.system.ChatMod());
        register(new ScreenshotManagerModule());
        register(new ChatConfirmationModule());
        register(new AutoTextModule());

        WVCMod.LOGGER.info("[WVC] {} modules registered.", modules.size());
    }

    private void register(WVCModule module) {
        modules.add(module);
        MinecraftForge.EVENT_BUS.register(module);
        net.minecraftforge.fml.common.FMLCommonHandler.instance().bus().register(module);
    }

    public WVCModule getModule(String name) {
        for (WVCModule m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }

    public List<WVCModule> getModules() {
        return Collections.unmodifiableList(modules);
    }
}
