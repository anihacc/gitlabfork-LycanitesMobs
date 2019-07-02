package com.lycanitesmobs;

import net.minecraft.block.material.Material;
import net.minecraft.potion.Effects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventListener {

    // ==================================================
    //                Client Fog Color
    // ==================================================
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        /*FogRenderer fogRenderer = event.getFogRenderer();
        LivingEntity entityLiving = Minecraft.getInstance().player;
        if(event.getState().getMaterial() == Material.LAVA && (!event.getEntity().isBurning() || entityLiving.isPotionActive(Effects.FIRE_RESISTANCE))) {
            event.setDensity(0.5F);
            event.setCanceled(true);
        }*/
    }


    // ==================================================
    //                First Person Fire
    // ==================================================
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onBlockOverlay(RenderBlockOverlayEvent event) {
        if(event.getBlockForOverlay().getMaterial() == Material.FIRE && (!event.getPlayer().isBurning() || event.getPlayer().isPotionActive(Effects.FIRE_RESISTANCE))) {
            event.setCanceled(true);
        }
    }
}
