package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityVespid extends EntityCreatureAgeable implements IMob, IGroupPredator {
    public EntityAIPlaceBlock aiPlaceBlock;
	private boolean vespidHiveBuilding = true;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVespid(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();

        this.canGrow = true;
        this.babySpawnChance = 0.1D;

        this.stepHeight = 1.0F;
        this.setAttackCooldownMax(10);
        
        this.vespidHiveBuilding = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getBool("Features", "Vespid Hive Building", this.vespidHiveBuilding, "Set to false to stop Vespids from building hives all together.");
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setLongMemory(true));
        this.aiPlaceBlock = new EntityAIPlaceBlock(this).setMaxDistance(128D).setSpeed(3D);
        this.tasks.addTask(4, this.aiPlaceBlock);
        this.tasks.addTask(5, new EntityAIFollowMaster(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(8, new EntityAIWander(this).setPauseRate(20));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(EntityPlayer.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAITargetMasterAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true).setHelpClasses(EntityVespidQueen.class));
        this.targetTasks.addTask(3, new EntityAITargetMaster(this).setTargetClass(EntityVespidQueen.class).setRange(64.0D));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IAnimals.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityPlayer.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
	
	// ==================================================
  	//                       Spawning
  	// ==================================================
    @Override
    public boolean isPersistant() {
    	if(this.hasMaster() && this.getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL)
    		return true;
    	return super.isPersistant();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Hive No Clip:
        if(!this.getEntityWorld().isRemote) {
	        if(!this.noClip) {
	        	if(this.isHiveBlock(this.getPosition()))
	        		this.noClip = true;
	        }
	        else if(!this.isHiveBlock(this.getPosition())) {
	        	this.noClip = false;
	        }
        }
        
        // Building AI:
        if(!this.getEntityWorld().isRemote && this.vespidHiveBuilding && this.hasMaster() && this.getMasterTarget() instanceof EntityVespidQueen && this.aiPlaceBlock.block == null) {
        	EntityVespidQueen queen = (EntityVespidQueen)this.getMasterTarget();
        	
        	// Build Hive Foundations:
        	if(!queen.hiveFoundationsSet()) {
        		List<Byte> directions = new ArrayList<Byte>();
        		if(!queen.doesHiveHaveXPositive()) {
        			directions.add((byte)0);
        		}
        		if(!queen.doesHiveHaveXNegative()) {
        			directions.add((byte)1);
        		}
        		if(!queen.doesHiveHaveYPositive()) {
        			directions.add((byte)2);
        		}
        		if(!queen.doesHiveHaveYNegative()) {
        			directions.add((byte)3);
        		}
        		if(!queen.doesHiveHaveZPositive()) {
        			directions.add((byte)4);
        		}
        		if(!queen.doesHiveHaveZNegative()) {
        			directions.add((byte)5);
        		}
        		
        		BlockPos hivePos = queen.getHivePosition();
        		int hiveMin = 5;
        		int hiveMinFloor = 3;
        		int hiveMax = 16;
        		
        		byte direction = 6;
        		if(directions.size() == 1)
        			direction = directions.get(0);
        		else if(directions.size() > 1)
        			direction = directions.get(this.getRNG().nextInt(directions.size()));
        		
            	if(direction == 0) {
            		int endX = hivePos.getX();
            		for(int x = endX; x <= hivePos.getX() + hiveMax; x++) {
            			if(!this.aiPlaceBlock.canPlaceBlock(new BlockPos(x, hivePos.getY(), hivePos.getZ())))
            				break;
            			endX = x;
            		}
            		if(endX >= hivePos.getX() + hiveMin) {
	            		this.aiPlaceBlock.setMetadata(5); // East Block Facing WEST
	            		this.aiPlaceBlock.setBlockPlacement(ObjectManager.getBlock("veswax"), new BlockPos(endX, hivePos.getY(), hivePos.getZ()));
            		}
            	}
            	
            	else if(direction == 1) {
            		int endX = hivePos.getX() - hiveMin;
            		for(int x = endX; x >= hivePos.getX() - hiveMax; x--) {
            			if(!this.aiPlaceBlock.canPlaceBlock(new BlockPos(x, hivePos.getY(), hivePos.getZ())))
            				break;
            			endX = x;
            		}
            		if(endX <= hivePos.getX() - hiveMin) {
	            		this.aiPlaceBlock.setMetadata(4); // West Block Facing EAST
	            		this.aiPlaceBlock.setBlockPlacement(ObjectManager.getBlock("veswax"), new BlockPos(endX, hivePos.getY(), hivePos.getZ()));
            		}
            	}
        		
            	if(direction == 2) {
            		int endY = hivePos.getY() + hiveMin;
            		for(int y = endY; y <= hivePos.getY() + hiveMax; y++) {
            			if(!this.aiPlaceBlock.canPlaceBlock(new BlockPos(hivePos.getX(), y, hivePos.getZ())))
            				break;
            			endY = y;
            		}
            		if(endY >= ((int) hivePos.getY() + hiveMin)) {
	            		this.aiPlaceBlock.setMetadata(0); // Top Block Facing DOWN
	            		this.aiPlaceBlock.setBlockPlacement(ObjectManager.getBlock("propolis"), new BlockPos(hivePos.getX(), endY, hivePos.getZ()));
            		}
            	}
            	
            	else if(direction == 3) {
            		int endY = hivePos.getY() - hiveMinFloor;
            		for(int y = endY; y >= hivePos.getY() - hiveMax; y--) {
            			if(!this.aiPlaceBlock.canPlaceBlock(new BlockPos(hivePos.getX(), y, hivePos.getZ())))
            				break;
            			endY = y;
            		}
            		if(endY <= hivePos.getY() - hiveMin) {
	            		this.aiPlaceBlock.setMetadata(1); // Bottom Block Facing UP
	            		this.aiPlaceBlock.setBlockPlacement(ObjectManager.getBlock("propolis"), new BlockPos(hivePos.getX(), endY, hivePos.getZ()));
            		}
            	}
        		
            	if(direction == 4) {
            		int endZ = hivePos.getZ() + hiveMin;
            		for(int z = endZ; z <= hivePos.getZ() + hiveMax; z++) {
            			if(!this.aiPlaceBlock.canPlaceBlock(new BlockPos(hivePos.getX(), hivePos.getY(), z)))
            				break;
            			endZ = z;
            		}
            		if(endZ >= hivePos.getZ() + hiveMin) {
	            		this.aiPlaceBlock.setMetadata(2); // South Block Facing NORTH
	            		this.aiPlaceBlock.setBlockPlacement(ObjectManager.getBlock("veswax"), new BlockPos(hivePos.getX(), hivePos.getY(), endZ));
            		}
            	}
            	
            	else if(direction == 5) {
            		int endZ = hivePos.getZ() - hiveMin;
            		for(int z = endZ; z >= hivePos.getZ() - hiveMax; z--) {
            			if(!this.aiPlaceBlock.canPlaceBlock(new BlockPos(hivePos.getX(), hivePos.getY(), z)))
            				break;
            			endZ = z;
            		}
            		if(endZ <= hivePos.getZ() - hiveMin) {
	            		this.aiPlaceBlock.setMetadata(3); // North Block Facing SOUTH
	            		this.aiPlaceBlock.setBlockPlacement(ObjectManager.getBlock("veswax"), new BlockPos(hivePos.getX(), hivePos.getY(), endZ));
            		}
            	}
        	}
        	
        	// Build On Hive Foundations:
        	else {
        		List<EntityVespidQueen.HiveExposedCoordinates> hiveExposedCoordsList = queen.getHiveExposureBlocks();
        		if(hiveExposedCoordsList.size() > 0) {
        			EntityVespidQueen.HiveExposedCoordinates hiveExposedCoords;
        			if(hiveExposedCoordsList.size() > 1)
        				hiveExposedCoords = hiveExposedCoordsList.get(this.getRNG().nextInt(hiveExposedCoordsList.size()));
        			else
        				hiveExposedCoords = hiveExposedCoordsList.get(0);
        			this.aiPlaceBlock.setMetadata(hiveExposedCoords.orientationMeta);
	        		this.aiPlaceBlock.setBlockPlacement(hiveExposedCoords.block, hiveExposedCoords.pos);
        		}
        	}
        }
        
        // Don't Keep Infected Conbas Targeted:
        if(!this.getEntityWorld().isRemote && this.getAttackTarget() instanceof EntityConba) {
        	if(((EntityConba)this.getAttackTarget()).vespidInfection) {
        		this.setAttackTarget(null);
        	}
        }
    }
	
	// ========== Hive ==========
    public boolean isHiveBlock(BlockPos searchPos) {
        if(this.isHiveWall(searchPos) || this.isHiveFloor(searchPos))
            return true;
        return false;
    }

    public boolean isHiveWall(BlockPos searchPos) {
        IBlockState searchState = this.getEntityWorld().getBlockState(searchPos);
        Block searchBlock = searchState.getBlock();
        if(searchBlock != null)
            if(searchBlock == ObjectManager.getBlock("veswax") && searchBlock.getMetaFromState(searchState) < 8)
                return true;
        return false;
    }

    public boolean isHiveFloor(BlockPos searchPos) {
        IBlockState searchState = this.getEntityWorld().getBlockState(searchPos);
        Block searchBlock = searchState.getBlock();
        if(searchBlock != null)
            if(searchBlock == ObjectManager.getBlock("veswax") && searchBlock.getMetaFromState(searchState) < 8)
                return true;
        return false;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Can Attack Entity ==========
    @Override
    public boolean canAttackEntity(EntityLivingBase targetEntity) {
    	if(targetEntity == this.getMasterTarget())
    		return false;
    	if(targetEntity instanceof EntityConba)
        	return false;
    	if(targetEntity instanceof EntityVespid) {
    		if(!((EntityVespid)targetEntity).hasMaster() || ((EntityVespid)targetEntity).getMasterTarget() == this.getMasterTarget())
    			return false;
    	}
    	if(targetEntity instanceof EntityVespidQueen) {
    		if(!this.hasMaster() || this.getMasterTarget() == targetEntity)
    			return false;
    	}
    	return super.canAttackEntity(targetEntity);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 4.0F;
    	return 1.0F;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("inWall")) return false;
    	return super.isDamageTypeApplicable(type, source, damage);
    }
}
