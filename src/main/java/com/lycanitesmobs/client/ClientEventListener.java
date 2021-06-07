package com.lycanitesmobs.client;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientEventListener {
    public static int ITEM_RENDER_MODE = 0; // Another hack lol. 0 = Fresh render. 1 = Rendering gui overlays. 2 = Rendering player hands or in world.

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderHand(RenderHandEvent event) {
        ITEM_RENDER_MODE = 1;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderHand(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            ITEM_RENDER_MODE = 2;
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        ITEM_RENDER_MODE = 1;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if(!(event.getEntity() instanceof EntityLivingBase))
            return;
        EntityLivingBase entityLiving = (EntityLivingBase)event.getEntity();
        if(event.getState().getMaterial() == Material.LAVA && (!event.getEntity().isBurning() || entityLiving.isPotionActive(MobEffects.FIRE_RESISTANCE))) {
            event.setDensity(0.5F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onBlockOverlay(RenderBlockOverlayEvent event) {
        if(event.getBlockForOverlay().getMaterial() == Material.FIRE && (!event.getPlayer().isBurning() || event.getPlayer().isPotionActive(MobEffects.FIRE_RESISTANCE))) {
            event.setCanceled(true);
        }
    }
}
