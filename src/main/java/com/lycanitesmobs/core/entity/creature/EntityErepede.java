package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityErepede extends RideableCreatureEntity {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityErepede(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        this.attackCooldownMax = 10;
        this.setupMob();
        
        // Stats:
        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(6.0F));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(this.hasRiderTarget()) {
            IBlockState blockState = this.getEntityWorld().getBlockState(this.getPosition().add(0, -1, 0));
            if (blockState.getMaterial() == Material.SAND || (blockState.getMaterial() == Material.AIR && this.getEntityWorld().getBlockState(this.getPosition().add(0, -2, 0)).getMaterial() == Material.SAND))
                return 1.8F;
        }
    	return 1.0F;
    }
    
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.9D;
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    public void mountAbility(Entity rider) {
    	if(this.getEntityWorld().isRemote)
    		return;
    	
    	if(this.abilityToggled)
    		return;
    	if(this.getStamina() < this.getStaminaCost())
    		return;

		if(rider instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)rider;
			ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("mudshot");
			if(projectileInfo != null) {
				BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), player);
				this.getEntityWorld().spawnEntity(projectile);
				this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
				this.triggerAttackCooldown();
			}
		}
    	
    	this.applyStaminaCost();
    }
    
    public float getStaminaCost() {
    	return 5;
    }
    
    public int getStaminaRecoveryWarmup() {
    	return 0;
    }
    
    public float getStaminaRecoveryMax() {
    	return 1.0F;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("mudshot", target, range, 0, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.BagSize; }




    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	return super.isDamageTypeApplicable(type, source, damage);
    }
    
    @Override
    public float getFallResistance() {
    	return 10;
    }


	// ==================================================
	//                     Pet Control
	// ==================================================
	public boolean petControlsEnabled() { return true; }
}
