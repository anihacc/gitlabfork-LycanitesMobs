package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomItemEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityLobber extends BaseCreatureEntity implements IMob {

    public int blockMeltingRadius = 2;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityLobber(EntityType<? extends EntityLobber> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.hasAttackSound = false;

        this.setupMob();

        this.setPathPriority(PathNodeType.LAVA, 0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(8.0F));
    }

	@Override
	public void loadCreatureFlags() {
		this.blockMeltingRadius = this.creatureInfo.getFlag("blockMeltingRadius", this.blockMeltingRadius);
	}
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Trail:
        if(!this.getEntityWorld().isRemote && this.isMoving() && this.ticksExisted % 5 == 0) {
        	int trailHeight = 1;
            int trailWidth = 1;
            if(this.isRareVariant())
                trailWidth = 3;
        	for(int y = 0; y < trailHeight; y++) {
        		Block block = this.getEntityWorld().getBlockState(this.getPosition().add(0, y, 0)).getBlock();
        		if(block == Blocks.AIR || block == Blocks.FIRE || block == Blocks.SNOW || block == Blocks.TALL_GRASS || block == ObjectManager.getBlock("frostfire") || block == ObjectManager.getBlock("icefire")) {
                    if(trailWidth == 1)
                        this.getEntityWorld().setBlockState(this.getPosition().add(0, y, 0), Blocks.FIRE.getDefaultState());
                    else
                        for(int x = -(trailWidth / 2); x < (trailWidth / 2) + 1; x++) {
                            for(int z = -(trailWidth / 2); z < (trailWidth / 2) + 1; z++) {
                                this.getEntityWorld().setBlockState(this.getPosition().add(x, y, z), Blocks.FIRE.getDefaultState());
                            }
                        }
                }
        	}
		}

        // Rare Subspecies Powers:
        if(!this.getEntityWorld().isRemote && this.isRareVariant() && this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.blockMeltingRadius > 0 && this.ticksExisted % 10 == 0) {

            // Melt Blocks:
            int range = this.blockMeltingRadius;
            for(int w = -((int)Math.ceil(this.getSize(Pose.STANDING).width) + range); w <= (Math.ceil(this.getSize(Pose.STANDING).width) + range); w++)
                for(int d = -((int)Math.ceil(this.getSize(Pose.STANDING).width) + range); d <= (Math.ceil(this.getSize(Pose.STANDING).width) + range); d++)
                    for(int h = 0; h <= Math.ceil(this.getSize(Pose.STANDING).height); h++) {
                        Block block = this.getEntityWorld().getBlockState(this.getPosition().add(w, h, d)).getBlock();
                        if(block == Blocks.OBSIDIAN || block == Blocks.COBBLESTONE || block == Blocks.DIRT || block == Blocks.GRAVEL || block == Blocks.SAND) {
							BlockState blockState = Blocks.LAVA.getDefaultState().with(FlowingFluidBlock.LEVEL, 5);
                            if(block == Blocks.OBSIDIAN)
                                blockState = Blocks.LAVA.getDefaultState();
                            this.getEntityWorld().setBlockState(this.getPosition().add(w, h, d), blockState);
                        }
                    }

            // Random Projectiles:
            if(this.ticksExisted % 40 == 0) {
				ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("magma");
				if(projectileInfo != null) {
					BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), this);
					projectile.setProjectileScale(2f);
					projectile.shoot((2 * this.getRNG().nextFloat()) - 1, this.getRNG().nextFloat(), (2 * this.getRNG().nextFloat()) - 1, 1.2F, 6.0F);
					this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
					this.getEntityWorld().addEntity(projectile);
				}
            }
        }
        
        // Particles:
        if(this.getEntityWorld().isRemote) {
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().addParticle(ParticleTypes.FLAME, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	            this.getEntityWorld().addParticle(ParticleTypes.DRIPPING_LAVA, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
	        if(this.ticksExisted % 10 == 0)
		        for(int i = 0; i < 2; ++i) {
		            this.getEntityWorld().addParticle(ParticleTypes.FLAME, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
		        }
        }
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(this.lavaContact())
    		return 2.0F;
    	return 1.0F;
    }
    
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
		int waterWeight = 10;
		BlockPos pos = new BlockPos(x, y, z);
        if(this.getEntityWorld().getBlockState(pos).getBlock() == Blocks.LAVA)
        	return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        
        if(this.getAttackTarget() != null)
        	return super.getBlockPathWeight(x, y, z);
        if(this.lavaContact())
			return -999999.0F;
		
		return super.getBlockPathWeight(x, y, z);
    }
	
	// Pushed By Water:
	@Override
	public boolean isPushedByWater() {
        return false;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("magma", target, range, 0, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}
    
    // ========== Is Aggressive ==========
    @Override
    public boolean isAggressive() {
    	if(this.getAir() <= -100)
    		return false;
    	return super.isAggressive();
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return !this.isRareVariant(); }
    
    @Override
    public boolean canBreatheUnderlava() {
        return true;
    }
    
    @Override
    public boolean canBreatheAir() {
        return true;
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 0F;
    	else return super.getDamageModifier(damageSrc);
    }
    
    
    // ==================================================
   	//                       Drops
   	// ==================================================
    // ========== Apply Drop Effects ==========
    /** Used to add effects or alter the dropped entity item. **/
    @Override
    public void applyDropEffects(CustomItemEntity entityitem) {
    	entityitem.setCanBurn(false);
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return 1.0F;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}
