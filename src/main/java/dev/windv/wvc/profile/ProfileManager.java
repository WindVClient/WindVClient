package dev.windv.wvc.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.windv.wvc.WVCMod;
import dev.windv.wvc.config.WVCConfig;
import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.module.visual.CrosshairModule;
import dev.windv.wvc.settings.BooleanSetting;
import dev.windv.wvc.settings.ColorSetting;
import dev.windv.wvc.settings.KeybindSetting;
import dev.windv.wvc.settings.Setting;
import dev.windv.wvc.settings.SliderSetting;
import net.minecraft.client.Minecraft;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * プロファイルマネージャー
 * 設定、HUD位置、クロスヘア画像をプロファイルごとに管理します。
 */
public class ProfileManager {

    private final File baseDir;
    private String currentProfile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ProfileManager() {
        this.baseDir = new File(Minecraft.getMinecraft().mcDataDir, "WVC");
        if (!baseDir.exists()) baseDir.mkdirs();
        
        this.currentProfile = WVCMod.INSTANCE.getConfig().getSelectedProfile();
        if (this.currentProfile == null || this.currentProfile.isEmpty()) this.currentProfile = "Default";
        
        File defDir = new File(baseDir, "Default");
        if (!defDir.exists()) defDir.mkdirs();
    }

    public List<String> getProfileList() {
        List<String> list = new ArrayList<>();
        File[] files = baseDir.listFiles(File::isDirectory);
        if (files != null) for (File f : files) list.add(f.getName());
        if (list.isEmpty()) list.add("Default");
        return list;
    }

    public void switchProfile(String name) {
        saveCurrentProfile();
        this.currentProfile = name;
        WVCMod.INSTANCE.getConfig().setSelectedProfile(name);
        loadProfile(name);
    }

    public void createProfile(String name) {
        File dir = new File(baseDir, name);
        if (!dir.exists()) dir.mkdirs();
        switchProfile(name);
    }

    public void saveCurrentProfile() {
        File profileDir = new File(baseDir, currentProfile);
        if (!profileDir.exists()) profileDir.mkdirs();

        File settingsFile = new File(profileDir, "settings.json");
        JsonObject json = new JsonObject();
        WVCConfig config = WVCMod.INSTANCE.getConfig();
        
        json.addProperty("themeColor", config.getThemeColor());
        
        // HUD 位置の保存
        JsonObject hudJson = new JsonObject();
        hudJson.addProperty("x", config.getHudX());
        hudJson.addProperty("y", config.getHudY());
        hudJson.addProperty("opacity", config.getHudOpacity());
        json.add("hud", hudJson);

        for (WVCModule m : WVCMod.INSTANCE.getModuleManager().getModules()) {
            JsonObject modJson = new JsonObject();
            modJson.addProperty("enabled", m.isEnabled());
            modJson.addProperty("x", m.getX());
            modJson.addProperty("y", m.getY());
            
            JsonObject settingsJson = new JsonObject();
            for (Setting s : m.getSettings()) {
                if (s instanceof BooleanSetting) settingsJson.addProperty(s.getName(), ((BooleanSetting) s).isEnabled());
                else if (s instanceof SliderSetting) settingsJson.addProperty(s.getName(), ((SliderSetting) s).getValue());
                else if (s instanceof ColorSetting) settingsJson.addProperty(s.getName(), ((ColorSetting) s).getColor());
                else if (s instanceof KeybindSetting) settingsJson.addProperty(s.getName(), ((KeybindSetting) s).getKeyCode());
            }
            modJson.add("settings", settingsJson);
            json.add(m.getName(), modJson);
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(settingsFile), "UTF-8")) {
            gson.toJson(json, writer);
        } catch (IOException e) { e.printStackTrace(); }

        CrosshairModule crosshair = (CrosshairModule) WVCMod.INSTANCE.getModuleManager().getModule("Crosshair");
        if (crosshair != null) saveCrosshairImage(new File(profileDir, "crosshair.png"), crosshair.getDots());
    }

    public void loadProfile(String name) {
        File profileDir = new File(baseDir, name);
        File settingsFile = new File(profileDir, "settings.json");
        if (!settingsFile.exists()) return;

        try (Reader reader = new InputStreamReader(new FileInputStream(settingsFile), "UTF-8")) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            if (json == null) return;

            WVCConfig config = WVCMod.INSTANCE.getConfig();
            if (json.has("themeColor")) config.setThemeColor(json.get("themeColor").getAsInt());
            
            // HUD 位置の読み込み
            if (json.has("hud")) {
                JsonObject hudJson = json.getAsJsonObject("hud");
                if (hudJson.has("x")) config.setHudX(hudJson.get("x").getAsInt());
                if (hudJson.has("y")) config.setHudY(hudJson.get("y").getAsInt());
                if (hudJson.has("opacity")) config.setHudOpacity(hudJson.get("opacity").getAsFloat());
            }

            for (WVCModule m : WVCMod.INSTANCE.getModuleManager().getModules()) {
                if (json.has(m.getName())) {
                    JsonObject modJson = json.getAsJsonObject(m.getName());
                    m.setEnabled(modJson.get("enabled").getAsBoolean());
                    if (modJson.has("x")) m.setX(modJson.get("x").getAsInt());
                    if (modJson.has("y")) m.setY(modJson.get("y").getAsInt());

                    JsonObject settingsJson = modJson.getAsJsonObject("settings");
                    for (Setting s : m.getSettings()) {
                        if (settingsJson.has(s.getName())) {
                            if (s instanceof BooleanSetting) ((BooleanSetting) s).setEnabled(settingsJson.get(s.getName()).getAsBoolean());
                            else if (s instanceof SliderSetting) ((SliderSetting) s).setValue(settingsJson.get(s.getName()).getAsDouble());
                            else if (s instanceof ColorSetting) ((ColorSetting) s).setColor(settingsJson.get(s.getName()).getAsInt());
                            else if (s instanceof KeybindSetting) ((KeybindSetting) s).setKeyCode(settingsJson.get(s.getName()).getAsInt());
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        CrosshairModule crosshair = (CrosshairModule) WVCMod.INSTANCE.getModuleManager().getModule("Crosshair");
        if (crosshair != null) loadCrosshairImage(new File(profileDir, "crosshair.png"), crosshair.getDots());
    }

    private void saveCrosshairImage(File file, boolean[] dots) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < 256; i++) img.setRGB(i % 16, i / 16, dots[i] ? 0xFFFFFFFF : 0x00000000);
        try { ImageIO.write(img, "png", file); } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadCrosshairImage(File file, boolean[] dots) {
        if (!file.exists()) return;
        try {
            BufferedImage img = ImageIO.read(file);
            if (img != null && img.getWidth() == 16 && img.getHeight() == 16) {
                for (int i = 0; i < 256; i++) dots[i] = (img.getRGB(i % 16, i / 16) & 0xFF000000) != 0;
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public String getCurrentProfile() { return currentProfile; }
}
