package dev.windv.wvc.module.system;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Screenshot Manager (Key Input Version)
 * F2キー押下を検知し、最新のスクリーンショットをクリップボードにコピーします。
 */
public class ScreenshotManagerModule extends WVCModule {

    private final Minecraft mc = Minecraft.func_71410_x();

    public ScreenshotManagerModule() {
        super("ScreenshotManager", true);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (!this.isEnabled()) return;

        // F2キー（スクリーンショット標準キー）が押されたかチェック
        if (Keyboard.isKeyDown(Keyboard.KEY_F2)) {
            captureAndCopy();
        }
    }

    private void captureAndCopy() {
        // 別スレッドで最新ファイルを探索（撮影完了を待つため）
        new Thread(() -> {
            try {
                // マイクラの撮影・保存処理が終わるのを待つ
                Thread.sleep(700);
                
                File screenshotsDir = new File(mc.mcDataDir, "screenshots");
                if (!screenshotsDir.exists() || !screenshotsDir.isDirectory()) return;

                // 最新のPNGファイルを取得
                File[] files = screenshotsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
                if (files == null || files.length == 0) return;

                Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
                File latest = files[0];

                // 1秒以内に作成されたものなら最新とみなす
                if (System.currentTimeMillis() - latest.lastModified() < 3000) {
                    BufferedImage image = ImageIO.read(latest);
                    if (image != null) {
                        copyToClipboard(image);
                        mc.addScheduledTask(() -> {
                            mc.thePlayer.addChatMessage(new ChatComponentText(
                                EnumChatFormatting.BLUE + "[WVC] " + EnumChatFormatting.GRAY + "Latest screenshot copied to clipboard!"
                            ));
                        });
                    }
                }
            } catch (Exception e) {
                WVCMod.LOGGER.error("Failed to copy screenshot", e);
            }
        }).start();
    }

    private void copyToClipboard(BufferedImage image) {
        TransferableImage transferable = new TransferableImage(image);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(transferable, null);
    }

    private static class TransferableImage implements Transferable {
        private final Image image;
        public TransferableImage(Image image) { this.image = image; }
        @Override public DataFlavor[] getTransferDataFlavors() { return new DataFlavor[]{DataFlavor.imageFlavor}; }
        @Override public boolean isDataFlavorSupported(DataFlavor flavor) { return DataFlavor.imageFlavor.equals(flavor); }
        @Override public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (DataFlavor.imageFlavor.equals(flavor) && image != null) return image;
            throw new UnsupportedFlavorException(flavor);
        }
    }
}
