package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureType;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.localisation.LanguageManager;
import com.lycanitesmobs.core.pets.PetEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemSoulstone extends ItemBase {
	public CreatureType creatureType;


	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulstone(Item.Properties properties, @Nullable CreatureType creatureType) {
		super(properties);
		this.modInfo = LycanitesMobs.modInfo;
		this.creatureType = creatureType;
        this.itemName = "soulstone";
        if(creatureType != null) {
        	this.itemName += creatureType.getName();
		}
        this.setup();
    }


	// ==================================================
	//                       Use
	// ==================================================
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    	if(this.creatureType == null)
    		return super.onItemRightClick(world, player, hand);

		ItemStack itemStack = player.getHeldItem(hand);
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null) {
			return new ActionResult(ActionResultType.SUCCESS, itemStack);
		}

		// Get Random Creature from Type:
		int randomCreatureIndex = player.getRNG().nextInt(this.creatureType.tameableCreatures.size());
		CreatureInfo creatureInfo = this.creatureType.tameableCreatures.get(randomCreatureIndex);
		if(creatureInfo == null) {
			return new ActionResult(ActionResultType.SUCCESS, itemStack);
		}
		LivingEntity entity = creatureInfo.createEntity(world);

		if(entity != null) {
			entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
			world.func_217376_c(entity);
			if (!player.getEntityWorld().isRemote && entity instanceof EntityCreatureTameable) {
				((EntityCreatureTameable) entity).setPlayerOwner(player);
			}
			this.applySoulstoneToEntity(player, entity);
		}

		return new ActionResult(ActionResultType.SUCCESS, itemStack);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
    	if(this.creatureType == null && this.applySoulstoneToEntity(player, entity)) {
			// Consume Soulstone:
			if (!player.playerAbilities.isCreativeMode)
				stack.setCount(Math.max(0, stack.getCount() - 1));
			if (stack.getCount() <= 0)
				player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);

			return true;
		}

    	return super.itemInteractionForEntity(stack, player, entity, hand);
	}

	/**
	 * Applies this Soulstone to the provided entity.
	 * @param player The player using the Soulstone.
	 * @param entity The entity targeted by the Soulstone.
	 * @return True on success.
	 */
	public boolean applySoulstoneToEntity(PlayerEntity player, LivingEntity entity) {
		ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
		if(playerExt == null)
			return false;
		if(!(entity instanceof EntityCreatureTameable)) {
			if(!player.getEntityWorld().isRemote)
				player.sendMessage(new TranslationTextComponent(LanguageManager.translate("message.soulstone.invalid")));
			return false;
		}

		EntityCreatureTameable entityTameable = (EntityCreatureTameable)entity;
		CreatureInfo creatureInfo = entityTameable.creatureInfo;
		if(!creatureInfo.isTameable() || entityTameable.getOwner() != player) {
			if(!player.getEntityWorld().isRemote)
				player.sendMessage(new TranslationTextComponent(LanguageManager.translate("message.soulstone.untamed")));
			return false;
		}
		if(entityTameable.getPetEntry() != null) {
			if(!player.getEntityWorld().isRemote)
				player.sendMessage(new TranslationTextComponent(LanguageManager.translate("message.soulstone.exists")));
			return false;
		}

		// Particle Effect:
		if(player.getEntityWorld().isRemote) {
			for(int i = 0; i < 32; ++i) {
				entity.getEntityWorld().addParticle(ParticleTypes.HAPPY_VILLAGER,
						entity.posX + (4.0F * player.getRNG().nextFloat()) - 2.0F,
						entity.posY + (4.0F * player.getRNG().nextFloat()) - 2.0F,
						entity.posZ + (4.0F * player.getRNG().nextFloat()) - 2.0F,
						0.0D, 0.0D, 0.0D);
			}
		}

		// Store Pet:
		if(!player.getEntityWorld().isRemote) {
			String petType = "pet";
			if(entityTameable.creatureInfo.isMountable()) {
				petType = "mount";
			}

			String message = LanguageManager.translate("message.soulstone." + petType + ".added");
			message = message.replace("%creature%", creatureInfo.getTitle());
			player.sendMessage(new TranslationTextComponent(message));
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
