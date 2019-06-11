package com.lycanitesmobs.core.block;

import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockBase extends Block {
	
	// Properties:
	public ModInfo group;
	public String blockName = "BlockBase";
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
	
	// Stats:
	/** If set to a value above 0, the block will update on the specified number of ticks. **/
	public int tickRate = 0;
	/** If true, this block will be set to air on it's first tick, useful for blocks that despawn over time like fire. **/
	public boolean removeOnTick = false;
	/** If true after performing a tick update, another tick update will be scheduled thus creating a loop. **/
	public boolean loopTicks = true;
	/** Will falling blocks such as sand or gravel destroy this block if they land on it? */
	public boolean canBeCrushed = false;
	
	/** If true, this block can be walked through, if false, it can be walked through based on the material. **/
	public boolean noEntityCollision = false;
	/** If true, this block cannot be broken or even hit like a solid block. **/
	public boolean noBreakCollision = false;
	/** Whether or not light can pass through this block, useful for blocks such as glass. Setting this to false will also stop blocks behind it from rendering. **/
	public boolean isOpaque = true;
	
	// Rendering:
	public static enum RENDER_TYPE {
		NONE(-1), NORMAL(0), CROSS(1), TORCH(2), FIRE(3), FLUID(4); // More found on RenderBlock, or use Client Proxies for custom renderers.
		public final int id;
	    private RENDER_TYPE(int value) { this.id = value; }
	    public int getValue() { return id; }
	}
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public BlockBase(Material material, ModInfo group, String name) {
        super(material);
        this.group = group;
        this.blockName = name;
        this.setup();
    }

	public BlockBase(Material material) {
		super(material);
	}
	
	/** Should be called by a child class once the blockName and other important variables are set, kind of a late construct. **/
	public void setup() {
        this.setRegistryName(this.group.filename, this.blockName.toLowerCase());
		this.setUnlocalizedName(this.blockName);
	}

    /** Copies various basic attributes from this block to the provided block, such as hardness. **/
    public void copyAttributesTo(Block block) {
        block.setHardness(this.blockHardness);
        block.setResistance(this.blockResistance);
    }


    // ==================================================
    //                      Info
    // ==================================================
	@Override
	public String getLocalizedName() {

    	return LanguageManager.translate(this.getUnlocalizedName() + ".name");
	}

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(this.getDescription(stack, world));
    }

    public String getDescription(ItemStack itemStack, @Nullable World world) {
        return LanguageManager.translate("tile." + this.blockName + ".description");
    }
	
	
	// ==================================================
	//                      Place
	// ==================================================
	@Override
	public void onBlockAdded(World world, BlockPos pos, BlockState state) {
		// Initial Block Ticking:
		if(this.tickRate > 0)
			world.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(world));
	}
	
	
	// ==================================================
	//                      Break
	// ==================================================
	//========== Drops ==========
	@Override
	public Item getItemDropped(BlockState state, Random random, int zero) {
        return super.getItemDropped(state, random, zero);
	}
	
	@Override
	public int damageDropped(BlockState state) {
		return 0;
	}
    
	@Override
	public int quantityDropped(Random random) {
        return 1;
    }


    // ==================================================
    //                   Block States
    // ==================================================
    @Override
    public BlockStateContainer createBlockState() {
        return super.createBlockState();
    }

    @Override
    public BlockState getStateFromMeta(int meta) {
        return this.getDefaultState();
    }

    @Override
    public int getMetaFromState(BlockState state) {
        return 0;
    }
	

	// ==================================================
	//                   Block Updates
	// ==================================================
    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos triggerPos) { //triggerPos?
        // Crushable:
		if(this.canBeCrushed)
			if(block == Blocks.SAND || block == Blocks.GRAVEL)
	        	world.removeBlock(pos);
        super.neighborChanged(state, world, pos, block, triggerPos);
    }
    
    
	// ==================================================
	//                     Ticking
	// ==================================================
    // ========== Tick Rate ==========
    @Override
    public int tickRate(World world) {
    	return this.tickRate;
    }

    // ========== Tick Update ==========
    @Override
    public void updateTick(World world, BlockPos pos, BlockState state, Random rand) {
        if (world.isRemote)
            return;

        // Remove On Tick:
        if (this.removeOnTick && this.canRemove(world, pos, state, rand))
            world.removeBlock(pos);

        // Looping Tick:
        else if (this.tickRate > 0 && this.loopTicks)
            world.scheduleBlockUpdate(pos, this, this.tickRate(world), 1);
    }
    
    // ========== Should Tick ==========
    @Override
    public boolean getTickRandomly() {
        return this.tickRate > 0;
    }

    // ========== Can Remove ==========
    /** Returns true if the block should be removed naturally (remove on tick). **/
    public boolean canRemove(World world, BlockPos pos, BlockState state, Random rand) {
        return true;
    }
    
    
	// ==================================================
	//                    Collision
	// ==================================================
	// ========== Is Block Passable ==========
    @Override
    public boolean isPassable(IBlockAccess world, BlockPos pos) {
        if(this.noEntityCollision) {
			return true;
		}
        return super.isPassable(world, pos);
    }

	// ========== Punch Collision ==========
	@Override
	public boolean isCollidable() {
    	if(this.noBreakCollision) {
    		return false;
		}
		return super.isCollidable();
	}

    // ========== Is Block Solid ==========
    @Override
    public boolean isSideSolid(BlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        if(this.noEntityCollision)
            return false;
        return super.isSideSolid(base_state, world, pos, side);
    }

    // ========== Physical Collision Box ==========
    @Override
    public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess world, BlockPos pos) {
        return super.getBoundingBox(state, world, pos);
    }

    // ========== Collision Bounding Box ==========
    @Override
    public AxisAlignedBB getCollisionBoundingBox(BlockState state, IBlockAccess world, BlockPos pos) {
        if(this.noEntityCollision)
            return Block.NULL_AABB;
        return super.getCollisionBoundingBox(state, world, pos);
    }

    // ========== Selection Box ==========
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBox(BlockState state, World world, BlockPos pos) {
        return super.getSelectedBoundingBox(state, world, pos);
    }
    
    // ========== Is Opaque ==========
    @Override
    public boolean isOpaqueCube(BlockState state) {
        return this.isOpaque;
    }
    
    
	// ==================================================
	//                Collision Effects
	// ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, BlockState blockState, Entity entity) {
		super.onEntityCollidedWithBlock(world, pos, blockState, entity);
	}

    @Override
    public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, BlockState blockState, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
        if(this.noEntityCollision && world instanceof World)
            this.onEntityCollidedWithBlock((World)world, blockpos, blockState, entity);
        return super.isEntityInsideMaterial(world, blockpos, blockState, entity, yToTest, materialIn, testingHead);
    }

    /** Sets the stepping sound for this block, this method is added because setStepSound is now private. **/
    public BlockBase setBlockStepSound(SoundType soundType) {
        return (BlockBase)this.setSoundType(soundType);
    }
}
