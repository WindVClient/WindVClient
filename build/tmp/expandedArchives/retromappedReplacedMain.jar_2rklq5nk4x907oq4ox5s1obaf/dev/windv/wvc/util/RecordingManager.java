package dev.windv.wvc.util;

import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RecordingManager {
    public static final RecordingManager INSTANCE = new RecordingManager();

    private boolean recording = false;
    private boolean downloading = false;
    private Process ffmpegProcess;
    private OutputStream ffmpegStream;
    private final ExecutorService encoderExecutor = Executors.newSingleThreadExecutor();
    private ByteBuffer pixelBuffer;
    private String ffmpegPath = "ffmpeg"; // デフォルトはPATHのffmpeg

    public RecordingManager() {
        checkAndDownloadFFmpeg();
    }

    private void checkAndDownloadFFmpeg() {
        File binDir = new File(Minecraft.func_71410_x().field_71412_D, "WVC/bin");
        File ffmpegFile = new File(binDir, "ffmpeg.exe");

        if (ffmpegFile.exists()) {
            ffmpegPath = ffmpegFile.getAbsolutePath();
            return;
        }

        // PATHにあるかチェック
        try {
            Process p = Runtime.getRuntime().exec("ffmpeg -version");
            if (p.waitFor() == 0) return;
        } catch (Exception ignored) {}

        // どちらにもなければダウンロード
        downloadFFmpeg(binDir, ffmpegFile);
    }

    private void downloadFFmpeg(File binDir, File ffmpegFile) {
        if (downloading) return;
        downloading = true;

        new Thread(() -> {
            try {
                System.out.println("[WVC] FFmpeg not found. Downloading automatically...");
                if (!binDir.exists()) binDir.mkdirs();

                // FFmpeg Essentials build (Windows)
                URL url = new URL("https://www.gyan.dev/ffmpeg/builds/ffmpeg-release-essentials.zip");
                try (InputStream in = url.openStream(); ZipInputStream zin = new ZipInputStream(in)) {
                    ZipEntry entry;
                    while ((entry = zin.getNextEntry()) != null) {
                        if (entry.getName().endsWith("ffmpeg.exe")) {
                            try (FileOutputStream out = new FileOutputStream(ffmpegFile)) {
                                byte[] buffer = new byte[8192];
                                int len;
                                while ((len = zin.read(buffer)) > 0) out.write(buffer, 0, len);
                            }
                            System.out.println("[WVC] FFmpeg installed successfully at: " + ffmpegFile.getAbsolutePath());
                            ffmpegPath = ffmpegFile.getAbsolutePath();
                            break;
                        }
                        zin.closeEntry();
                    }
                }
            } catch (Exception e) {
                System.err.println("[WVC] Failed to download FFmpeg: " + e.getMessage());
            } finally {
                downloading = false;
            }
        }).start();
    }

    public void startRecording(int fps) {
        if (recording || downloading) return;

        Minecraft mc = Minecraft.func_71410_x();
        int width = mc.field_71443_c;
        int height = mc.field_71440_d;

        File dir = new File(mc.field_71412_D, "recordings");
        if (!dir.exists()) dir.mkdirs();

        String fileName = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".mp4";
        File outputFile = new File(dir, fileName);

        try {
            ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath, "-y", "-f", "rawvideo", "-pix_fmt", "rgb24",
                "-s", width + "x" + height, "-r", String.valueOf(fps),
                "-i", "-", "-c:v", "libx264", "-preset", "ultrafast", "-crf", "23",
                outputFile.getAbsolutePath()
            );
            pb.redirectErrorStream(true);
            ffmpegProcess = pb.start();
            ffmpegStream = ffmpegProcess.getOutputStream();
            recording = true;
            pixelBuffer = BufferUtils.createByteBuffer(width * height * 3);
            
            System.out.println("Recording started: " + outputFile.getName());
        } catch (Exception e) {
            System.err.println("Failed to start recording. Please wait for FFmpeg to finish downloading.");
            recording = false;
        }
    }

    public void stopRecording() {
        if (!recording) return;
        recording = false;
        try {
            if (ffmpegStream != null) {
                ffmpegStream.flush();
                ffmpegStream.close();
            }
            if (ffmpegProcess != null) {
                ffmpegProcess.waitFor();
            }
            System.out.println("Recording stopped.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRender() {
        if (!recording) return;

        Minecraft mc = Minecraft.func_71410_x();
        int width = mc.field_71443_c;
        int height = mc.field_71440_d;

        pixelBuffer.clear();
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixelBuffer);

        byte[] pixels = new byte[width * height * 3];
        pixelBuffer.get(pixels);

        encoderExecutor.execute(() -> {
            try {
                if (ffmpegStream != null) {
                    byte[] flipped = new byte[pixels.length];
                    for (int y = 0; y < height; y++) {
                        System.arraycopy(pixels, (height - y - 1) * width * 3, flipped, y * width * 3, width * 3);
                    }
                    ffmpegStream.write(flipped);
                }
            } catch (Exception e) {
                stopRecording();
            }
        });
    }

    public boolean isRecording() { return recording; }
    public boolean isDownloading() { return downloading; }
}
