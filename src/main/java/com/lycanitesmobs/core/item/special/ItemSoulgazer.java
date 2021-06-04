package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.item.BaseItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public class ItemSoulgazer extends BaseItem {

    public ItemSoulgazer(Item.Properties properties) {
        super(properties);
		this.itemName = "soulgazer";
		this.setup();
    }

	@Override
	public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
    	ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
    	if(playerExt == null || playerExt.creatureStudyCooldown > 0) {
			return ActionResultType.FAIL;
		}
    	playerExt.studyCreature(entity, 25);

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
}
