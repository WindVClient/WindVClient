package dev.windv.wvc;

import dev.windv.wvc.config.WVCConfig;
import dev.windv.wvc.module.ModuleManager;
import dev.windv.wvc.profile.ProfileManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Wind V Client - メインModクラス
 * PvP特化の軽量クライアント for Minecraft 1.8.9 Forge
 */
@Mod(
    modid   = WVCMod.MODID,
    name    = WVCMod.NAME,
    version = WVCMod.VERSION,
    clientSideOnly = true,
    acceptedMinecraftVersions = "[1.8.9]"
)
public class WVCMod {

    // Mod定数
    public static final String MODID   = "windvclient";
    public static final String NAME    = "Wind V Client";
    public static final String VERSION = "1.0.0";

    // ロガー
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    // シングルトンインスタンス
    @Mod.Instance(MODID)
    public static WVCMod INSTANCE;

    // 設定マネージャー
    private WVCConfig config;

    // モジュールマネージャー
    private ModuleManager moduleManager;

    // プロファイルマネージャー
    private ProfileManager profileManager;

    // カスタムフォントレンダラー
    private dev.windv.wvc.util.font.CFontRenderer fontRenderer;

    /**
     * プリ初期化フェーズ - 設定の読み込み
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("[WVC] Initializing Wind V Client v{}...", VERSION);
        config = new WVCConfig(event.getSuggestedConfigurationFile());
        config.load();
    }

    /**
     * 初期化フェーズ - モジュールの登録
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        // Initialize custom font (Tahoma is cleaner than Verdana)
        fontRenderer = new dev.windv.wvc.util.font.CFontRenderer(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 18), true, true);

        moduleManager = new ModuleManager();
        moduleManager.registerModules();

        profileManager = new ProfileManager();
        profileManager.loadProfile(profileManager.getCurrentProfile());

        // 共通イベント（ロゴ描画など）を登録
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new dev.windv.wvc.event.RenderEventHandler());
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new dev.windv.wvc.event.InputEventHandler());
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new dev.windv.wvc.event.OldAnimationsExtraHandler());

        // プロ仕様：リフレクションを使用してマイクラの深層（ItemRenderer）を強制的にすり替え
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.func_71410_x();
        try {
            net.minecraftforge.fml.relauncher.ReflectionHelper.setPrivateValue(
                net.minecraft.client.renderer.EntityRenderer.class, 
                mc.field_71460_t, 
                new dev.windv.wvc.event.OldItemRenderer(mc, mc.func_175597_ag()), 
                "itemRenderer", "field_78516_c"
            );
        } catch (Exception e) {
            LOGGER.error("[WVC] Failed to inject OldItemRenderer", e);
        }

        LOGGER.info("[WVC] Initialization complete. WVC is ready!");
    }

    public dev.windv.wvc.util.font.CFontRenderer getFontRenderer() {
        return fontRenderer;
    }

    // ゲッター
    public WVCConfig getConfig() { return config; }
    public ModuleManager getModuleManager() { return moduleManager; }
    public ProfileManager getProfileManager() { return profileManager; }
}
