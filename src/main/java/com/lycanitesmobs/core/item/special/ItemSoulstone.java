package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.localisation.LanguageManager;
import com.lycanitesmobs.core.pets.PetEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ItemSoulstone extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSoulstone(Item.Properties properties, ModInfo group, String type) {
		super(properties);
        this.itemName = "soulstone" + type;
		this.modInfo = group;
        this.setup();
    }
    
    
	// ==================================================
	//                       Use
	// ==================================================
	// ========== Entity Interaction ==========
    @Override
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
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

			// Consume Soulstone:
			if (!player.playerAbilities.isCreativeMode)
				stack.setCount(Math.max(0, stack.getCount() - 1));
			if (stack.getCount() <= 0)
				player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
		}

    	return true;
    }
}
