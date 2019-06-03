package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAglebemu extends EntityCreatureTameable implements IMob {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAglebemu(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.hasAttackSound = true;
        this.spreadFire = false;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;
        this.setupMob();

        this.setPathPriority(PathNodeType.WATER, 0F);
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIAttackMelee(this).setRange(3));
        this.tasks.addTask(5, this.aiSit);
        this.tasks.addTask(6, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(6, new EntityAITargetOwnerThreats(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Random Leaping:
        if(this.onGround && !this.getEntityWorld().isRemote && this.isMoving()) {
            if(this.hasAttackTarget()) {
                if(this.rand.nextInt(5) == 0)
                    this.leap(6.0F, 0.6D, this.getAttackTarget());
            }
            else {
                if(this.rand.nextInt(25) == 0)
                    this.leap(1.0D, 1.0D);
            }
        }
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
        if(this.isInWater()) // Checks specifically just for water.
            return 2.0F;
        else if(this.waterContact()) // Checks for water, rain, etc.
            return 1.5F;
        return super.getAISpeedModifier();
    }

    // Pathing Weight:
    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState blockState = this.getEntityWorld().getBlockState(pos);
        if(blockState.getBlock() == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(blockState.getBlock() == Blocks.FLOWING_WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * waterWeight;
        if(this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(pos))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getAttackTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }

    // Pushed By Water:
    @Override
    public boolean isPushedByWater() {
        return false;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 100;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canBreatheAboveWater() {
        return true;
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 5; }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityAglebemu(this.getEntityWorld());
	}
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
