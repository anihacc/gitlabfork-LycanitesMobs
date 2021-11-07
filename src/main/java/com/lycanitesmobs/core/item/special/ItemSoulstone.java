package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureType;
import com.lycanitesmobs.core.item.CreatureTypeItem;
import com.lycanitesmobs.core.pets.PetEntry;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ItemSoulstone extends CreatureTypeItem {
    public ItemSoulstone(Item.Properties properties, @Nullable CreatureType creatureType) {
		super(properties, "soulstone", creatureType);
        if(creatureType != null) {
        	this.itemName += creatureType.getName();
		}
        this.setup();
    }

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    	return super.use(world, player, hand);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
    	if(this.applySoulstoneToEntity(player, entity)) {
			// Consume Soulstone:
			if (!player.getAbilities().instabuild)
				stack.setCount(Math.max(0, stack.getCount() - 1));
			if (stack.getCount() <= 0)
				player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);

			return InteractionResult.SUCCESS;
		}

    	return super.interactLivingEntity(stack, player, entity, hand);
	}

	/**
	 * Applies this Soulstone to the provided entity.
	 * @param player The player using the Soulstone.
	 * @param entity The entity targeted by the Soulstone.
	 * @return True on success.
	 */
	public boolean applySoulstoneToEntity(Player player, LivingEntity entity) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return false;
		if(!(entity instanceof TameableCreatureEntity)) {
			if(!player.getCommandSenderWorld().isClientSide)
				player.sendMessage(new TranslatableComponent("message.soulstone.invalid"), Util.NIL_UUID);
			return false;
		}

		TameableCreatureEntity entityTameable = (TameableCreatureEntity)entity;
		CreatureInfo creatureInfo = entityTameable.creatureInfo;
		if(!creatureInfo.isTameable() || entityTameable.getOwner() != player) {
			if(!player.getCommandSenderWorld().isClientSide)
				player.sendMessage(new TranslatableComponent("message.soulstone.untamed"), Util.NIL_UUID);
			return false;
		}
		if(entityTameable.getPetEntry() != null) {
			if(!player.getCommandSenderWorld().isClientSide)
				player.sendMessage(new TranslatableComponent("message.soulstone.exists"), Util.NIL_UUID);
			return false;
		}

		// Particle Effect:
		if(player.getCommandSenderWorld().isClientSide) {
			for(int i = 0; i < 32; ++i) {
				entity.getCommandSenderWorld().addParticle(ParticleTypes.HAPPY_VILLAGER,
						entity.position().x() + (4.0F * player.getRandom().nextFloat()) - 2.0F,
						entity.position().y() + (4.0F * player.getRandom().nextFloat()) - 2.0F,
						entity.position().z() + (4.0F * player.getRandom().nextFloat()) - 2.0F,
						0.0D, 0.0D, 0.0D);
			}
		}

		// Store Pet:
		if(!player.getCommandSenderWorld().isClientSide) {
			String petType = "pet";
			if(entityTameable.creatureInfo.isMountable()) {
				petType = "mount";
			}

			Component message = new TranslatableComponent("message.soulstone." + petType + ".added.prefix")
					.append(" ")
					.append(creatureInfo.getTitle())
					.append(" ")
					.append(new TranslatableComponent("message.soulstone." + petType + ".added.suffix"));
			player.sendMessage(message, Util.NIL_UUID);
			//player.addStat(ObjectManager.getStat("soulstone"), 1);

			// Add Pet Entry:
			PetEntry petEntry = PetEntry.createFromEntity(player, entityTameable, petType);
			playerExt.petManager.addEntry(petEntry);
			playerExt.sendPetEntriesToPlayer(petType);
			petEntry.assignEntity(entity);
			entityTameable.setPetEntry(petEntry);
		}

		return true;
	}
}
