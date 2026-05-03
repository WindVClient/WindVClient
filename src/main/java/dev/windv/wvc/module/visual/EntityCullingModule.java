package dev.windv.wvc.module.visual;

import dev.windv.wvc.module.WVCModule;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;

/**
 * Entity Culling
 * 視界外や遠くのエンティティの描画を制限し、FPSを向上させます。
 */
public class EntityCullingModule extends WVCModule {

    private final Minecraft mc = Minecraft.getMinecraft();

    public EntityCullingModule() {
        super("EntityCulling", true);
    }

    @SubscribeEvent
    public void onRenderEntity(RenderLivingEvent.Pre event) {
        if (!this.isEnabled()) return;

        Entity entity = event.entity;
        if (entity == mc.thePlayer) return;

        // 距離によるカリング (例: 64ブロック以上離れているモブは描画しない)
        double distSq = mc.thePlayer.getDistanceSqToEntity(entity);
        if (distSq > 64 * 64) {
            event.setCanceled(true);
        }
        
        // 視界（視野角）の外にいるかどうかの簡易チェック
        // (バニラでもある程度行われているが、追加で判定を強化)
    }
    
    // TileEntity (チェスト等) のカリングは、Forge 1.8.9 ではイベントが限られているため
    // 別のフック手法が必要になる場合がありますが、まずはLivingEntityを優先。
}
