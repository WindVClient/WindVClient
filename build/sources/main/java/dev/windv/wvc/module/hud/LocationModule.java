package dev.windv.wvc.module.hud;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.BooleanSetting;
import dev.windv.wvc.settings.ColorSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Location モジュール
 * 現在の座標(X, Y, Z)とバイオームを表示します。
 */
public class LocationModule extends WVCModule {

    private final BooleanSetting showBiome;
    private final ColorSetting textColor;

    public LocationModule() {
        super("Location", true);
        this.setX(10);
        this.setY(100);
        this.addSetting(showBiome = new BooleanSetting("Show Biome", true));
        this.addSetting(textColor = new ColorSetting("Text Color", 255, 255, 255));
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !this.isEnabled()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // 座標の取得と整形
        int x = (int) mc.thePlayer.posX;
        int y = (int) mc.thePlayer.posY;
        int z = (int) mc.thePlayer.posZ;
        String coordsText = String.format("XYZ: %d / %d / %d", x, y, z);

        // バイオームの取得
        String biomeText = "";
        if (showBiome.isEnabled()) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ);
            BiomeGenBase biome = mc.theWorld.getBiomeGenForCoords(pos);
            biomeText = "Biome: " + (biome != null ? biome.biomeName : "Unknown");
        }

        // 描画
        int renderX = this.getX();
        int renderY = this.getY();
        int color = textColor.getColor();
        
        // シャドウ付きで描画
        WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow(coordsText, renderX, renderY, color);
        if (showBiome.isEnabled()) {
            WVCMod.INSTANCE.getFontRenderer().drawStringWithShadow(biomeText, renderX, renderY + 11, color);
        }
    }
}
