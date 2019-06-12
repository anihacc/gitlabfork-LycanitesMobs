package com.lycanitesmobs.core.block.building;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockBase;
import com.lycanitesmobs.core.entity.creature.EntityVespidQueen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Random;

public class BlockVeswax extends BlockBase {
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockVeswax(Block.Properties properties) {
		super(properties);
        //this.setCreativeTab(LycanitesMobs.blocksTab);
		
		// Properties:
		this.group = LycanitesMobs.modInfo;
		this.blockName = "veswax";
		this.setup();
		
		// Stats:
		//this.setHardness(0.6F);
		//this.setHarvestLevel("axe", 0);
        //this.setSoundType(SoundType.WOOD);
		this.tickRate = 100;
		this.removeOnTick = true;

		this.setDefaultState(this.stateContainer.getBaseState().with(AGE, 0));
	}


	// ==================================================
	//                   Block States
	// ==================================================
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}


    // ==================================================
    //                   Placement
    // ==================================================
    /*@Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        int orientationMeta = placer.getHorizontalFacing().getOpposite().getIndex();
        orientationMeta += 8;
        world.setBlockState(pos, state.withProperty(HIVE, orientationMeta), 2);
        super.onBlockPlacedBy(world, pos, state, placer, itemStack);
    }*/


    // ==================================================
    //                     Ticking
    // ==================================================
    // ========== Tick Rate ==========
    @Override
    public int tickRate(IWorldReader world) {
        return this.tickRate;
    }

    // ========== Tick Update ==========
    @Override
    public void randomTick(BlockState state, World world, BlockPos pos, Random random) {
        if(world.isRemote)
            return;
		int age = state.get(AGE);
        if(age >= 8)
            return;
        double range = 32D;
        if(!world.getEntitiesWithinAABB(EntityVespidQueen.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range)).isEmpty())
            return;
        super.tick(state, world, pos, random);
    }
}
