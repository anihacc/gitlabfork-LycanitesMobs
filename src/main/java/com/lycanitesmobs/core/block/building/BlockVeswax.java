package com.lycanitesmobs.core.block.building;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockBase;
import com.lycanitesmobs.core.entity.creature.EntityVespidQueen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BlockVeswax extends BlockBase {
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockVeswax(Block.Properties properties, String name) {
		super(properties);

		this.group = LycanitesMobs.modInfo;
		this.blockName = name;
		
		// Stats:
		this.tickRate = 100;
		this.removeOnTick = true;

		this.setRegistryName(this.group.modid, this.blockName.toLowerCase());
		this.setDefaultState(this.getStateContainer().getBaseState().with(AGE, 0));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}


    // ==================================================
    //                   Placement
    // ==================================================
    @Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        int orientationMeta = placer.getHorizontalFacing().getOpposite().getIndex();
        orientationMeta += 8;
        world.setBlockState(pos, state.with(AGE, orientationMeta), 1);
    }


    // ==================================================
    //                     Ticking
    // ==================================================
    // ========== Tick Rate ==========
    @Override
    public int tickRate(IWorldReader world) {
        return this.tickRate;
    }

    @Override
	public boolean ticksRandomly(BlockState state) {
		return state.get(AGE) < 8;
	}

    // ========== Tick Update ==========
	@Override
	public void func_225534_a_(BlockState state, ServerWorld world, BlockPos pos, Random random) { //tick()
        if(world.isRemote)
            return;
        double range = 32D;
        if(!world.getEntitiesWithinAABB(EntityVespidQueen.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range)).isEmpty())
            return;
        super.func_225534_a_(state, world, pos, random);
    }
}
