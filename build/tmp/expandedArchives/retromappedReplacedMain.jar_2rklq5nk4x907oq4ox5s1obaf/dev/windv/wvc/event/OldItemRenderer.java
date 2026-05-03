package dev.windv.wvc.event;

import dev.windv.wvc.WVCMod;
import dev.windv.wvc.module.visual.OldAnimationsModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 究極のOldItemRenderer - ラッパー・スイング合成版
 */
public class OldItemRenderer extends ItemRenderer {

    private final Minecraft mc;
    private static Method transformMethod;
    private static boolean fieldsInitialized = false;

    public OldItemRenderer(Minecraft mc, ItemRenderer original) {
        super(mc);
        this.mc = mc;
        initFields();
    }

    private static void initFields() {
        if (fieldsInitialized) return;
        try {
            // privateメソッドをリフレクションで取得（剣のガード描画用）
            transformMethod = ReflectionHelper.findMethod(ItemRenderer.class, null, 
                new String[]{"transformFirstPersonItem", "func_178096_b"}, 
                float.class, float.class);
            fieldsInitialized = true;
        } catch (Exception e) {}
    }

    @Override
    public void func_78440_a(float partialTicks) {
        OldAnimationsModule mod = (OldAnimationsModule) WVCMod.INSTANCE.getModuleManager().getModule("OldAnimations");
        if (mod == null || !mod.isEnabled()) {
            super.func_78440_a(partialTicks);
            return;
        }

        AbstractClientPlayer player = mc.field_71439_g;
        ItemStack stack = (player != null) ? player.func_70694_bm() : null;

        if (stack == null || (stack.func_77973_b() instanceof ItemMap)) {
            super.func_78440_a(partialTicks);
            return;
        }

        EnumAction action = stack.func_77975_n();
        float swingProgress = player.func_70678_g(partialTicks);

        // --- アイテム使用中の描画 ---
        if (player.func_71039_bw() && player.func_71052_bv() > 0) {
            if ((action == EnumAction.EAT || action == EnumAction.DRINK) && mod.isOldEating()) {
                // 食事・飲みはスイング変形を適用（1.7スタイル）
                GlStateManager.func_179094_E();
                applySwingTransform(swingProgress);
                super.func_78440_a(partialTicks);
                GlStateManager.func_179121_F();
                return;
            } else if (action == EnumAction.BOW) {
                // 弓は引き絞り中のスイングアニメーションが存在しない
                // applySwingTransform を適用すると震えるためバニラに委ねる
                super.func_78440_a(partialTicks);
                return;
            } else if (action == EnumAction.BLOCK && mod.isOldBlock()) {
                // 剣のガード（1.7 Block-Hit）
                render17BlockHit(player, stack, swingProgress);
                return;
            }
        }

        super.func_78440_a(partialTicks);
    }

    /**
     * 1.7スタイルのスイング（腕振り）変形を適用
     */
    private void applySwingTransform(float swingProgress) {
        float f = MathHelper.func_76126_a(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.func_76126_a(MathHelper.func_76129_c(swingProgress) * (float)Math.PI);
        
        // 殴っている感を強調するためのパラメータ
        GlStateManager.func_179109_b(0.0F, f1 * 0.1F, f1 * -0.1F);
        GlStateManager.func_179114_b(f * -30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.func_179114_b(f1 * -40.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.func_179114_b(f1 * -110.0F, 1.0F, 0.0F, 0.0F);
    }

    /**
     * 剣ガード（1.7 Block-Hit）専用描画
     */
    private void render17BlockHit(AbstractClientPlayer player, ItemStack stack, float swingProgress) {
        GlStateManager.func_179094_E();
        RenderHelper.func_74519_b();
        
        // 1.7 Block-Hit: リクィップなし
        try {
            if (transformMethod != null) {
                transformMethod.invoke(this, 0.0F, swingProgress);
            }
        } catch (Exception e) {}

        // 1.7のガードポーズ角度と位置
        GlStateManager.func_179109_b(-0.5F, 0.2F, 0.0F);
        GlStateManager.func_179114_b(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.func_179114_b(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.func_179114_b(60.0F, 0.0F, 1.0F, 0.0F);

        // 強調スイングをガードにも乗せる
        applySwingTransform(swingProgress);

        this.func_178099_a(player, stack, ItemCameraTransforms.TransformType.FIRST_PERSON);
        RenderHelper.func_74518_a();
        GlStateManager.func_179121_F();
    }
}
