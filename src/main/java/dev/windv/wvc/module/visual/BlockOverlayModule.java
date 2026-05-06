package dev.windv.wvc.module.visual;

import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.BooleanSetting;
import dev.windv.wvc.settings.ColorSetting;
import dev.windv.wvc.settings.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * BlockOverlay モジュール
 * ターゲット中のブロックを強調表示します。
 */
public class BlockOverlayModule extends WVCModule {

    private final ColorSetting color;
    private final SliderSetting width;
    private final BooleanSetting outline;
    private final BooleanSetting fill;

    public BlockOverlayModule() {
        super("BlockOverlay", true);
        this.addSetting(color = new ColorSetting("Color", 255, 255, 255));
        this.addSetting(width = new SliderSetting("Line Width", 2.0, 0.5, 5.0, false));
        this.addSetting(outline = new BooleanSetting("Outline", true));
        this.addSetting(fill = new BooleanSetting("Fill", true));
    }

    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent event) {
        if (!this.isEnabled() || event.target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

        Minecraft mc = Minecraft.getMinecraft();
        BlockPos pos = event.target.getBlockPos();
        Block block = mc.theWorld.getBlockState(pos).getBlock();

        // バニラの枠線描画をキャンセル
        event.setCanceled(true);

        double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * event.partialTicks;
        double y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * event.partialTicks;
        double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * event.partialTicks;

        AxisAlignedBB bb = block.getSelectedBoundingBox(mc.theWorld, pos).expand(0.002, 0.002, 0.002).offset(-x, -y, -z);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        int c = color.getColor();
        float r = (c >> 16 & 255) / 255.0F;
        float g = (c >> 8 & 255) / 255.0F;
        float b = (c & 255) / 255.0F;

        if (fill.isEnabled()) {
            GlStateManager.color(r, g, b, 0.2F);
            drawFilledBox(bb);
        }

        if (outline.isEnabled()) {
            GL11.glLineWidth((float) width.getValue());
            GlStateManager.color(r, g, b, 0.8F);
            RenderGlobal.drawSelectionBoundingBox(bb);
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    private void drawFilledBox(AxisAlignedBB bb) {
        GL11.glBegin(GL11.GL_QUADS);
        // Bottom
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ); GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ); GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        // Top
        GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ); GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ); GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        // Front
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ); GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ); GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
        // Back
        GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ); GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ); GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        // Left
        GL11.glVertex3d(bb.minX, bb.minY, bb.minZ); GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
        GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ); GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
        // Right
        GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ); GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ); GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        GL11.glEnd();
    }
}
