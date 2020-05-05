package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.EnumSet;

public class EatBlockGoal extends Goal {
	// Targets:
    private BaseCreatureEntity host;
    
    // Properties:
    private Block[] blocks = new Block[0];
    private Material[] materials = new Material[0];
    private Block replaceBlock = Blocks.AIR;
    private int eatTime = 40;
    private int eatTimeMax = 40;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EatBlockGoal(BaseCreatureEntity setHost) {
        this.host = setHost;
		this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    
    
    // ==================================================
   	//                  Set Properties
   	// ==================================================
     public EatBlockGoal setBlocks(Block... setBlocks) {
    	this.blocks = setBlocks;
     	return this;
     }

     public EatBlockGoal setMaterials(Material... setMaterials) {
    	this.materials = setMaterials;
     	return this;
     }

     public EatBlockGoal setReplaceBlock(Block block) {
    	this.replaceBlock = block;
     	return this;
     }

     public EatBlockGoal setEatTime(int setTime) {
    	this.eatTimeMax = setTime;
     	return this;
     }
 	
     
 	// ==================================================
  	//                   Should Execute
  	// ==================================================
 	@Override
    public boolean shouldExecute() {
    	 if(this.host.getRNG().nextInt(this.host.isBaby() ? 50 : 1000) != 0)
             return false;
    	 
    	 int i = MathHelper.floor(this.host.getPositionVec().getX());
         int j = MathHelper.floor(this.host.getPositionVec().getY());
         int k = MathHelper.floor(this.host.getPositionVec().getZ());

         BlockState blockState = this.host.getEntityWorld().getBlockState(new BlockPos(i, j - 1, k));
         return this.isValidBlock(blockState);
     }
  	
     
  	// ==================================================
   	//                 Valid Block Check
   	// ==================================================
     public boolean isValidBlock(BlockState blockState) {
         for(Block edibleBlock : this.blocks) {
        	 if(edibleBlock == blockState.getBlock())
        		 return true;
         }
         
         Material material = blockState.getMaterial();
         for(Material edibleMaterial : this.materials) {
        	 if(edibleMaterial == material)
        		 return true;
         }
         
         return false;
     }
 	
     
 	// ==================================================
  	//                      Start
  	// ==================================================
 	@Override
    public void startExecuting() {
    	 this.eatTime = this.eatTimeMax;
         this.host.clearMovement();
     }
 	
     
 	// ==================================================
  	//                       Reset
  	// ==================================================
 	@Override
    public void resetTask() {
    	 this.eatTime = this.eatTimeMax;
     }
  	
     
  	// ==================================================
   	//                      Continue
   	// ==================================================
  	@Override
    public boolean shouldContinueExecuting() {
    	  return this.eatTime > 0;
      }
 	
     
 	// ==================================================
  	//                      Update
  	// ==================================================
 	@Override
    public void tick() {
         if(--this.eatTime != 0) return;
         
         int i = MathHelper.floor(this.host.getPositionVec().getX());
         int j = MathHelper.floor(this.host.getPositionVec().getY());
         int k = MathHelper.floor(this.host.getPositionVec().getZ());
         BlockState blockState = this.host.getEntityWorld().getBlockState(new BlockPos(i, j - 1, k));
         
         if(this.isValidBlock(blockState)) {
             //if(this.host.getEntityWorld().getGameRules().getGameRuleBooleanValue("mobGriefing"))
        	 this.host.getEntityWorld().removeBlock(new BlockPos(i, j - 1, k), true); // Might be something else was x, y, z, false
         }

         this.host.getEntityWorld().playEvent(2001, new BlockPos(i, j - 1, k), Block.getStateId(blockState));
         this.host.getEntityWorld().setBlockState(new BlockPos(i, j - 1, k), this.replaceBlock.getDefaultState(), 2);
         this.host.onEat();
     }
}
