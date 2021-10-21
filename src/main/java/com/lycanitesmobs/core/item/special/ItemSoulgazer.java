package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.item.BaseItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;

public class ItemSoulgazer extends BaseItem {

    public ItemSoulgazer(Item.Properties properties) {
        super(properties);
		this.itemName = "soulgazer";
		this.setup();
    }

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
    	ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
    	if(extendedPlayer == null) {
			return InteractionResult.FAIL;
		}

    	int amount = CreatureManager.getInstance().config.creatureStudyKnowledge;
    	if (!extendedPlayer.studyCreature(entity, amount, true)) {
    		return InteractionResult.FAIL;
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

    	return InteractionResult.SUCCESS;
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
