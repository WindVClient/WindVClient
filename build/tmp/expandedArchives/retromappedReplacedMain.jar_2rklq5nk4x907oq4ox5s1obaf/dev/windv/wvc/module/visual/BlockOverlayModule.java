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
        if (!this.isEnabled() || event.target.field_72313_a != MovingObjectPosition.MovingObjectType.BLOCK) return;

        Minecraft mc = Minecraft.func_71410_x();
        BlockPos pos = event.target.func_178782_a();
        Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();

        // バニラの枠線描画をキャンセル
        event.setCanceled(true);

        double x = mc.field_71439_g.field_70142_S + (mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70142_S) * event.partialTicks;
        double y = mc.field_71439_g.field_70137_T + (mc.field_71439_g.field_70163_u - mc.field_71439_g.field_70137_T) * event.partialTicks;
        double z = mc.field_71439_g.field_70136_U + (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70136_U) * event.partialTicks;

        AxisAlignedBB bb = block.func_180646_a(mc.field_71441_e, pos).func_72314_b(0.002, 0.002, 0.002).func_72317_d(-x, -y, -z);

        GlStateManager.func_179147_l();
        GlStateManager.func_179120_a(770, 771, 1, 0);
        GlStateManager.func_179090_x();
        GlStateManager.func_179132_a(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        int c = color.getColor();
        float r = (c >> 16 & 255) / 255.0F;
        float g = (c >> 8 & 255) / 255.0F;
        float b = (c & 255) / 255.0F;

        if (fill.isEnabled()) {
            GlStateManager.func_179131_c(r, g, b, 0.2F);
            drawFilledBox(bb);
        }

        if (outline.isEnabled()) {
            GL11.glLineWidth((float) width.getValue());
            GlStateManager.func_179131_c(r, g, b, 0.8F);
            RenderGlobal.func_181561_a(bb);
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.func_179132_a(true);
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
    }

    private void drawFilledBox(AxisAlignedBB bb) {
        GL11.glBegin(GL11.GL_QUADS);
        // Bottom
        GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c); GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c);
        GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f); GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f);
        // Top
        GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c); GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f);
        GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f); GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c);
        // Front
        GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c); GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c);
        GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c); GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c);
        // Back
        GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f); GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f);
        GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f); GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f);
        // Left
        GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c); GL11.glVertex3d(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f);
        GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f); GL11.glVertex3d(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c);
        // Right
        GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c); GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c);
        GL11.glVertex3d(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f); GL11.glVertex3d(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f);
        GL11.glEnd();
    }
}
