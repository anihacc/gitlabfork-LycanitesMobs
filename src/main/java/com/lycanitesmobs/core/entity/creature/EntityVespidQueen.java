package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.block.building.BlockVeswax;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.CopyMasterAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityVespidQueen extends AgeableCreatureEntity implements IMob, IGroupPredator {
	public boolean inHiveCache = false;
	private int hiveCheckCacheTime = 0;
	public class HiveExposedCoordinates {
		public Block block;
		public BlockPos pos;
		public int orientationMeta;
		
		public HiveExposedCoordinates(Block block, BlockPos pos, int orientationMeta) {
			this.block = block;
			this.pos = pos;
			this.orientationMeta = orientationMeta;
		}
	}
	public List<HiveExposedCoordinates> hiveExposedBlocks = new ArrayList<HiveExposedCoordinates>();
	private int hiveExposedBlockCacheTime = 0;
	
	private int vespidQueenSwarmLimit = 10; // TODO Creature Flags.
	private boolean vespidHiveBuilding = true;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVespidQueen(EntityType<? extends EntityVespidQueen> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.solidCollision = true;
        this.setupMob();
        
        this.canGrow = true;
        this.babySpawnChance = 0D;

        this.stepHeight = 1.0F;
        this.setAttackCooldownMax(10);
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(2, new AttackMeleeGoal(this).setLongMemory(true));
        this.goalSelector.addGoal(7, new StayByHomeGoal(this));
        this.goalSelector.addGoal(8, new WanderGoal(this).setPauseRate(1200));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(1, new CopyMasterAttackTargetGoal(this));
        this.targetSelector.addGoal(2, new FindAttackTargetGoal(this).setTargetClass(EntityConba.class));
        this.targetSelector.addGoal(3, new FindAttackTargetGoal(this).setTargetClass(EntityVespidQueen.class));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(EntityVespid.class));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(IGroupPrey.class));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(AnimalEntity.class));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(IGroupAnimal.class));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(VillagerEntity.class));
    }

	
	// ==================================================
  	//                       Spawning
  	// ==================================================
    @Override
    public boolean isPersistant() {
    	if(this.hasHome() && this.getEntityWorld().getDifficulty() != Difficulty.PEACEFUL)
    		return true;
    	return super.isPersistant();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        if(this.vespidHiveBuilding) {
	        // Hive Cache Times:
	        this.hiveCheckCacheTime--;
	        if(this.hiveCheckCacheTime < 0)
	        	this.hiveCheckCacheTime = 0;
	        this.hiveExposedBlockCacheTime--;
	        if(this.hiveExposedBlockCacheTime < 0)
	        	this.hiveExposedBlockCacheTime = 0;
	        
	        // Set Home In Hive:
	        if(!this.getEntityWorld().isRemote && !this.hasHome()) {
	        	if(this.hiveFoundationsSet()) {
	        		this.setHome((int)this.posX, (int)this.posY, (int)this.posZ, 16F);
	        	}
	        }
	        
	        // Spawn Babies:
	        if(!this.getEntityWorld().isRemote && this.hiveFoundationsSet() && this.ticksExisted % 60 == 0) {
				this.allyUpdate();
	        }
        }
        
        // Don't Keep Infected Conbas Targeted:
        if(!this.getEntityWorld().isRemote && this.getAttackTarget() instanceof EntityConba) {
        	if(((EntityConba)this.getAttackTarget()).vespidInfection) {
        		this.setAttackTarget(null);
        	}
        }
    }
    
    // ========== Spawn Babies ==========
	public void allyUpdate() {
		if(this.getEntityWorld().isRemote)
			return;
		
		// Spawn Babies:
		if(this.vespidQueenSwarmLimit > 0 && this.nearbyCreatureCount(EntityVespid.class, 32D) < this.vespidQueenSwarmLimit) {
			float random = this.rand.nextFloat();
			if(random <= 0.05F) {
				LivingEntity minion = this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
				if(minion instanceof AgeableCreatureEntity) {
		    		((AgeableCreatureEntity)minion).setGrowingAge(((AgeableCreatureEntity) minion).growthTime);
		    	}
			}
		}
	}
	
    public LivingEntity spawnAlly(double x, double y, double z) {
		LivingEntity minion = CreatureManager.getInstance().getCreature("vespid").createEntity(this.getEntityWorld());
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
    	if(minion instanceof BaseCreatureEntity) {
    		((BaseCreatureEntity)minion).applySubspecies(this.getSubspeciesIndex());
    	}
    	this.getEntityWorld().addEntity(minion);
        if(this.getAttackTarget() != null)
        	minion.setRevengeTarget(this.getAttackTarget());
        return minion;
    }

	// ========== Hive ==========
    public BlockPos getHivePosition() {
        if(this.hasHome())
            return this.getHomePosition();
        return this.getPosition();
    }

	public boolean hiveFoundationsSet() {
        return this.hiveFoundationsSet(false);
    }
	public boolean hiveFoundationsSet(boolean clearCache) {
		if(clearCache || this.hiveCheckCacheTime <= 0) {
			this.hiveCheckCacheTime = 100;
			if(!this.doesHiveHaveXPositive()) {
				this.inHiveCache = false;
				return false;
			}
			
			if(!this.doesHiveHaveXNegative()) {
				this.inHiveCache = false;
				return false;
			}
			
			if(!this.doesHiveHaveYPositive()) {
				this.inHiveCache = false;
				return false;
			}
			
			if(!this.doesHiveHaveYNegative()) {
				this.inHiveCache = false;
				return false;
			}
			
			if(!this.doesHiveHaveZPositive()) {
				this.inHiveCache = false;
				return false;
			}
			
			if(!this.doesHiveHaveZNegative()) {
				this.inHiveCache = false;
				return false;
			}
			
			this.inHiveCache = true;
			return true;
		}
		else {
			return this.inHiveCache;
		}
	}

    public boolean isHiveBlock(BlockPos searchPos) {
        if(this.isHiveWall(searchPos) || this.isHiveFloor(searchPos))
            return true;
        return false;
    }

    public boolean isHiveWall(BlockPos searchPos) {
        BlockState searchState = this.getEntityWorld().getBlockState(searchPos);
        Block searchBlock = searchState.getBlock();
        if(searchBlock != null)
            if(searchBlock == ObjectManager.getBlock("veswax"))
                return true;
        return false;
    }

    public boolean isHiveFloor(BlockPos searchPos) {
        BlockState searchState = this.getEntityWorld().getBlockState(searchPos);
        Block searchBlock = searchState.getBlock();
        if(searchBlock != null)
            if(searchBlock == ObjectManager.getBlock("propolis"))
                return true;
        return false;
    }
	
	public boolean doesHiveHaveXPositive() {
		BlockPos hivePos = this.getHivePosition();
		for(int x = hivePos.getX(); x <= hivePos.getX() + 28; x++) {
            if(this.isHiveWall(new BlockPos(x, hivePos.getY(), hivePos.getZ())))
                return true;
        }
        return false;
    }

    public boolean doesHiveHaveXNegative() {
        BlockPos hivePos = this.getHivePosition();
        for(int x = hivePos.getX(); x >= hivePos.getX() - 28; x--) {
            if(this.isHiveWall(new BlockPos(x, hivePos.getY(), hivePos.getZ())))
                return true;
        }
        return false;
	}
	
	public boolean doesHiveHaveYPositive() {
		BlockPos hivePos = this.getHivePosition();
		for(int y = hivePos.getY(); y <= hivePos.getY() + 28; y++) {
            if(this.isHiveFloor(new BlockPos(hivePos.getX(), y, hivePos.getZ())))
                return true;
		}
		return false;
	}
	
	public boolean doesHiveHaveYNegative() {
		BlockPos hivePos = this.getHivePosition();
		for(int y = hivePos.getY(); y >= hivePos.getY() - 28; y--) {
            if(this.isHiveFloor(new BlockPos(hivePos.getX(), y, hivePos.getZ())))
                return true;
		}
		return false;
	}
	
	public boolean doesHiveHaveZPositive() {
		BlockPos hivePos = this.getHivePosition();
		for(int z = hivePos.getZ(); z <= hivePos.getZ() + 28; z++) {
            if(this.isHiveWall(new BlockPos(hivePos.getX(), hivePos.getY(), z)))
                return true;
		}
		return false;
	}
	
	public boolean doesHiveHaveZNegative() {
		BlockPos hivePos = this.getHivePosition();
		for(int z = hivePos.getZ(); z >= hivePos.getZ() - 28; z--) {
            if(this.isHiveWall(new BlockPos(hivePos.getX(), hivePos.getY(), z)))
                return true;
		}
		return false;
	}
	
	public List<HiveExposedCoordinates> getHiveExposureBlocks() {
		if(this.hiveExposedBlockCacheTime <= 0) {
			this.hiveExposedBlockCacheTime = 200;
			this.hiveExposedBlocks = new ArrayList<>();
			BlockPos hivePos = this.getHivePosition();
			int hiveMax = 28;
			
			for(int x = hivePos.getX() - hiveMax; x <= hivePos.getX() + hiveMax; x++) {
				for(int y = hivePos.getY() - hiveMax; y <= hivePos.getY() + hiveMax; y++) {
					for(int z = hivePos.getZ() - hiveMax; z <= hivePos.getZ() + hiveMax; z++) {
                        BlockPos checkPos = new BlockPos(x, y, z);
						if(this.isHiveBlock(checkPos)) {
                            BlockState state = this.getEntityWorld().getBlockState(checkPos);
							Block block = state.getBlock();
							int orientationMeta = state.get(BlockVeswax.AGE) % 8;
							Direction facing = Direction.byIndex(orientationMeta);

							if(facing.getXOffset() == 0) {
								if(!this.isHiveBlock(checkPos.add(-1, 0, 0)) && this.canPlaceBlockAt(checkPos.add(-1, 0, 0)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(-1, 0, 0), orientationMeta));
								if(!this.isHiveBlock(checkPos.add(1, 0, 0)) && this.canPlaceBlockAt(checkPos.add(1, 0, 0)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(1, 0, 0), orientationMeta));
							}

							if(facing.getYOffset() == 0) {
                                if(!this.isHiveBlock(checkPos.add(0, -1, 0)) && this.canPlaceBlockAt(checkPos.add(0, -1, 0)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(0, -1, 0), orientationMeta));
                                if(!this.isHiveBlock(checkPos.add(0, 1, 0)) && this.canPlaceBlockAt(checkPos.add(0, 1, 0)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(0, 1, 0), orientationMeta));
							}

							if(facing.getZOffset() == 0) {
                                if(!this.isHiveBlock(checkPos.add(0, 0, -1)) && this.canPlaceBlockAt(checkPos.add(0, 0, -1)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(0, 0, -1), orientationMeta));
                                if(!this.isHiveBlock(checkPos.add(0, 0, 1)) && this.canPlaceBlockAt(checkPos.add(0, 0, 1)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(0, 0, 1), orientationMeta));
							}
						}
					}
				}
			}
		}
		return this.hiveExposedBlocks;
	}
	
	public boolean canPlaceBlockAt(BlockPos pos) {
        BlockState targetState = this.getEntityWorld().getBlockState(pos);
		Block targetBlock = targetState.getBlock();
        if(targetBlock == null)
			return false;
		if(targetBlock == Blocks.AIR)
			return true;
		if(targetState.getMaterial() == Material.WATER || targetState.getMaterial() == Material.LAVA)
			return true;
		return false;
	}
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
		if(!super.attackMelee(target, damageScale))
			return false;

		if(target instanceof EntityConba) {
			((EntityConba)target).vespidInfection = true;
			return true;
		}

        return true;
    }
    
    // ========== Can Attack Entity ==========
    @Override
    public boolean canAttack(LivingEntity targetEntity) {
    	if(targetEntity instanceof EntityConba)
        	if(((EntityConba)targetEntity).vespidInfection)
        		return false;
    	if(targetEntity instanceof EntityVespid) {
    		if(!((EntityVespid)targetEntity).hasMaster() || ((EntityVespid)targetEntity).getMasterTarget() == this)
    			return false;
    	}
    	return super.canAttack(targetEntity);
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
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("inWall")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }
}
