package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.api.IGroupShadow;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.entity.projectile.EntityBloodleech;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityEpion extends EntityCreatureTameable implements IMob, IGroupShadow {
    
	public boolean epionGreifing = true; // TODO Creature flags.
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEpion(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = false;
        this.flySoundSpeed = 20;
        
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new SwimmingGoal(this));
        this.field_70714_bg.addTask(2, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(6.0F));
        this.field_70714_bg.addTask(3, this.aiSit);
        this.field_70714_bg.addTask(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(8, new WanderGoal(this));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));

        this.field_70715_bh.addTask(0, new OwnerRevengeTargetingGoal(this));
        this.field_70715_bh.addTask(1, new OwnerAttackTargetingGoal(this));
        this.field_70715_bh.addTask(2, new RevengeTargetingGoal(this));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class).setCheckSight(false));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(5, new AttackTargetingGoal(this).setTargetClass(IGroupPrey.class));
        this.field_70715_bh.addTask(6, new OwnerDefenseTargetingGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Sunlight Explosions:
        if(!this.getEntityWorld().isRemote && !this.isTamed()) {
        	if(!this.isFlying() && (this.onGround || this.isInWater()) && this.isAlive()) {
        		int explosionRadius = 2;
				if(this.subspecies != null)
					explosionRadius = 3;
				explosionRadius = Math.max(2, Math.round((float)explosionRadius * (float)this.sizeScale));
                if(this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.epionGreifing)
	        	    this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, explosionRadius, Explosion.Mode.BREAK);
	        	this.remove();
        	}
        }
        
        // Particles:
        if(this.getEntityWorld().isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().addParticle(ParticleTypes.WITCH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityBloodleech.class, target, range, 0, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() {
    	if(this.getEntityWorld().isRemote) return true;
    	if(this.daylightBurns() && this.getEntityWorld().isDaytime() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.epionGreifing) {
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
    public boolean daylightBurns() { return true; }
    
    @Override
    public float getFallResistance() { return 100; }


	// ==================================================
	//                       Visuals
	// ==================================================
	/** Returns this creature's main texture. Also checks for for subspecies. **/
	public ResourceLocation getTexture() {
		if(!"Vampire Bat".equals(this.getCustomName()))
			return super.getTexture();

		String textureName = this.getTextureName() + "_vampirebat";
		if(AssetManager.getTexture(textureName) == null)
			AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
		return AssetManager.getTexture(textureName);
	}
}
