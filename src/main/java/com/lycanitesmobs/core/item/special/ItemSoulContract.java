package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.pets.PetEntry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ItemSoulContract extends ItemBase {

    public ItemSoulContract() {
		super();
		this.setMaxStackSize(1);
        this.itemName = "soul_contract";
        this.setup();
    }

	/** Returns the owner uuid. **/
	public UUID getOwnerUUID(ItemStack itemStack) {
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		UUID uuid = null;
		if(nbt.hasUniqueId("ownerUUID")) {
			uuid = nbt.getUniqueId("ownerUUID");
		}
		return uuid;
	}

	/** Returns the pet entry uuid. **/
	public UUID getPetEntryUUID(ItemStack itemStack) {
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		UUID uuid = null;
		if(nbt.hasUniqueId("petEntryUUID")) {
			uuid = nbt.getUniqueId("petEntryUUID");
		}
		return uuid;
	}

	@Override
	public void addInformation(ItemStack itemStack,  World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
		super.addInformation(itemStack, world, tooltip, tooltipFlag);

		UUID ownerUUID = this.getOwnerUUID(itemStack);
		UUID petEntryUUID = this.getPetEntryUUID(itemStack);
		if (ownerUUID != null && petEntryUUID != null) {
			EntityPlayer owner = world.getPlayerEntityByUUID(ownerUUID);
			ExtendedPlayer extendedOwner = ExtendedPlayer.getForPlayer(owner);
			if (owner == null || extendedOwner == null) {
				tooltip.add(LanguageManager.translate("item.lycanitesmobs.soul_contract.offline"));
				return;
			}
			PetEntry petEntry = extendedOwner.petManager.getEntry(petEntryUUID);
			if (petEntry == null) {
				tooltip.add(LanguageManager.translate("item.lycanitesmobs.soul_contract.released"));
				tooltip.add(LanguageManager.translate("item.lycanitesmobs.soul_contract.owner") + " " + owner.getDisplayName());
				return;
			}
			tooltip.add(LanguageManager.translate("item.lycanitesmobs.soul_contract.bound") + " " + petEntry.getDisplayName());
			tooltip.add(LanguageManager.translate("item.lycanitesmobs.soul_contract.owner") + " " + owner.getDisplayName());
		}
	}

	@Override
	public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
		// Invalid Entity:
		if(!(entity instanceof BaseCreatureEntity) || !((BaseCreatureEntity)entity).isBoundPet()) {
			if (!player.getEntityWorld().isRemote) {
				player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.invalid")));
			}
			return false;
		}

		BaseCreatureEntity creatureEntity = (BaseCreatureEntity)entity;
    	UUID ownerUUID = this.getOwnerUUID(itemStack);
    	UUID petEntryUUID = this.getPetEntryUUID(itemStack);

		if (ownerUUID == null || petEntryUUID == null) {
			return this.bindSoul(itemStack, player, creatureEntity);
		}

		return this.transferSoul(itemStack, player, creatureEntity, ownerUUID, petEntryUUID);
	}

	/**
	 * Binds the provided pet uuid to this soul contract.
	 * @param itemStack The Soul Contract itemstack.
	 * @param player The player using the Soul Contract to transfer from.
	 * @param creatureEntity The entity targeted by the Soul Contract.
	 * @return Pass on failure otherwise success.
	 */
	public boolean bindSoul(ItemStack itemStack, EntityPlayer player, BaseCreatureEntity creatureEntity) {

		// Check Ownership:
		if(creatureEntity.getOwner() != player || "familiar".equalsIgnoreCase(creatureEntity.getPetEntry().getType())) {
			if (!player.getEntityWorld().isRemote) {
				player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.not_owner")));
			}
			return false;
		}

		// Bind Soul:
		if (!player.getEntityWorld().isRemote) {
			NBTTagCompound nbt = this.getTagCompound(itemStack);
			nbt.setUniqueId("ownerUUID", player.getUniqueID());
			nbt.setUniqueId("petEntryUUID", creatureEntity.getPetEntry().petEntryID);
			itemStack.setTagCompound(nbt);
			player.inventory.setInventorySlotContents(player.inventory.currentItem, itemStack);
			player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.bound")));
		}

		return true;
	}

	/**
	 * Transfers the bound pet to a new owner.
	 * @param itemStack The Soul Contract itemstack.
	 * @param player The player using the Soul Contract to transfer to.
	 * @param creatureEntity The entity targeted by the Soul Contract to check for a match.
	 * @param ownerUUID The unique id of the pet owner.
	 * @param petEntryUUID The unique id of the pet entry to transfer.
	 * @return Pass on failure otherwise success.
	 */
	public boolean transferSoul(ItemStack itemStack, EntityPlayer player, BaseCreatureEntity creatureEntity, UUID ownerUUID, UUID petEntryUUID) {
		// Transferring to Self:
		if (player.getUniqueID().equals(ownerUUID)) {
			// Unbind from Contract:
			if (petEntryUUID.equals(creatureEntity.getPetEntry().petEntryID)) {
				if (!player.getEntityWorld().isRemote) {
					NBTTagCompound nbt = this.getTagCompound(itemStack);
					nbt.removeTag("ownerUUID");
					nbt.removeTag("petEntryUUID");
					itemStack.setTagCompound(nbt);
					player.inventory.setInventorySlotContents(player.inventory.currentItem, itemStack);
					player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.unbound")));
				}
				return true;
			}

			// Bind a different Creature to the Contract instead:
			return this.bindSoul(itemStack, player, creatureEntity);
		}

		// Transfer on Wrong Pet:
		if (!petEntryUUID.equals(creatureEntity.getPetEntry().petEntryID)) {
			if (!player.getEntityWorld().isRemote) {
				player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.wrong_target")));
			}
			return false;
		}

		// Get Owner:
		EntityPlayer owner = player.getEntityWorld().getPlayerEntityByUUID(ownerUUID);
		ExtendedPlayer extendedOwner = ExtendedPlayer.getForPlayer(owner);
		if (owner == null || extendedOwner == null) {
			if (!player.getEntityWorld().isRemote) {
				player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.owner_missing")));
			}
			return false;
		}

		// Get Receiver:
		ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(player);
		if (extendedPlayer == null) {
			return false;
		}

		// Transfer to New Owner:
		if (!player.getEntityWorld().isRemote) {
			extendedOwner.petManager.removeEntry(creatureEntity.getPetEntry());
			extendedPlayer.petManager.addEntry(creatureEntity.getPetEntry());
			extendedOwner.sendPetEntriesToPlayer(creatureEntity.getPetEntry().getType());
			extendedOwner.sendPetEntryRemoveToPlayer(creatureEntity.getPetEntry());
			extendedPlayer.sendPetEntriesToPlayer(creatureEntity.getPetEntry().getType());
			owner.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.transferred")));
			player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.transferred")));
		}
		if (creatureEntity instanceof TameableCreatureEntity) {
			((TameableCreatureEntity) creatureEntity).setPlayerOwner(player);
		}
		player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);

		return true;
	}

	@Override
	public boolean hasEffect(ItemStack itemStack) {
		return this.getOwnerUUID(itemStack) != null && this.getPetEntryUUID(itemStack) != null;
	}
}
