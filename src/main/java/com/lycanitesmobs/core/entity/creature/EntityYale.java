package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ItemDrop;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.ArrayList;
import java.util.Random;

public class EntityYale extends EntityCreatureAgeable implements IAnimals, IGroupAnimal, IShearable {
	
	public ItemDrop woolDrop;
	
	/**
	 * Simulates a crafting instance between two dyes and uses the result dye as a mixed color, used for babies with different colored parents.
	 */
	private final InventoryCrafting colorMixer = new InventoryCrafting(new Container() {
        private static final String __OBFID = "CL_00001649";
        public boolean canInteractWith(PlayerEntity par1PlayerEntity) {
            return false;
        }
    }, 2, 1);

    protected static final DataParameter<Byte> FUR = EntityDataManager.createKey(EntityCreatureBase.class, DataSerializers.BYTE);
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityYale(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.fleeHealthPercent = 1.0F;
        this.isAggressiveByDefault = false;
        this.setupMob();
        
        // Add Dyes to the Color Mixer:
        this.colorMixer.setInventorySlotContents(0, new ItemStack(Items.DYE, 1, 0));
        this.colorMixer.setInventorySlotContents(1, new ItemStack(Items.DYE, 1, 0));

        // Load Shear Drop From Config:
		this.woolDrop = new ItemDrop(Blocks.WOOL.getRegistryName().toString(), 0, 1).setMinAmount(1).setMaxAmount(3);
		this.woolDrop = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getItemDrop("Features", "Yale Shear Drop", this.woolDrop, "The item dropped by Yales when sheared. Format is: itemid,metadata,quantitymin,quantitymax,chance");
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(2, new EntityAIAvoid(this).setNearSpeed(1.3D).setFarSpeed(1.2D).setNearDistance(5.0D).setFarDistance(20.0D));
        this.tasks.addTask(3, new EntityAIMate(this));
        this.tasks.addTask(4, new EntityAITempt(this).setItemList("vegetables"));
        this.tasks.addTask(5, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.tasks.addTask(6, new EntityAIEatBlock(this).setBlocks(Blocks.GRASS_BLOCK).setReplaceBlock(Blocks.DIRT));
        this.tasks.addTask(7, new EntityAIWander(this).setPauseRate(30));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(2, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
        this.targetTasks.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
    }
	
	// ========== Init ==========
    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(FUR, (byte) 1);
    }
	
	
    // ==================================================
    //                      Spawn
    // ==================================================
	// ========== On Spawn ==========
	@Override
	public void onFirstSpawn() {
		if(!this.isChild())
			this.setColor(this.getRandomFurColor(this.getRNG()));
		super.onFirstSpawn();
	}
	
	
    // ==================================================
    //                      Abilities
    // ==================================================
	// ========== IShearable ==========
	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return this.hasFur() && !this.isChild();
	}
	
	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		ArrayList<ItemStack> dropStacks = new ArrayList<>();
		if(this.woolDrop == null) {
			return dropStacks;
		}

		this.setFur(false);
		this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);

		int quantity = this.woolDrop.getQuantity(this.getRNG(), fortune);
		ItemStack dropStack = this.woolDrop.getEntityDropItemStack(this, quantity);
		this.dropItem(dropStack);
		dropStacks.add(dropStack);
		
		return dropStacks;
	}
	
	// ========== Fur ==========
	public boolean hasFur() {
		if(this.dataManager == null) return true;
		return this.dataManager.get(FUR) > 0;
	}

	public void setFur(boolean fur) {
		if(!this.getEntityWorld().isRemote)
			this.dataManager.set(FUR, (byte) (fur ? 1 : 0));
	}
	
	@Override
	public void onEat() {
		if(!this.getEntityWorld().isRemote)
			this.setFur(true);
	}
	
	@Override
	public boolean canBeColored(PlayerEntity player) {
		return true;
	}
	
	@Override
	public void setColor(int color) {
        if(this.woolDrop == null) {
			this.woolDrop = new ItemDrop(Blocks.WOOL.getRegistryName().toString(), 0, 1).setMinAmount(1).setMaxAmount(3);
		}
		else if(this.woolDrop.getItemStack().getItem() == Item.getItemFromBlock(Blocks.WOOL)) {
			this.woolDrop.setDrop(new ItemStack(Blocks.WOOL, 1, color));
		}
		super.setColor(color);
	}
	
	public int getRandomFurColor(Random random) {
        int i = random.nextInt(100);
        return i < 5 ? 15 : (i < 10 ? 7 : (i < 15 ? 8 : (i < 18 ? 12 : (random.nextInt(500) == 0 ? 6 : 0))));
    }
	
	public int getMixedFurColor(EntityCreatureBase entityA, EntityCreatureBase entityB) {
		int i = 15 - entityA.getColor();
        int j = 15 - entityB.getColor();
        if(i == j)
            return 15 - i;
        this.colorMixer.getStackInSlot(0).setItemDamage(i);
        this.colorMixer.getStackInSlot(1).setItemDamage(j);
        ItemStack itemstack = CraftingManager.findMatchingResult(this.colorMixer, entityA.world);
        int k;
        if(itemstack != null && itemstack.getItem() == Items.DYE)
            k = itemstack.getItemDamage();
        else
            k = this.getEntityWorld().rand.nextBoolean() ? i : j;
        return 15 - k;
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
    // ========== Pathing Weight ==========
    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        BlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
        Block block = blockState.getBlock();
        if(block != Blocks.AIR) {
            if(blockState.getMaterial() == Material.GRASS)
                return 10F;
            if(blockState.getMaterial() == Material.GROUND)
                return 7F;
        }
        return super.getBlockPathWeight(x, y, z);
    }

    // ========== Can leash ==========
    @Override
    public boolean canBeLeashedTo(PlayerEntity player) {
        return true;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 50;
    }
    
    
    // ==================================================
   	//                      Drops
   	// ==================================================
    // ========== Drop Items ==========
    /** Cycles through all of this entity's DropRates and drops random loot, usually called on death. If this mob is a minion, this method is cancelled. **/
    @Override
    protected void dropFewItems(boolean playerKill, int lootLevel) {
    	if(!this.hasFur())
    		this.woolDrop.setMinAmount(0).setMaxAmount(0);
    	super.dropFewItems(playerKill, lootLevel);
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable partner) {
		EntityCreatureAgeable baby = new EntityYale(this.getEntityWorld());
		int color = this.getMixedFurColor(this, partner);
        baby.setColor(color);
		return baby;
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack testStack) {
		return ObjectLists.inItemList("vegetables", testStack);
    }
    
	
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Used when loading this mob from a saved chunk. **/
    @Override
    public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
    	super.readEntityFromNBT(nbtTagCompound);
    	if(nbtTagCompound.hasKey("HasFur")) {
    		this.setFur(nbtTagCompound.getBoolean("HasFur"));
    	}
    }
    
    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
    	super.writeEntityToNBT(nbtTagCompound);
    	nbtTagCompound.setBoolean("HasFur", this.hasFur());
    }
}
