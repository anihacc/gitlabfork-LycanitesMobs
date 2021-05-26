package com.lycanitesmobs.core.entity.creature;

import com.google.common.collect.Maps;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.EatBlockGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import com.lycanitesmobs.core.info.ItemDrop;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class EntityYale extends AgeableCreatureEntity implements IForgeShearable {

	protected static final DataParameter<Byte> FUR = EntityDataManager.createKey(EntityYale.class, DataSerializers.BYTE);

	private static final Map<DyeColor, IItemProvider> WOOL_BY_COLOR = Util.make(Maps.newEnumMap(DyeColor.class), (itemProviderMap) -> {
		itemProviderMap.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
		itemProviderMap.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
		itemProviderMap.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
		itemProviderMap.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
		itemProviderMap.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
		itemProviderMap.put(DyeColor.LIME, Blocks.LIME_WOOL);
		itemProviderMap.put(DyeColor.PINK, Blocks.PINK_WOOL);
		itemProviderMap.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
		itemProviderMap.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
		itemProviderMap.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
		itemProviderMap.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
		itemProviderMap.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
		itemProviderMap.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
		itemProviderMap.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
		itemProviderMap.put(DyeColor.RED, Blocks.RED_WOOL);
		itemProviderMap.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
	});
	private static final Map<DyeColor, float[]> DYE_TO_RGB = Maps.newEnumMap(Arrays.stream(DyeColor.values()).collect(Collectors.toMap((DyeColor p_200204_0_) -> {
		return p_200204_0_;
	}, EntityYale::createSheepColor)));

    protected ItemDrop woolDrop;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityYale(EntityType<? extends EntityYale> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

        this.canGrow = true;
        this.babySpawnChance = 0.1D;
        this.fleeHealthPercent = 1.0F;
        this.isAggressiveByDefault = false;
        this.setupMob();

		this.woolDrop = new ItemDrop(Blocks.WHITE_WOOL.getRegistryName().toString(), 1).setMinAmount(1).setMaxAmount(3);
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
		this.goalSelector.addGoal(this.nextIdleGoalIndex++, new EatBlockGoal(this).setBlocks(Blocks.GRASS).setReplaceBlock(Blocks.DIRT));
		super.registerGoals();
		this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new TemptGoal(this).setIncludeDiet(true));
		this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
    }
	
	// ========== Init ==========
    @Override
    protected void registerData() {
        super.registerData();
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

	public boolean isShearable(@Nonnull ItemStack item, IWorldReader world, BlockPos pos) {
		return this.hasFur() && !this.isChild();
	}


	public ArrayList<ItemStack> onSheared(@Nonnull ItemStack item, IWorld world, BlockPos pos, int fortune) {
		ArrayList<ItemStack> dropStacks = new ArrayList<>();
		if(this.woolDrop == null) {
			return dropStacks;
		}

		this.setFur(false);
		this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);

		int quantity = this.woolDrop.getQuantity(this.getRNG(), fortune, 1);
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
	public void setColor(DyeColor color) {
		Item woolItem = WOOL_BY_COLOR.get(this.getColor()).asItem();
        if(this.woolDrop == null) {
			this.woolDrop = new ItemDrop(woolItem.getRegistryName().toString(), 1).setMinAmount(1).setMaxAmount(3);
		}
		else if(this.woolDrop.getItemStack().getItem() != woolItem) {
			this.woolDrop.setDrop(new ItemStack(woolItem, 1));
		}
		super.setColor(color);
	}

	public DyeColor getRandomFurColor(Random random) {
		int i = random.nextInt(100);
		if (i < 5) {
			return DyeColor.BLACK;
		} else if (i < 10) {
			return DyeColor.GREEN;
		} else if (i < 15) {
			return DyeColor.RED;
		} else if (i < 18) {
			return DyeColor.BROWN;
		} else {
			return random.nextInt(500) == 0 ? DyeColor.PINK : DyeColor.WHITE;
		}
	}
	
	/**
	 * Attempts to mix both parent sheep to come up with a mixed dye color.
	 */
	private DyeColor getMixedFurColor(BaseCreatureEntity father, BaseCreatureEntity mother) {
		DyeColor dyeA = father.getColor();
		DyeColor dyeB = mother.getColor();
		CraftingInventory craftinginventory = mixColors(dyeA, dyeB);
		return this.world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftinginventory, this.world).map((craftingRecipe) ->
				craftingRecipe.getCraftingResult(craftinginventory)).map(ItemStack::getItem).filter(DyeItem.class::isInstance).map(DyeItem.class::cast).map(DyeItem::getDyeColor).orElseGet(() ->
				this.world.rand.nextBoolean() ? dyeA : dyeB);
	}

	private static CraftingInventory mixColors(DyeColor dyeA, DyeColor dyeB) {
		CraftingInventory craftinginventory = new CraftingInventory(new Container(null, -1) {
			public boolean canInteractWith(PlayerEntity playerIn) {
				return false;
			}
		}, 2, 1);
		craftinginventory.setInventorySlotContents(0, new ItemStack(DyeItem.getItem(dyeA)));
		craftinginventory.setInventorySlotContents(1, new ItemStack(DyeItem.getItem(dyeB)));
		return craftinginventory;
	}

	private static float[] createSheepColor(DyeColor p_192020_0_) {
		if (p_192020_0_ == DyeColor.WHITE) {
			return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
		} else {
			float[] afloat = p_192020_0_.getColorComponentValues();
			float f = 0.75F;
			return new float[]{afloat[0] * 0.75F, afloat[1] * 0.75F, afloat[2] * 0.75F};
		}
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
            if(blockState.getMaterial() == Material.ORGANIC)
                return 10F;
            if(blockState.getMaterial() == Material.EARTH)
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
	//                     Equipment
	// ==================================================
	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.BagSize; }
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
	@Override
	public boolean canDropItem(ItemDrop itemDrop) {
		if(!super.canDropItem(itemDrop)) {
			return false;
		}
		if(itemDrop.getItemStack().getItem() instanceof BlockItem && ((BlockItem)itemDrop.getItemStack().getItem()).getBlock().getRegistryName().toString().contains("_wool")) {
			return this.hasFur();
		}
		return true;
	}

	@Override
	public void dropItem(ItemStack itemStack) {
		if(this.woolDrop != null && itemStack.getItem() instanceof BlockItem && ((BlockItem)itemStack.getItem()).getBlock().getRegistryName().toString().contains("_wool")) {
			itemStack = new ItemStack(this.woolDrop.getItemStack().getItem(), itemStack.getCount());
		}
		super.dropItem(itemStack);
	}
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
		AgeableCreatureEntity baby = super.createChild(partner);
		DyeColor color = this.getMixedFurColor(this, partner);
        baby.setColor(color);
		return baby;
	}
    
	
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Used when loading this mob from a saved chunk. **/
    @Override
    public void readAdditional(CompoundNBT nbtTagCompound) {
    	super.readAdditional(nbtTagCompound);
    	if(nbtTagCompound.contains("HasFur")) {
    		this.setFur(nbtTagCompound.getBoolean("HasFur"));
    	}
    }
    
    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeAdditional(CompoundNBT nbtTagCompound) {
    	super.writeAdditional(nbtTagCompound);
    	nbtTagCompound.putBoolean("HasFur", this.hasFur());
    }
}
