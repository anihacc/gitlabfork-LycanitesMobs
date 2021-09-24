package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSoulgazer extends ItemBase {

    public ItemSoulgazer() {
        super();
        this.setMaxStackSize(1);
        this.itemName = "soulgazer";
        this.setup();
        this.setContainerItem(this); // Infinite use in the crafting grid.
    }

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
		super.onUpdate(itemStack, world, entity, par4, par5);
	}

	@Override
    public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null || playerExt.creatureStudyCooldown > 0) {
			if (!player.getEntityWorld().isRemote) {
				player.sendMessage(new TextComponentString(LanguageManager.translate("message.beastiary.study.recharging")));
			}
			return false;
		}

		playerExt.studyCreature(entity, CreatureManager.getInstance().config.creatureStudyKnowledge);
		if (!player.getEntityWorld().isRemote) {
			player.sendMessage(new TextComponentString(LanguageManager.translate("message.beastiary.study")));
		}

		if(player.getEntityWorld().isRemote) {
			for(int i = 0; i < 32; ++i) {
				entity.getEntityWorld().spawnParticle(EnumParticleTypes.VILLAGER_HAPPY,
						entity.getPosition().getX() + (4.0F * player.getRNG().nextFloat()) - 2.0F,
						entity.getPosition().getY() + (4.0F * player.getRNG().nextFloat()) - 2.0F,
						entity.getPosition().getZ() + (4.0F * player.getRNG().nextFloat()) - 2.0F,
						0.0D, 0.0D, 0.0D);
			}
		}

    	return true;
    }

	@Override
	public boolean showDurabilityBar(ItemStack itemStack) {
    	if (LycanitesMobs.proxy.getClientPlayer() != null) {
    		ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(LycanitesMobs.proxy.getClientPlayer());
    		if (extendedPlayer != null) {
    			return extendedPlayer.creatureStudyCooldown > 0;
			}
		}
		return false;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack itemStack) {
		if (LycanitesMobs.proxy.getClientPlayer() != null) {
			ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(LycanitesMobs.proxy.getClientPlayer());
			if (extendedPlayer != null) {
				return ((double)extendedPlayer.creatureStudyCooldown / extendedPlayer.creatureStudyCooldownMax);
			}
		}
		return 0;
	}
}
