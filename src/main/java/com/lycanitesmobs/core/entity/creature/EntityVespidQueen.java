package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.StayByHomeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityVespidQueen extends AgeableCreatureEntity implements IMob {
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
	public List<HiveExposedCoordinates> hiveExposedBlocks = new ArrayList<>();
	private int hiveExposedBlockCacheTime = 0;
	
	private int swarmLimit = 10;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVespidQueen(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.solidCollision = true;
        this.setupMob();
        
        this.canGrow = true;
        this.babySpawnChance = 0D;

        this.stepHeight = 1.0F;
        this.setAttackCooldownMax(10);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
        this.tasks.addTask(this.nextTravelGoalIndex, new StayByHomeGoal(this));

		this.targetTasks.addTask(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(this.getClass()));
		Class<? extends Entity> conbaType = CreatureManager.getInstance().getEntityClass("conba");
		if(conbaType != null)
			this.targetTasks.addTask(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(conbaType));
    }

	@Override
	public void loadCreatureFlags() {
		this.swarmLimit = this.creatureInfo.getFlag("swarmLimit", this.swarmLimit);
	}

	
	// ==================================================
  	//                       Spawning
  	// ==================================================
    @Override
    public boolean isPersistant() {
    	if(this.hasHome() && this.getEntityWorld().getDifficulty() != EnumDifficulty.PEACEFUL)
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
        
        // Don't Keep Infected Conbas Targeted:
        if(!this.getEntityWorld().isRemote && this.getAttackTarget() instanceof EntityConba) {
        	if(((EntityConba)this.getAttackTarget()).vespidInfection) {
        		this.setAttackTarget(null);
        	}
        }
    }

	@Override
	public boolean rollWanderChance() {
		return this.getRNG().nextDouble() <= 0.0008D;
	}
    
    // ========== Spawn Babies ==========
	public void allyUpdate() {
		if(this.getEntityWorld().isRemote)
			return;
		
		// Spawn Babies:
		if(this.swarmLimit > 0 && this.nearbyCreatureCount(CreatureManager.getInstance().getCreature("vespid").getEntityClass(), 32D) < this.swarmLimit) {
			float random = this.rand.nextFloat();
			if(random <= 0.05F) {
				EntityLivingBase minion = this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
				if(minion instanceof AgeableCreatureEntity) {
		    		((AgeableCreatureEntity)minion).setGrowingAge(((AgeableCreatureEntity) minion).growthTime);
		    	}
			}
		}
	}
	
    public EntityLivingBase spawnAlly(double x, double y, double z) {
		EntityLivingBase minion = CreatureManager.getInstance().getCreature("vespid").createEntity(this.getEntityWorld());
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
    	if(minion instanceof BaseCreatureEntity) {
    		((BaseCreatureEntity)minion).applySubspecies(this.getSubspeciesIndex());
    	}
    	this.getEntityWorld().spawnEntity(minion);
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
        IBlockState searchState = this.getEntityWorld().getBlockState(searchPos);
        Block searchBlock = searchState.getBlock();
        if(searchBlock != null)
            if(searchBlock == ObjectManager.getBlock("veswax"))
                return true;
        return false;
    }

    public boolean isHiveFloor(BlockPos searchPos) {
        IBlockState searchState = this.getEntityWorld().getBlockState(searchPos);
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
			this.hiveExposedBlocks = new ArrayList<HiveExposedCoordinates>();
			BlockPos hivePos = this.getHivePosition();
			int hiveMax = 28;

			for(int x = hivePos.getX() - hiveMax; x <= hivePos.getX() + hiveMax; x++) {
				for(int y = hivePos.getY() - hiveMax; y <= hivePos.getY() + hiveMax; y++) {
					for(int z = hivePos.getZ() - hiveMax; z <= hivePos.getZ() + hiveMax; z++) {
						BlockPos checkPos = new BlockPos(x, y, z);
						if(this.isHiveBlock(checkPos)) {
							IBlockState state = this.getEntityWorld().getBlockState(checkPos);
							Block block = state.getBlock();
							int orientationMeta = block.getMetaFromState(state);
							EnumFacing facing = EnumFacing.getFront(orientationMeta);

							if(facing.getFrontOffsetX() == 0) {
								if(!this.isHiveBlock(checkPos.add(-1, 0, 0)) && this.canPlaceBlockAt(checkPos.add(-1, 0, 0)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(-1, 0, 0), orientationMeta));
								if(!this.isHiveBlock(checkPos.add(1, 0, 0)) && this.canPlaceBlockAt(checkPos.add(1, 0, 0)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(1, 0, 0), orientationMeta));
							}

							if(facing.getFrontOffsetY() == 0) {
								if(!this.isHiveBlock(checkPos.add(0, -1, 0)) && this.canPlaceBlockAt(checkPos.add(0, -1, 0)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(0, -1, 0), orientationMeta));
								if(!this.isHiveBlock(checkPos.add(0, 1, 0)) && this.canPlaceBlockAt(checkPos.add(0, 1, 0)))
									this.hiveExposedBlocks.add(new HiveExposedCoordinates(block, checkPos.add(0, 1, 0), orientationMeta));
							}

							if(facing.getFrontOffsetZ() == 0) {
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
        IBlockState targetState = this.getEntityWorld().getBlockState(pos);
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
    public boolean canAttackEntity(EntityLivingBase targetEntity) {
    	if(targetEntity instanceof EntityConba)
        	if(((EntityConba)targetEntity).vespidInfection)
        		return false;
    	if(targetEntity instanceof EntityVespid) {
    		if(!((EntityVespid)targetEntity).hasMaster() || ((EntityVespid)targetEntity).getMasterTarget() == this)
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
