package com.lycanitesmobs.client;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.ItemManager;
import com.lycanitesmobs.core.item.special.ItemSoulgazer;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventListener {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onItemTooltip(ItemTooltipEvent event) {
        int sharpness = ItemManager.getInstance().getEquipmentSharpnessRepair(event.getItemStack());
        int mana = ItemManager.getInstance().getEquipmentManaRepair(event.getItemStack());
        if (sharpness > 0 || mana > 0) {
            event.getToolTip().add(new TranslatableComponent("equipment.item.repair").withStyle(ChatFormatting.BLUE));
            if (sharpness > 0) {
                event.getToolTip().add(new TranslatableComponent("equipment.sharpness").append(" " + sharpness).withStyle(ChatFormatting.BLUE));
            }
            if (mana > 0) {
                event.getToolTip().add(new TranslatableComponent("equipment.mana").append(" " + mana).withStyle(ChatFormatting.BLUE));
            }
        }

        if (event.getItemStack().getItem() instanceof ItemSoulgazer) {
            ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(event.getPlayer());
            if (extendedPlayer != null && extendedPlayer.creatureStudyCooldown > 0) {
                event.getToolTip().add(new TranslatableComponent("message.beastiary.study.cooldown").append(" " + String.format("%.0f", (float)extendedPlayer.creatureStudyCooldown / 20) + "s").withStyle(ChatFormatting.BLUE));
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        GameRenderer fogRenderer = event.getRenderer();
        LivingEntity entityLiving = Minecraft.getInstance().player;
        if (entityLiving == null) {
            return;
        }
        if(entityLiving.isInLava() && (!entityLiving.isOnFire() || entityLiving.hasEffect(MobEffects.FIRE_RESISTANCE))) {
            event.setDensity(0.5F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onBlockOverlay(RenderBlockOverlayEvent event) {
        if(event.getBlockForOverlay().getMaterial() == Material.FIRE && (!event.getPlayer().isOnFire() || event.getPlayer().hasEffect(MobEffects.FIRE_RESISTANCE))) {
            event.setCanceled(true);
        }
    }
}
