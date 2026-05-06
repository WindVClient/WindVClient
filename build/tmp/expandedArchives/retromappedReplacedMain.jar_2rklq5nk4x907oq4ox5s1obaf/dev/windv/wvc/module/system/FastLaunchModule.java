package dev.windv.wvc.module.system;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * FastLaunch モジュール
 * クライアントの起動プロセスとリソース読み込みを高速化します。
 */
public class FastLaunchModule extends WVCModule {

    public FastLaunchModule() {
        super("FastLaunch", true);
    }

    // 1.8.9では、Minecraft.java の起動ループ内にある 
    // スプラッシュ画面の更新間隔をリフレクション等で操作することで
    // 体感的な起動速度を大幅に向上させることが可能です。
    // (ここでは基盤のみ作成し、実際のハックはModuleManagerでの登録時に実施される想定)
}
