package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.item.BaseItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSoulgazer extends BaseItem {

    public ItemSoulgazer(Item.Properties properties) {
        super(properties);
		this.itemName = "soulgazer";
		this.setup();
    }

	@Override
	public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
    	ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
    	if(extendedPlayer == null || extendedPlayer.creatureStudyCooldown > 0) {
			if (!player.getCommandSenderWorld().isClientSide) {
				player.sendMessage(new TranslationTextComponent("message.beastiary.study.recharging"), Util.NIL_UUID);
			}
			return ActionResultType.FAIL;
		}

    	extendedPlayer.studyCreature(entity, 25);
		if (!player.getCommandSenderWorld().isClientSide) {
			player.sendMessage(new TranslationTextComponent("message.beastiary.study"), Util.NIL_UUID);
		}

		if(player.getCommandSenderWorld().isClientSide) {
			for(int i = 0; i < 32; ++i) {
				entity.getCommandSenderWorld().addParticle(ParticleTypes.HAPPY_VILLAGER,
						entity.position().x() + (4.0F * player.getRandom().nextFloat()) - 2.0F,
						entity.position().y() + (4.0F * player.getRandom().nextFloat()) - 2.0F,
						entity.position().z() + (4.0F * player.getRandom().nextFloat()) - 2.0F,
						0.0D, 0.0D, 0.0D);
			}
		}

    	return ActionResultType.SUCCESS;
    }

    @Override
	public boolean hasContainerItem(ItemStack itemStack) {
    	return true;
	}

    @Override
	public ItemStack getContainerItem(ItemStack itemStack) {
    	return new ItemStack(this, 1);
	}

	@Override
	public boolean showDurabilityBar(ItemStack itemStack) {
		if (LycanitesMobs.PROXY.getClientPlayer() != null) {
			ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(LycanitesMobs.PROXY.getClientPlayer());
			if (extendedPlayer != null) {
				return extendedPlayer.creatureStudyCooldown > 0;
			}
		}
		return false;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack itemStack) {
		if (LycanitesMobs.PROXY.getClientPlayer() != null) {
			ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(LycanitesMobs.PROXY.getClientPlayer());
			if (extendedPlayer != null) {
				return ((double)extendedPlayer.creatureStudyCooldown / extendedPlayer.creatureStudyCooldownMax);
			}
		}
		return 0;
	}
}
