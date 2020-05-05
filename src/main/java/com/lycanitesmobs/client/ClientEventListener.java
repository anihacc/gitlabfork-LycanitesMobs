package com.lycanitesmobs.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ClientEventListener {

    // ==================================================
    //                Client Fog Color
    // ==================================================
    @Environment(EnvType.CLIENT)
    public void onFogDensity() {
        /*FogRenderer fogRenderer = event.getFogRenderer();
        LivingEntity entityLiving = MinecraftClient.getInstance().player;
        if(event.getState().getMaterial() == Material.LAVA && (!event.getEntity().isBurning() || entityLiving.isPotionActive(StatusEffects.FIRE_RESISTANCE))) {
            event.setDensity(0.5F);
            event.setCanceled(true);
        }*/
    }


    // ==================================================
    //                First Person Fire
    // ==================================================
    @Environment(EnvType.CLIENT)
    public void onBlockOverlay() {
//        if(event.getBlockForOverlay().getMaterial() == Material.FIRE && (!event.getPlayer().isBurning() || event.getPlayer().isPotionActive(StatusEffects.FIRE_RESISTANCE))) {
//            event.setCanceled(true);
//        }
    }
}
