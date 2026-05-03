package dev.windv.wvc.module.visual;

import dev.windv.wvc.module.WVCModule;
import dev.windv.wvc.settings.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.List;

/**
 * Super Natural Motion Blur (True PolyBlur Engine)
 * マインクラフトの標準シェーダーエンジンと独自のカスタムシェーダーを利用し、
 * バグやちらつきを完全に排除した「究極のモーションブラー」です。
 */
public class MotionBlurModule extends WVCModule {

    private final SliderSetting blurAmount;
    private ShaderGroup blurShader;
    private int lastWidth, lastHeight;

    public MotionBlurModule(boolean enabled) {
        super("Motion Blur", enabled);
        // デフォルトをPolyBlurに近い適度な滑らかさに設定
        this.addSetting(blurAmount = new SliderSetting("Amount", 0.6, 0.1, 0.9, false));
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        // UIが描画される「直前」にフックすることで、UIを汚さずワールドだけにブラーをかける
        if (!isEnabled() || event.type != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null || !net.minecraft.client.renderer.OpenGlHelper.shadersSupported) return;

        // --- 1. シェーダーエンジンの初期化 ---
        if (blurShader == null) {
            try {
                // minecraftの標準リソースパス（assets/minecraft）からシェーダーを読み込む
                blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), new ResourceLocation("shaders/post/motion_blur.json"));
                blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
                lastWidth = mc.displayWidth;
                lastHeight = mc.displayHeight;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        // --- 2. 画面サイズ変更への対応 ---
        if (mc.displayWidth != lastWidth || mc.displayHeight != lastHeight) {
            blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            lastWidth = mc.displayWidth;
            lastHeight = mc.displayHeight;
        }

        // --- 3. ブラー強度の動的反映 ---
        float amount = (float) blurAmount.getValue();
        // Weight: 0.1 なら前フレームが90%残る(重いブラー)、1.0 なら前フレームが残らない
        float weight = 1.0f - amount; 
        weight = Math.max(0.1f, Math.min(1.0f, weight)); // 安全装置

        updateShaderIntensity(weight);

        // --- 4. シェーダーの実行 (PolyBlur完全再現) ---
        // テクスチャ行列を初期化（シェーダー実行前の儀式）
        GlStateManager.matrixMode(5890); // GL_TEXTURE
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        
        // シェーダーを実行し、現在の画面を加工して Framebuffer に書き戻す
        blurShader.loadShaderGroup(event.partialTicks);
        
        GlStateManager.popMatrix();

        // マイクラのメイン Framebuffer を再びアクティブにし、この後の UI 描画に備える
        mc.getFramebuffer().bindFramebuffer(true);
    }

    private void updateShaderIntensity(float weight) {
        if (blurShader == null) return;
        try {
            // Forge 1.8.9のリフレクションを使用し、ShaderGroup内のシェーダーリストへアクセス
            List<Shader> listShaders = ReflectionHelper.getPrivateValue(ShaderGroup.class, blurShader, "listShaders", "field_148031_d");
            
            for (Shader shader : listShaders) {
                ShaderUniform su = shader.getShaderManager().getShaderUniform("Weight");
                if (su != null) {
                    su.set(weight);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (blurShader != null) {
            blurShader.deleteShaderGroup();
            blurShader = null;
        }
    }
}
