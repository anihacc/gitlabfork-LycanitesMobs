package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.info.ItemDrop;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
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

public class EntityYale extends AgeableCreatureEntity implements IShearable {

	protected static final DataParameter<Byte> FUR = EntityDataManager.createKey(EntityYale.class, DataSerializers.BYTE);

	public ItemDrop woolDrop;

	/**
	 * Simulates a crafting instance between two dyes and uses the result dye as a mixed color, used for babies with different colored parents.
	 */
	private final InventoryCrafting colorMixer = new InventoryCrafting(new Container() {
		private static final String __OBFID = "CL_00001649";
		public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
			return false;
		}
	}, 2, 1);
    
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

		// Load Shear Drop From Config:
		this.woolDrop = new ItemDrop(Blocks.WOOL.getRegistryName().toString(), 0, 1).setMinAmount(1).setMaxAmount(3);
		this.woolDrop = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getItemDrop("Features", "Yale Shear Drop", this.woolDrop, "The item dropped by Yales when sheared. Format is: itemid,metadata,quantitymin,quantitymax,chance");
	}

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
		this.tasks.addTask(this.nextDistractionGoalIndex++, new TemptGoal(this).setItemList("diet_herbivore"));
		this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
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
	public boolean canBeColored(EntityPlayer player) {
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

	public int getMixedFurColor(BaseCreatureEntity entityA, BaseCreatureEntity entityB) {
		int i = 15 - entityA.getColor();
		int j = 15 - entityB.getColor();
		if(i == j)
			return 15 - i;
		this.colorMixer.getStackInSlot(0).setItemDamage(i);
		this.colorMixer.getStackInSlot(1).setItemDamage(j);
		ItemStack itemstack = CraftingManager.findMatchingResult(this.colorMixer, entityA.world);
		int k;
		if(!itemstack.isEmpty() && itemstack.getItem() == Items.DYE)
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
        IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
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
    public boolean canBeLeashedTo(EntityPlayer player) {
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
	public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
		AgeableCreatureEntity baby = new EntityYale(this.getEntityWorld());
		int color = this.getMixedFurColor(this, partner);
		baby.setColor(color);
		return baby;
	}
    
	
    // ==================================================
    //                        NBT
    // ==================================================
	// ========== Read ===========
	@Override
	public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
		super.readEntityFromNBT(nbtTagCompound);
		if(nbtTagCompound.hasKey("HasFur")) {
			this.setFur(nbtTagCompound.getBoolean("HasFur"));
		}
	}

	// ========== Write ==========
	@Override
	public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
		super.writeEntityToNBT(nbtTagCompound);
		nbtTagCompound.setBoolean("HasFur", this.hasFur());
	}
}
