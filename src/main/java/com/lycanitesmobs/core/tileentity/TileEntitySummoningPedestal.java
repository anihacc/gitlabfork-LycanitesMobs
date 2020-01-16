package com.lycanitesmobs.core.tileentity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockSummoningPedestal;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.PortalEntity;
import com.lycanitesmobs.core.network.MessageSummoningPedestalStats;
import com.lycanitesmobs.core.network.MessageSummoningPedestalSummonSet;
import com.lycanitesmobs.core.container.SummoningPedestalContainer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.client.gui.SummoningPedestalScreen;
import com.lycanitesmobs.core.pets.SummonSet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class TileEntitySummoningPedestal extends TileEntityBase implements IInventory {

	public long updateTick = 0;

	// Summoning Properties:
	public PortalEntity summoningPortal;
	public UUID ownerUUID;
	public String ownerName = "";
	public SummonSet summonSet;
	public int summonAmount = 1;

	// Summoning Stats:
	public int capacityCharge = 100;
	public int capacity = 0;
	public int capacityMax = (this.capacityCharge * 10);
	public int summonProgress = 0;
	public int summonProgressMax = 3 * 60;

	// Inventory:
	public String inventoryName = "";
	public NonNullList<ItemStack> itemStacks = NonNullList.withSize(3, ItemStack.EMPTY);
	public int summoningFuel = 0;
	public int summoningFuelMax = 10 * 60 * 20;
	public int summoningFuelAmount = 10 * 60 * 20; // 10 minutes per Redstone Dust

	// Summoned Minions:
	public List<BaseCreatureEntity> minions = new ArrayList<>();
	protected String[] loadMinionIDs; // Temporary array for initially populating from NBT data in update.

	// Block:
	protected boolean blockStateSet = false;


	public TileEntitySummoningPedestal() {
		super();
		this.summoningFuelMax = LycanitesMobs.config.getInt("Player", "Summoning Pedestal Redstone Time", this.summoningFuelMax, "How much summoning time (in ticks) 1 redstone dust provides. 20 ticks = 1 second, default is 12000 (10 minutes).");
	}


	// ========================================
	//                  Remove
	// ========================================
	/** Can be called by a block when broken to alert this TileEntity that it is being removed. **/
	@Override
	public void onRemove() {
		if(this.summoningPortal != null && !this.summoningPortal.isDead) {
			this.summoningPortal.setDead();
		}
	}


	// ========================================
	//                  Update
	// ========================================
	/** The main update called every tick. **/
	@Override
	public void update() {
		// Client Side Only:
		if(this.getWorld().isRemote) {

			// Summoning Progress Animation:
			if(this.summoningFuel > 0) {
				if (this.summonProgress >= this.summonProgressMax)
					this.summonProgress = 0;
				else if (this.summonProgress > 0)
					this.summonProgress++;
			}

			return;
		}

		// Load Minion IDs:
		if(this.loadMinionIDs != null) {
			int range = 20;
			List nearbyEntities = this.getWorld().getEntitiesWithinAABB(BaseCreatureEntity.class,
					new AxisAlignedBB(this.getPos().getX() - range, this.getPos().getY() - range, this.getPos().getZ() - range,
							this.getPos().getX() + range, this.getPos().getY() + range, this.getPos().getZ() + range));
			Iterator possibleEntities = nearbyEntities.iterator();
			while(possibleEntities.hasNext()) {
				BaseCreatureEntity possibleEntity = (BaseCreatureEntity)possibleEntities.next();
				for(String loadMinionID : this.loadMinionIDs) {
					UUID uuid = null;
					try { uuid = UUID.fromString(loadMinionID); } catch (Exception e) {}
					if(possibleEntity.getUniqueID().equals(uuid)) {
						this.minions.add(possibleEntity);
						break;
					}
				}
			}
			this.loadMinionIDs = null;
		}

		// Summoning:
		if(this.summonSet != null && this.summonSet.getCreatureInfo() != null) {
			if (this.summonSet.getFollowing()) {
				this.summonSet.following = false;
			}

			// Summoning Portal:
			if (this.summoningPortal == null || this.summoningPortal.isDead) {
				this.summoningPortal = new PortalEntity(this.getWorld(), this);
				this.summoningPortal.setProjectileScale(4);
				this.getWorld().spawnEntity(this.summoningPortal);
			}

			// Update Minions:
			if (this.updateTick % 100 == 0) {
				this.capacity = 0;
				for (BaseCreatureEntity minion : this.minions.toArray(new BaseCreatureEntity[this.minions.size()])) {
					if (minion == null || minion.isDead)
						this.minions.remove(minion);
					else {
						this.capacity += (minion.creatureInfo.summonCost * this.capacityCharge);
					}
				}
			}

			// Check Capacity:
			if (this.capacity + this.summonSet.getCreatureInfo().summonCost > this.capacityMax) {
				this.summonProgress = 0;
			}

			// Try To Summon:
			else {
				if(this.summoningFuel <= 0) {
					ItemStack fuelStack = this.getStackInSlot(0);
					if(!fuelStack.isEmpty()) {
						int refuel = this.summoningFuelAmount;
						if(fuelStack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)) {
							refuel = this.summoningFuelAmount * 9;
						}
						fuelStack.shrink(1);
						this.summoningFuel = refuel;
						this.summoningFuelMax = refuel;
					}
				}

				if (this.summoningFuel > 0) {
					this.summoningFuel--;

					// Summon Minions:
					if (this.summonProgress++ >= this.summonProgressMax) {
						this.summoningPortal.summonCreatures();
						this.summonProgress = 0;
						this.capacity = Math.min(this.capacity + (this.capacityCharge * this.summonSet.getCreatureInfo().summonCost), this.capacityMax);
					}
				}
			}
		}

		// Block State:
		if (!this.blockStateSet) {
			if (!"".equals(this.getOwnerName()))
				BlockSummoningPedestal.setState(BlockSummoningPedestal.EnumSummoningPedestal.PLAYER, this.getWorld(), this.getPos());
			else
				BlockSummoningPedestal.setState(BlockSummoningPedestal.EnumSummoningPedestal.NONE, this.getWorld(), this.getPos());
			this.blockStateSet = true;
		}

		// Sync To Client:
		if(this.updateTick % 20 == 0) {
			LycanitesMobs.packetHandler.sendToAllAround(new MessageSummoningPedestalStats(this.capacity, this.summonProgress, this.summoningFuel, this.summoningFuelMax, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()),
					new NetworkRegistry.TargetPoint(this.getWorld().provider.getDimension(), this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 5D));
		}

		this.updateTick++;
	}


	// ========================================
	//           Summoning Pedestal
	// ========================================
	/** Sets the owner of this block. **/
	public void setOwner(EntityLivingBase entity) {
		if(entity instanceof EntityPlayer) {
			EntityPlayer entityPlayer = (EntityPlayer)entity;
			this.ownerUUID = entityPlayer.getUniqueID();
			this.ownerName = entity.getName();
		}
	}

	/** Returns the name of the owner of this pedestal. **/
	@Nullable
	public UUID getOwnerUUID() {
		return this.ownerUUID;
	}

	/** Returns the name of the owner of this pedestal. **/
	public String getOwnerName() {
		return this.ownerName;
	}

	/** Returns the player that this belongs to or null if owned by no player. **/
	@Nullable
	public EntityPlayer getPlayer() {
		if(this.ownerUUID == null) {
			return null;
		}
		return this.getWorld().getPlayerEntityByUUID(this.ownerUUID);
	}

	/** Returns the class that this is summoning. **/
	@Nullable
	public Class getSummonClass() {
		if(this.summonSet == null) {
			return null;
		}
		return this.summonSet.getCreatureClass();
	}

	/** Sets the Summon Set for this to use. **/
	public void setSummonSet(SummonSet summonSet) {
		if(this.getPlayer() != null && !summonSet.isUseable()) {
			return;
		}
		this.summonSet = new SummonSet(null);
		this.summonSet.setSummonType(summonSet.summonType);
		this.summonSet.sitting = summonSet.getSitting();
		this.summonSet.following = false;
		this.summonSet.passive = summonSet.getPassive();
		this.summonSet.aggressive = summonSet.getAggressive();
		this.summonSet.pvp = summonSet.getPVP();
	}


	// ========== Minion Behaviour ==========
	/** Applies the minion behaviour to the summoned player owned minion. **/
	public void applyMinionBehaviour(TameableCreatureEntity minion) {
		if(this.summonSet != null) {
			this.summonSet.applyBehaviour(minion);
			minion.applySubspecies(this.summonSet.subspecies);
		}
		this.minions.add(minion);
		minion.setHome(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 20);
	}


	// ========================================
	//                Inventory
	// ========================================
	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.itemStacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the stack in the given slot.
	 */
	@Override
	public ItemStack getStackInSlot(int index) {
		return this.itemStacks.get(index);
	}

	/**
	 * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(this.itemStacks, index, count);
	}

	/**
	 * Removes a stack from the given slot and returns it.
	 */
	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(this.itemStacks, index);
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.itemStacks.set(index, stack);
		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
	}

	@Override
	public int getSizeInventory() {
		return this.itemStacks.size();
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
	 * guis use Slot.isItemValid
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack itemStack) {
		return itemStack.getItem() == Items.REDSTONE || itemStack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public String getName() {
		return this.inventoryName;
	}

	@Override
	public boolean hasCustomName() {
		return !"".equals(this.inventoryName);
	}


	// ========================================
	//              Client Events
	// ========================================
	@Override
	public boolean receiveClientEvent(int eventID, int eventArg) {
		return false;
	}


	// ========================================
	//             Network Packets
	// ========================================
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound syncData = new NBTTagCompound();

		// Both:
		if(this.summonSet != null) {
			NBTTagCompound summonSetNBT = new NBTTagCompound();
			this.summonSet.writeToNBT(summonSetNBT);
			syncData.setTag("SummonSet", summonSetNBT);
		}

		// Server to Client:
		if(!this.getWorld().isRemote && this.getOwnerUUID() != null && this.getOwnerName() != null) {
			syncData.setString("OwnerUUID", this.getOwnerUUID().toString());
			syncData.setString("OwnerName", this.getOwnerName());
			syncData.setString("InventoryName", this.getName());
		}

		return new SPacketUpdateTileEntity(this.getPos(), 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		if(!this.getWorld().isRemote)
			return;

		NBTTagCompound syncData = packet.getNbtCompound();
		if (syncData.hasKey("OwnerUUID"))
			this.ownerUUID = UUID.fromString(syncData.getString("OwnerUUID"));
		if (syncData.hasKey("OwnerName"))
			this.ownerName = syncData.getString("OwnerName");
		if (syncData.hasKey("InventoryName"))
			this.inventoryName = syncData.getString("InventoryName");
		if (syncData.hasKey("SummonSet")) {
			SummonSet summonSet = new SummonSet(null);
			summonSet.readFromNBT(syncData.getCompoundTag("SummonSet"));
			this.summonSet = summonSet;
		}
	}

	public void sendSummonSetToServer(SummonSet summonSet) {
		LycanitesMobs.packetHandler.sendToServer(new MessageSummoningPedestalSummonSet(summonSet, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ()));
	}


	// ========================================
	//                 NBT Data
	// ========================================
	/** Reads from saved NBT data. **/
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);

		if(nbtTagCompound.hasKey("OwnerUUID")) {
			String uuidString = nbtTagCompound.getString("OwnerUUID");
			if(!"".equals(uuidString))
				this.ownerUUID = UUID.fromString(uuidString);
			else
				this.ownerUUID = null;
		}
		else {
			this.ownerUUID = null;
		}

		if(nbtTagCompound.hasKey("OwnerName")) {
			this.ownerName = nbtTagCompound.getString("OwnerName");
		}
		else {
			this.ownerName = "";
		}

		if(nbtTagCompound.hasKey("SummonSet")) {
			NBTTagCompound summonSetNBT = nbtTagCompound.getCompoundTag("SummonSet");
			SummonSet summonSet = new SummonSet(null);
			summonSet.readFromNBT(summonSetNBT);
			this.summonSet = summonSet;
		}
		else {
			this.summonSet = null;
		}

		if(nbtTagCompound.hasKey("MinionIDs")) {
			NBTTagList minionIDs = nbtTagCompound.getTagList("MinionIDs", 10);
			this.loadMinionIDs = new String[minionIDs.tagCount()];
			for(int i = 0; i < minionIDs.tagCount(); i++) {
				NBTTagCompound minionID = minionIDs.getCompoundTagAt(i);
				if(minionID.hasKey("ID")) {
					this.loadMinionIDs[i] = minionID.getString("ID");
				}
			}
		}

		// Fuel:
		if(nbtTagCompound.hasKey("Fuel")) {
			this.summoningFuel = nbtTagCompound.getInteger("Fuel");
		}
		if(nbtTagCompound.hasKey("FuelMax")) {
			this.summoningFuelMax = nbtTagCompound.getInteger("FuelMax");
		}
		if(nbtTagCompound.hasKey("Items")) {
			NonNullList<ItemStack> itemStacks = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(nbtTagCompound, itemStacks); // Reads ItemStack into a List from "Items" tag.

			for(int i = 0; i < itemStacks.size(); ++i) {
				if(i < this.getSizeInventory()) {
					ItemStack itemStack = itemStacks.get(i);
					if(itemStack.isEmpty())
						this.setInventorySlotContents(i, ItemStack.EMPTY);
					else {
						this.setInventorySlotContents(i, itemStack);
					}
				}
			}
		}

		super.readFromNBT(nbtTagCompound);
	}

	/** Writes to NBT data. **/
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);

		if(this.ownerUUID == null) {
			nbtTagCompound.setString("OwnerUUID", "");
		}
		else {
			nbtTagCompound.setString("OwnerUUID", this.ownerUUID.toString());
		}

		if(this.summonSet != null) {
			NBTTagCompound summonSetNBT = new NBTTagCompound();
			this.summonSet.writeToNBT(summonSetNBT);
			nbtTagCompound.setTag("SummonSet", summonSetNBT);
		}

		nbtTagCompound.setString("OwnerName", this.ownerName);

		if(this.minions.size() > 0) {
			NBTTagList minionIDs = new NBTTagList();
			for(EntityLivingBase minion : this.minions) {
				NBTTagCompound minionID = new NBTTagCompound();
				minionID.setString("ID", minion.getUniqueID().toString());
				minionIDs.appendTag(minionID);
			}
			nbtTagCompound.setTag("MinionIDs", minionIDs);
		}

		// Fuel:
		nbtTagCompound.setInteger("Fuel", this.summoningFuel);
		nbtTagCompound.setInteger("FuelMax", this.summoningFuelMax);
		ItemStackHelper.saveAllItems(nbtTagCompound, this.itemStacks);

		return nbtTagCompound;
	}


	// ========================================
	//                Open GUI
	// ========================================
	@Override
	public Object getGUI(EntityPlayer player) {
		if(this.getWorld().isRemote)
			return new SummoningPedestalScreen(player, this);
		return new SummoningPedestalContainer(this, player.inventory);
	}
}
