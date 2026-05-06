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
        super(renderManager, Minecraft.func_71410_x().func_175599_af());
        this.itemRendererLocal = Minecraft.func_71410_x().func_175599_af();
    }

    @Override
    public void func_76986_a(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!WVCMod.INSTANCE.getModuleManager().getModule("ItemPhysics").isEnabled()) {
            super.func_76986_a(entity, x, y, z, entityYaw, partialTicks);
            return;
        }

        ItemStack itemstack = entity.func_92059_d();
        if (itemstack == null || itemstack.func_77973_b() == null) return;

        this.func_110776_a(TextureMap.field_110575_b);

        GlStateManager.func_179091_B();
        GlStateManager.func_179092_a(516, 0.1F);
        GlStateManager.func_179147_l();
        GlStateManager.func_179120_a(770, 771, 1, 0);
        GlStateManager.func_179094_E();

        IBakedModel ibakedmodel = this.itemRendererLocal.func_175037_a().func_178089_a(itemstack);
        
        // func_177077_a の代わりに、スタックサイズに基づく描画個数を計算
        int i = 1;
        if (itemstack.field_77994_a > 48) i = 5;
        else if (itemstack.field_77994_a > 32) i = 4;
        else if (itemstack.field_77994_a > 16) i = 3;
        else if (itemstack.field_77994_a > 1) i = 2;

        GlStateManager.func_179109_b((float)x, (float)y + 0.03F, (float)z);
        
        float rotation = ((float)entity.func_174872_o() + partialTicks) * 2.0F;
        if (!entity.field_70122_E) {
            GlStateManager.func_179114_b(rotation, 0.1f, 1.0f, 0.1f);
        } else {
            GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
        }

        for (int j = 0; j < i; ++j) {
            GlStateManager.func_179094_E();
            if (j > 0) {
                // 重なりを表現
                GlStateManager.func_179109_b(0, 0, -0.01f * j);
            }
            GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
            this.itemRendererLocal.func_180454_a(itemstack, ibakedmodel);
            GlStateManager.func_179121_F();
        }

        GlStateManager.func_179121_F();
        GlStateManager.func_179101_C();
        GlStateManager.func_179084_k();
    }
}
