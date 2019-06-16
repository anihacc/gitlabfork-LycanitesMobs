package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.block.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockQuickWeb extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockQuickWeb(Block.Properties properties) {
		super(properties);
		this.setDefaultState(this.getStateContainer().getBaseState().with(AGE, 0));
        //this.setCreativeTab(LycanitesMobs.blocksTab);
		
		// Properties:
		this.group = LycanitesMobs.modInfo;
		this.blockName = "quickweb";
		this.setup();
		
		// Stats:
		this.tickRate = 200;
		this.removeOnTick = true;
		this.loopTicks = false;
		this.canBeCrushed = false;
		
		//this.noEntityCollision = true;
		this.noBreakCollision = false;
		this.isOpaque = false;
		
		//this.setHardness(0.1F);
		//this.setHarvestLevel("sword", 0);
		//this.setLightOpacity(1);
	}
	
	
	// ==================================================
	//                     Break
	// ==================================================
    /*@Override
    public Item getItemDropped(BlockState blockState, Random random, int fortune) {
        return ObjectManager.getItem("quickwebcharge");
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
    //                      Rendering
    // ==================================================
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
