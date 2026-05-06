package dev.windv.wvc.render;

import dev.windv.wvc.WVCMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * ItemPhysics レンダラー
 * アイテムのドロップ描写に物理演算のような回転と接地感を与えます。
 */
public class ItemPhysicsRenderer extends RenderEntityItem {

    private final Random random = new Random();

    private final net.minecraft.client.renderer.entity.RenderItem itemRendererLocal;

    public ItemPhysicsRenderer(RenderManager renderManager) {
        super(renderManager, Minecraft.getMinecraft().getRenderItem());
        this.itemRendererLocal = Minecraft.getMinecraft().getRenderItem();
    }

    @Override
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!WVCMod.INSTANCE.getModuleManager().getModule("ItemPhysics").isEnabled()) {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
            return;
        }

        ItemStack itemstack = entity.getEntityItem();
        if (itemstack == null || itemstack.getItem() == null) return;

        this.bindTexture(TextureMap.locationBlocksTexture);

        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();

        IBakedModel ibakedmodel = this.itemRendererLocal.getItemModelMesher().getItemModel(itemstack);
        
        // func_177077_a の代わりに、スタックサイズに基づく描画個数を計算
        int i = 1;
        if (itemstack.stackSize > 48) i = 5;
        else if (itemstack.stackSize > 32) i = 4;
        else if (itemstack.stackSize > 16) i = 3;
        else if (itemstack.stackSize > 1) i = 2;

        GlStateManager.translate((float)x, (float)y + 0.03F, (float)z);
        
        float rotation = ((float)entity.getAge() + partialTicks) * 2.0F;
        if (!entity.onGround) {
            GlStateManager.rotate(rotation, 0.1f, 1.0f, 0.1f);
        } else {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        }

        for (int j = 0; j < i; ++j) {
            GlStateManager.pushMatrix();
            if (j > 0) {
                // 重なりを表現
                GlStateManager.translate(0, 0, -0.01f * j);
            }
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            this.itemRendererLocal.renderItem(itemstack, ibakedmodel);
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
    }
}
