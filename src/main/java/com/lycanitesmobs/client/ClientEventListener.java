package com.lycanitesmobs.client;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventListener {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        GameRenderer fogRenderer = event.getRenderer();
        LivingEntity entityLiving = Minecraft.getInstance().player;
        if (entityLiving == null) {
            return;
        }
        if(entityLiving.isInLava() && (!entityLiving.isOnFire() || entityLiving.hasEffect(Effects.FIRE_RESISTANCE))) {
            event.setDensity(0.5F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onBlockOverlay(RenderBlockOverlayEvent event) {
        if(event.getBlockForOverlay().getMaterial() == Material.FIRE && (!event.getPlayer().isOnFire() || event.getPlayer().hasEffect(Effects.FIRE_RESISTANCE))) {
            event.setCanceled(true);
        }
    }
}
