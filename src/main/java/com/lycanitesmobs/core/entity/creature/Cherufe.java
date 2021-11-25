package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomItemEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Cherufe extends BaseCreatureEntity implements Enemy {

    public int blockMeltingRadius = 2;
	
    public Cherufe(EntityType<? extends Cherufe> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.hasAttackSound = false;

        this.setupMob();

        this.setPathfindingMalus(BlockPathTypes.LAVA, 0F);
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

	@Override
    public void aiStep() {
        super.aiStep();

        if(!this.getCommandSenderWorld().isClientSide && this.isMoving() && this.tickCount % 5 == 0) {
        	int trailHeight = 1;
            int trailWidth = 1;
            if(this.isRareVariant())
                trailWidth = 3;
        	for(int y = 0; y < trailHeight; y++) {
        		Block block = this.getCommandSenderWorld().getBlockState(this.blockPosition().offset(0, y, 0)).getBlock();
        		if(block == Blocks.AIR || block == Blocks.FIRE || block == Blocks.SNOW || block == Blocks.TALL_GRASS || block == ObjectManager.getBlock("frostfire") || block == ObjectManager.getBlock("icefire")) {
                    if(trailWidth == 1)
                        this.getCommandSenderWorld().setBlockAndUpdate(this.blockPosition().offset(0, y, 0), Blocks.FIRE.defaultBlockState());
                    else
                        for(int x = -(trailWidth / 2); x < (trailWidth / 2) + 1; x++) {
                            for(int z = -(trailWidth / 2); z < (trailWidth / 2) + 1; z++) {
                                this.getCommandSenderWorld().setBlockAndUpdate(this.blockPosition().offset(x, y, z), Blocks.FIRE.defaultBlockState());
                            }
                        }
                }
        	}
		}

        if(!this.getCommandSenderWorld().isClientSide && this.isRareVariant() && this.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.blockMeltingRadius > 0 && this.tickCount % 10 == 0) {
            int range = this.blockMeltingRadius;
            for(int w = -((int)Math.ceil(this.getDimensions(Pose.STANDING).width) + range); w <= (Math.ceil(this.getDimensions(Pose.STANDING).width) + range); w++)
                for(int d = -((int)Math.ceil(this.getDimensions(Pose.STANDING).width) + range); d <= (Math.ceil(this.getDimensions(Pose.STANDING).width) + range); d++)
                    for(int h = 0; h <= Math.ceil(this.getDimensions(Pose.STANDING).height); h++) {
                        Block block = this.getCommandSenderWorld().getBlockState(this.blockPosition().offset(w, h, d)).getBlock();
                        if(block == Blocks.OBSIDIAN || block == Blocks.COBBLESTONE || block == Blocks.DIRT || block == Blocks.GRAVEL || block == Blocks.SAND) {
							BlockState blockState = Blocks.LAVA.defaultBlockState().setValue(LiquidBlock.LEVEL, 5);
                            this.getCommandSenderWorld().setBlockAndUpdate(this.blockPosition().offset(w, h, d), blockState);
                        }
                    }

            if(this.tickCount % 40 == 0) {
				ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("magma");
				if(projectileInfo != null) {
					BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getCommandSenderWorld(), this);
					projectile.setProjectileScale(2f);
					projectile.shoot((2 * this.getRandom().nextFloat()) - 1, this.getRandom().nextFloat(), (2 * this.getRandom().nextFloat()) - 1, 1.2F, 6.0F);
					this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
					this.getCommandSenderWorld().addFreshEntity(projectile);
				}
            }
        }
        
        if(this.getCommandSenderWorld().isClientSide) {
	        for(int i = 0; i < 2; ++i) {
	            this.getCommandSenderWorld().addParticle(ParticleTypes.FLAME, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	            this.getCommandSenderWorld().addParticle(ParticleTypes.DRIPPING_LAVA, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
	        if(this.tickCount % 10 == 0)
		        for(int i = 0; i < 2; ++i) {
		            this.getCommandSenderWorld().addParticle(ParticleTypes.FLAME, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
		        }
        }
    }

    @Override
    public float getAISpeedModifier() {
    	if(this.lavaContact())
    		return 2.0F;
    	return 1.0F;
    }
    
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
		int waterWeight = 10;
		BlockPos pos = new BlockPos(x, y, z);
        if(this.getCommandSenderWorld().getBlockState(pos).getBlock() == Blocks.LAVA)
        	return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        
        if(this.getTarget() != null)
        	return super.getBlockPathWeight(x, y, z);
        if(this.lavaContact())
			return -999999.0F;
		
		return super.getBlockPathWeight(x, y, z);
    }

	@Override
	public boolean isPushedByFluid() {
        return false;
    }

	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("magma", target, range, 0, new Vec3(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}

    @Override
    public boolean isAggressive() {
    	if(this.getAirSupply() <= -100)
    		return false;
    	return super.isAggressive();
    }
    
    

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

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFire())
    		return 0F;
    	else return super.getDamageModifier(damageSrc);
    }

    @Override
    public void applyDropEffects(CustomItemEntity entityitem) {
    	entityitem.setCanBurn(false);
    }

    public float getBrightness() {
        return 1.0F;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
}