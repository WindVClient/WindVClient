package dev.windv.wvc.module.system;

import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.util.RecordingManager;
import dev.windv.wvc.settings.SliderSetting;
import net.minecraft.client.Minecraft;

public class ScreenRecorderModule extends WVCModule {

    private final SliderSetting fps = new SliderSetting("FPS", 30.0, 10.0, 60.0, true);

    public ScreenRecorderModule() {
        super("Screen Recorder", false);
        addSetting(fps);
    }

    @Override
    public void onEnable() {
        if (RecordingManager.INSTANCE.isDownloading()) {
            Minecraft.func_71410_x().field_71439_g.func_145747_a(new net.minecraft.util.ChatComponentText("§b[WindV] §fFFmpegをダウンロード中です。完了までお待ちください..."));
            setEnabled(false);
            return;
        }
        RecordingManager.INSTANCE.startRecording((int) fps.getValue());
    }

    @Override
    public void onDisable() {
        RecordingManager.INSTANCE.stopRecording();
    }
}
