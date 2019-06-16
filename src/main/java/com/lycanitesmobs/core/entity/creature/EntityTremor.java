package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupRock;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityTremor extends EntityCreatureTameable implements IMob, IGroupRock {

	private AttackMeleeGoal meleeAttackAI;

	public int tremorExplosionStrength = 1; // TODO Creature flags.

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTremor(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;

		this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new SwimmingGoal(this));
        this.meleeAttackAI = new AttackMeleeGoal(this).setLongMemory(true);
        this.field_70714_bg.addTask(2, meleeAttackAI);
        this.field_70714_bg.addTask(3, this.aiSit);
        this.field_70714_bg.addTask(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(8, new WanderGoal(this));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));

        this.field_70715_bh.addTask(0, new OwnerRevengeTargetingGoal(this));
        this.field_70715_bh.addTask(1, new OwnerAttackTargetingGoal(this));
        this.field_70715_bh.addTask(2, new RevengeTargetingGoal(this).setHelpCall(true));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(6, new OwnerDefenseTargetingGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        if(!this.getEntityWorld().isRemote && this.getSubspeciesIndex() == 3 && !this.isPetType("familiar")) {
	    	// Random Charging:
	    	if(this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) > 1 && this.getRNG().nextInt(20) == 0) {
	    		if(this.posY - 1 > this.getAttackTarget().posY)
	    			this.leap(6.0F, -1.0D, this.getAttackTarget());
	    		else if(this.posY + 1 < this.getAttackTarget().posY)
	    			this.leap(6.0F, 1.0D, this.getAttackTarget());
	    		else
	    			this.leap(6.0F, 0D, this.getAttackTarget());
	    	}
        }

        // Particles:
        if(this.getEntityWorld().isRemote)
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().addParticle(ParticleTypes.SMOKE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
			}
    }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;
    	
    	// Explosion:
		int explosionStrength = Math.max(1, this.tremorExplosionStrength);
		Explosion.Mode damageTerrain = this.tremorExplosionStrength > 0 && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") ? Explosion.Mode.BREAK : Explosion.Mode.NONE;
		if(this.isPetType("familiar")) {
			explosionStrength = 1;
			damageTerrain = Explosion.Mode.NONE;
		}
		this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, explosionStrength, damageTerrain);

        return true;
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
   	//                     Immunities
   	// ==================================================
	// ========== Damage ==========
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
		if(source.isExplosion()) {
			this.heal(damage);
			return false;
		}
    	if(type.equals("cactus") || type.equals("inWall")) return false;
    	    return super.isInvulnerableTo(type, source, damage);
    }

	@Override
	public boolean isInvulnerableTo(Entity entity) {
		if(entity instanceof WitherEntity) {
			return false;
		}
		return super.isInvulnerableTo(entity);
	}

	@Override
	public boolean isPotionApplicable(EffectInstance effectInstance) {
    	if(effectInstance.getPotion() == Effects.field_82731_v) {
    		return false;
		}
		return super.isPotionApplicable(effectInstance);
	}
    
    @Override
    public boolean canBurn() { return false; }

    @Override
	public boolean canBeTargetedBy(LivingEntity entity) {
    	if(entity instanceof WitherEntity) {
    		return false;
		}
		return super.canBeTargetedBy(entity);
	}
}
