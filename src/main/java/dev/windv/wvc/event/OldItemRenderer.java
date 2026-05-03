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
    public void renderItemInFirstPerson(float partialTicks) {
        OldAnimationsModule mod = (OldAnimationsModule) WVCMod.INSTANCE.getModuleManager().getModule("OldAnimations");
        if (mod == null || !mod.isEnabled()) {
            super.renderItemInFirstPerson(partialTicks);
            return;
        }

        AbstractClientPlayer player = mc.thePlayer;
        ItemStack stack = (player != null) ? player.getHeldItem() : null;

        if (stack == null || (stack.getItem() instanceof ItemMap)) {
            super.renderItemInFirstPerson(partialTicks);
            return;
        }

        EnumAction action = stack.getItemUseAction();
        float swingProgress = player.getSwingProgress(partialTicks);

        // --- アイテム使用中の描画 ---
        if (player.isUsingItem() && player.getItemInUseCount() > 0) {
            if ((action == EnumAction.EAT || action == EnumAction.DRINK) && mod.isOldEating()) {
                // 食事・飲みはスイング変形を適用（1.7スタイル）
                GlStateManager.pushMatrix();
                applySwingTransform(swingProgress);
                super.renderItemInFirstPerson(partialTicks);
                GlStateManager.popMatrix();
                return;
            } else if (action == EnumAction.BOW) {
                // 弓は引き絞り中のスイングアニメーションが存在しない
                // applySwingTransform を適用すると震えるためバニラに委ねる
                super.renderItemInFirstPerson(partialTicks);
                return;
            } else if (action == EnumAction.BLOCK && mod.isOldBlock()) {
                // 剣のガード（1.7 Block-Hit）
                render17BlockHit(player, stack, swingProgress);
                return;
            }
        }

        super.renderItemInFirstPerson(partialTicks);
    }

    /**
     * 1.7スタイルのスイング（腕振り）変形を適用
     */
    private void applySwingTransform(float swingProgress) {
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
        
        // 殴っている感を強調するためのパラメータ
        GlStateManager.translate(0.0F, f1 * 0.1F, f1 * -0.1F);
        GlStateManager.rotate(f * -30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -40.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -110.0F, 1.0F, 0.0F, 0.0F);
    }

    /**
     * 剣ガード（1.7 Block-Hit）専用描画
     */
    private void render17BlockHit(AbstractClientPlayer player, ItemStack stack, float swingProgress) {
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        
        // 1.7 Block-Hit: リクィップなし
        try {
            if (transformMethod != null) {
                transformMethod.invoke(this, 0.0F, swingProgress);
            }
        } catch (Exception e) {}

        // 1.7のガードポーズ角度と位置
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);

        // 強調スイングをガードにも乗せる
        applySwingTransform(swingProgress);

        this.renderItem(player, stack, ItemCameraTransforms.TransformType.FIRST_PERSON);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}
