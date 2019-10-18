package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EatBlockGoal extends EntityAIBase {
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
		this.setMutexBits(3);
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
    	 if(this.host.getRNG().nextInt(this.host.isChild() ? 50 : 1000) != 0)
             return false;
    	 
    	 int i = MathHelper.floor(this.host.posX);
         int j = MathHelper.floor(this.host.posY);
         int k = MathHelper.floor(this.host.posZ);

         IBlockState IBlockState = this.host.getEntityWorld().getBlockState(new BlockPos(i, j - 1, k));
         return this.isValidBlock(IBlockState);
     }
  	
     
  	// ==================================================
   	//                 Valid Block Check
   	// ==================================================
     public boolean isValidBlock(IBlockState IBlockState) {
         for(Block edibleBlock : this.blocks) {
        	 if(edibleBlock == IBlockState.getBlock())
        		 return true;
         }
         
         Material material = IBlockState.getMaterial();
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
    	 this.eatTime = 40;
         this.host.clearMovement();
     }
 	
     
 	// ==================================================
  	//                       Reset
  	// ==================================================
 	@Override
    public void resetTask() {
    	 this.eatTime = 40;
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
    public void updateTask() {
         if(--this.eatTime != 0) return;
         
         int i = MathHelper.floor(this.host.posX);
         int j = MathHelper.floor(this.host.posY);
         int k = MathHelper.floor(this.host.posZ);
         IBlockState IBlockState = this.host.getEntityWorld().getBlockState(new BlockPos(i, j - 1, k));
         
         if(this.isValidBlock(IBlockState)) {
             //if(this.host.getEntityWorld().getGameRules().getGameRuleBooleanValue("mobGriefing"))
        	 this.host.getEntityWorld().setBlockToAir(new BlockPos(i, j - 1, k));
         }

         this.host.getEntityWorld().playEvent(2001, new BlockPos(i, j - 1, k), Block.getStateId(IBlockState));
         this.host.getEntityWorld().setBlockState(new BlockPos(i, j - 1, k), this.replaceBlock.getDefaultState(), 2);
         this.host.onEat();
     }
}
