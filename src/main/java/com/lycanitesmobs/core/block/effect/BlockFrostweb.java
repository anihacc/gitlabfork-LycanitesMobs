package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class BlockFrostweb extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFrostweb(Block.Properties properties) {
		super(properties);

		this.group = LycanitesMobs.modInfo;
		this.blockName = "frostweb";
		
		// Stats:
		this.tickRate = 200;
		this.removeOnTick = true;
		this.loopTicks = false;
		this.canBeCrushed = false;

		this.noBreakCollision = false;

		this.setRegistryName(this.group.modid, this.blockName.toLowerCase());
		this.setDefaultState(this.getStateContainer().getBaseState().with(AGE, 0));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}


	// ==================================================
	//                     Break
	// ==================================================
    /*@Override
    public Item getItemDropped(BlockState blockState, Random random, int fortune) {
        return ObjectManager.getItem("frostwebcharge");
    }

    @Override
    public int damageDropped(BlockState blockState) {
        return 0;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }*/
    
    
	// ==================================================
	//                Collision Effects
	// ==================================================
	@Override
	public void onEntityCollision(BlockState blockState, World world, BlockPos pos, Entity entity) {
		super.onEntityCollision(blockState, world, pos, entity);
		entity.setMotionMultiplier(blockState, new Vec3d(0.25D, (double)0.05F, 0.25D));
	}
    
    
	// ==================================================
	//                      Particles
	// ==================================================
    @Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();

		if (random.nextInt(100) == 0) {
			x += random.nextFloat();
			z += random.nextFloat();
			world.addParticle(ParticleTypes.ITEM_SNOWBALL, x, y, z, 0.0D, 0.0D, 0.0D);
		}
		super.animateTick(state, world, pos, random);
	}


	// ==================================================
	//                      Rendering
	// ==================================================
    /*@Override Redundant?
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }*/
}
