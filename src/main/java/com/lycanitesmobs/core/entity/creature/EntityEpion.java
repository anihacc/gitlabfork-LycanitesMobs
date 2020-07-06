package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EntityEpion extends RideableCreatureEntity implements IMob {
    
	public boolean griefing = true;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEpion(EntityType<? extends EntityEpion> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = false;
        this.flySoundSpeed = 20;
        
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(6.0F));
    }

	@Override
	public void loadCreatureFlags() {
		this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
	}

	@Override
	public float getStafeSpeed() {
		return 1F;
	}
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Sunlight Explosions:
        if(!this.getEntityWorld().isRemote && !this.isTamed() && !this.isMinion() && !this.isRareVariant()) {
        	if(!this.isFlying() && (this.onGround || this.isInWater()) && this.isAlive()) {
        		int explosionRadius = 2;
				if(this.subspecies != null)
					explosionRadius = 3;
				explosionRadius = Math.max(2, Math.round((float)explosionRadius * (float)this.sizeScale));
                if(this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.griefing)
	        	    this.getEntityWorld().createExplosion(this, this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), explosionRadius, Explosion.Mode.NONE);
				else
					this.getEntityWorld().createExplosion(this, this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), explosionRadius, Explosion.Mode.BREAK);
	        	this.remove();
        	}
        }
        
        // Particles:
        if(this.getEntityWorld().isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().addParticle(ParticleTypes.WITCH, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
    }

	@Override
	public boolean canEntityBeSeen(Entity target) {
		if(this.isRareVariant()) {
			return true;
		}
		return super.canEntityBeSeen(target);
	}
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("bloodleech", target, range, 0, new Vector3d(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() {
    	if(this.getEntityWorld().isRemote) return true;
    	if(this.daylightBurns() && this.getEntityWorld().isDaytime() && this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.griefing) {
    		float brightness = this.getBrightness();
        	if(brightness > 0.5F && this.getEntityWorld().canBlockSeeSky(this.getPosition()))
        		return false;
    	}
        return true;
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    /** Returns true if this mob should be damaged by the sun. **/
    @Override
	public boolean daylightBurns() {
		return !this.isMinion() && !this.hasMaster() && !this.isTamed() && !this.isRareVariant();
	}
    
    @Override
    public float getFallResistance() { return 100; }


	// ==================================================
	//                   Mount Ability
	// ==================================================
	public void mountAbility(Entity rider) {
		if(this.getEntityWorld().isRemote)
			return;

		if(this.getStamina() < this.getStaminaCost())
			return;

		if(rider instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity)rider;
			ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("bloodleech");
			if(projectileInfo != null) {
				BaseProjectileEntity projectile = projectileInfo.createProjectile(this.getEntityWorld(), player);
				this.getEntityWorld().addEntity(projectile);
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
	//                       Visuals
	// ==================================================
	/** Returns this creature's main texture. Also checks for for subspecies. **/
	public ResourceLocation getTexture() {
		if(!this.hasCustomName() || !"Vampire Bat".equals(this.getCustomName().getString()))
			return super.getTexture();

		String textureName = this.getTextureName() + "_vampirebat";
		if(TextureManager.getTexture(textureName) == null)
			TextureManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
		return TextureManager.getTexture(textureName);
	}
}
