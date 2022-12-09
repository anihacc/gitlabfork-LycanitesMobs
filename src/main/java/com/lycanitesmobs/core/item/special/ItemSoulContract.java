package com.lycanitesmobs.core.item.special;

import com.lycanitesmobs.client.localisation.LanguageManager;
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
			tooltip.add(LanguageManager.translate("item.lycanitesmobs.soul_contract.owner") + " " + owner.getDisplayName().getFormattedText());
		}
	}

	@Override
	public boolean onItemRightClickOnEntity(EntityPlayer player, Entity entity, ItemStack itemStack) {
		// Invalid Entity:
		if(!(entity instanceof TameableCreatureEntity) || !((TameableCreatureEntity)entity).isBoundPet()) {
			if (!player.getEntityWorld().isRemote) {
				player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.invalid")));
			}
			return false;
		}

		TameableCreatureEntity tameableCreatureEntity = (TameableCreatureEntity)entity;
    	UUID ownerUUID = this.getOwnerUUID(itemStack);
    	UUID petEntryUUID = this.getPetEntryUUID(itemStack);

		if (ownerUUID == null || petEntryUUID == null) {
			return this.bindSoul(itemStack, player, tameableCreatureEntity);
		}

		return this.transferSoul(itemStack, player, tameableCreatureEntity, ownerUUID, petEntryUUID);
	}

	/**
	 * Binds the provided pet uuid to this soul contract.
	 * @param itemStack The Soul Contract itemstack.
	 * @param player The player using the Soul Contract to transfer from.
	 * @param tameableCreatureEntity The entity targeted by the Soul Contract.
	 * @return Pass on failure otherwise success.
	 */
	public boolean bindSoul(ItemStack itemStack, EntityPlayer player, TameableCreatureEntity tameableCreatureEntity) {

		// Check Ownership:
		if(tameableCreatureEntity.getPlayerOwner() != player || "familiar".equalsIgnoreCase(tameableCreatureEntity.getPetEntry().getType())) {
			if (!player.getEntityWorld().isRemote) {
				player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.not_owner")));
			}
			return false;
		}

		// Bind Soul:
		if (!player.getEntityWorld().isRemote) {
			NBTTagCompound nbt = this.getTagCompound(itemStack);
			nbt.setUniqueId("ownerUUID", player.getUniqueID());
			nbt.setUniqueId("petEntryUUID", tameableCreatureEntity.getPetEntry().petEntryID);
			itemStack.setTagCompound(nbt);
			player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.bound")));
		}

		return true;
	}

	/**
	 * Transfers the bound pet to a new owner.
	 * @param itemStack The Soul Contract itemstack.
	 * @param player The player using the Soul Contract to transfer to.
	 * @param tameableCreatureEntity The entity targeted by the Soul Contract to check for a match.
	 * @param ownerUUID The unique id of the pet owner.
	 * @param petEntryUUID The unique id of the pet entry to transfer.
	 * @return Pass on failure otherwise success.
	 */
	public boolean transferSoul(ItemStack itemStack, EntityPlayer player, TameableCreatureEntity tameableCreatureEntity, UUID ownerUUID, UUID petEntryUUID) {
		// Transferring to Self:
		if (player.getUniqueID().equals(ownerUUID)) {
			// Unbind from Contract:
			if (petEntryUUID.equals(tameableCreatureEntity.getPetEntry().petEntryID)) {
				if (!player.getEntityWorld().isRemote) {
					NBTTagCompound nbt = this.getTagCompound(itemStack);
					//nbt.removeTag("ownerUUID");//Tags set as setUniqueId can not be directly removed, as they are actually <tag>Most and <tag>Least
					//nbt.removeTag("petEntryUUID");
					nbt.removeTag("ownerUUIDMost");
					nbt.removeTag("ownerUUIDLeast");
					nbt.removeTag("petEntryUUIDMost");
					nbt.removeTag("petEntryUUIDLeast");
					itemStack.setTagCompound(nbt);
					player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.unbound")));
				}
				return true;
			}

			// Bind a different Creature to the Contract instead:
			return this.bindSoul(itemStack, player, tameableCreatureEntity);
		}

		// Transfer on Wrong Pet:
		if (!petEntryUUID.equals(tameableCreatureEntity.getPetEntry().petEntryID)) {
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
			extendedOwner.petManager.removeEntry(tameableCreatureEntity.getPetEntry());
			extendedPlayer.petManager.addEntry(tameableCreatureEntity.getPetEntry());
			extendedOwner.sendPetEntriesToPlayer(tameableCreatureEntity.getPetEntry().getType());
			extendedOwner.sendPetEntryRemoveToPlayer(tameableCreatureEntity.getPetEntry());
			extendedPlayer.sendPetEntriesToPlayer(tameableCreatureEntity.getPetEntry().getType());
			owner.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.transferred")));
			player.sendMessage(new TextComponentString(LanguageManager.translate("message.soul_contract.transferred")));
		}
		tameableCreatureEntity.setPlayerOwner(player);
		itemStack.shrink(1);

		return true;
	}

	@Override
	public boolean hasEffect(ItemStack itemStack) {
		return this.getOwnerUUID(itemStack) != null && this.getPetEntryUUID(itemStack) != null;
	}
}
