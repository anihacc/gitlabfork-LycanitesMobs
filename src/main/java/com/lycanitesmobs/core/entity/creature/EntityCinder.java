package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.entity.RapidFireProjectileEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class EntityCinder extends TameableCreatureEntity implements IMob, IFusable {

	public float inWallDamageAbsorbed = 0;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCinder(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.spawnsInBlock = false;
        this.hasAttackSound = false;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setStaminaTime(100).setRange(5.0F).setMinChaseDistance(2.0F));
    }
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Suffocation Transform:
		if(!this.getEntityWorld().isRemote) {
			if(this.inWallDamageAbsorbed >= 10) {
				this.transform(CreatureManager.getInstance().getEntityClass("volcan"), null, false);
			}
		}
        
        // Particles:
        if(this.getEntityWorld().isRemote) {
			for (int i = 0; i < 2; ++i) {
				this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
				this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
			}
		}
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
    	// Type:
    	List<RapidFireProjectileEntity> projectiles = new ArrayList<>();
		ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("ember");
		if(projectileInfo == null) {
			return;
		}
    	
    	RapidFireProjectileEntity projectileEntry = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectiles.add(projectileEntry);
    	
    	RapidFireProjectileEntity projectileEntry2 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectileEntry2.offsetX += 1.0D;
    	projectiles.add(projectileEntry2);
    	
    	RapidFireProjectileEntity projectileEntry3 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectileEntry3.offsetX -= 1.0D;
    	projectiles.add(projectileEntry3);
    	
    	RapidFireProjectileEntity projectileEntry4 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectileEntry4.offsetZ += 1.0D;
    	projectiles.add(projectileEntry4);
    	
    	RapidFireProjectileEntity projectileEntry5 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectileEntry5.offsetZ -= 1.0D;
    	projectiles.add(projectileEntry5);
    	
    	RapidFireProjectileEntity projectileEntry6 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectileEntry6.offsetY += 1.0D;
    	projectiles.add(projectileEntry6);
    	
    	RapidFireProjectileEntity projectileEntry7 = new RapidFireProjectileEntity(projectileInfo, this.getEntityWorld(), this, 15, 3);
    	projectileEntry7.offsetY -= 1.0D;
    	projectiles.add(projectileEntry7);
    	
    	for(RapidFireProjectileEntity projectile : projectiles) {
	        projectile.setProjectileScale(1f);
	    	
	    	// Y Offset:
	    	projectile.posY -= this.height / 4;
	    	
	    	// Accuracy:
	    	float accuracy = 1.0F * (this.getRNG().nextFloat() - 0.5F);
	    	
	    	// Set Velocities:
	        double d0 = target.posX - this.posX + accuracy;
	        double d1 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D - projectile.posY + accuracy;
	        double d2 = target.posZ - this.posZ + accuracy;
	        float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2F;
	        float velocity = 0.6F;
	        projectile.shoot(d0, d1 + (double)f1, d2, velocity, 6.0F);
	        
	        // Launch:
	        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	        this.getEntityWorld().spawnEntity(projectile);
    	}
    	
        super.attackRanged(target, range);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


	// ==================================================
	//                     Equipment
	// ==================================================
	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }



	// ==================================================
   	//                     Immunities
   	// ==================================================
	@Override
	public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
		/*if(type.equals("inWall")) {
			this.inWallDamageAbsorbed += damage;
			return false;
		}*/
		return super.isDamageTypeApplicable(type, source, damage);
	}
    
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return true; }
    
    
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
    public void applyDropEffects(EntityItemCustom entityitem) {
    	entityitem.setCanBurn(false);
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    @Override
    public float getBrightness() {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }


	// ==================================================
	//                      Fusion
	// ==================================================
	protected IFusable fusionTarget;

	@Override
	public IFusable getFusionTarget() {
		return this.fusionTarget;
	}

	@Override
	public void setFusionTarget(IFusable fusionTarget) {
		this.fusionTarget = fusionTarget;
	}

	@Override
	public Class getFusionClass(IFusable fusable) {
		if(fusable instanceof EntityJengu) {
			return CreatureManager.getInstance().getEntityClass("xaphan");
		}
		if(fusable instanceof EntityGeonach) {
			return CreatureManager.getInstance().getEntityClass("volcan");
		}
		if(fusable instanceof EntityDjinn) {
			return CreatureManager.getInstance().getEntityClass("zephyr");
		}
		if(fusable instanceof EntityAegis) {
			return CreatureManager.getInstance().getEntityClass("wisp");
		}
		if(fusable instanceof EntityArgus) {
			return CreatureManager.getInstance().getEntityClass("grue");
		}
		return null;
	}
}
