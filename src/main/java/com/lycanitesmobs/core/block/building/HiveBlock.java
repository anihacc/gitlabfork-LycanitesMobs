package com.lycanitesmobs.core.block.building;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockBase;
import com.lycanitesmobs.core.entity.creature.VespidQueen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;

import java.util.Random;

public class HiveBlock extends BlockBase {
	// ==================================================
	//                   Constructor
	// ==================================================
	public HiveBlock(Block.Properties properties, String name) {
		super(properties);

		this.group = LycanitesMobs.modInfo;
		this.blockName = name;
		
		// Stats:
		this.tickRate = 100;
		this.removeOnTick = true;

		this.setRegistryName(this.group.modid, this.blockName.toLowerCase());
		this.registerDefaultState(this.getStateDefinition().any().setValue(AGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}


    // ==================================================
    //                   Placement
    // ==================================================
    @Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        int orientationMeta = placer.getDirection().getOpposite().get3DDataValue();
        orientationMeta += 8;
        world.setBlock(pos, state.setValue(AGE, orientationMeta), 1);
    }


    // ==================================================
    //                     Ticking
    // ==================================================
    // ========== Tick Rate ==========
    @Override
    public int tickRate(LevelReader world) {
        return this.tickRate;
    }

    @Override
	public boolean isRandomlyTicking(BlockState state) {
		return state.getValue(AGE) < 8;
	}

    // ========== Tick Update ==========
	@Override
	public void tick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        if(world.isClientSide || state.getValue(AGE) >= 8)
            return;
        double range = 32D;
        if(!world.getEntitiesOfClass(VespidQueen.class, new AABB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range)).isEmpty())
            return;
        super.tick(state, world, pos, random);
    }
}
