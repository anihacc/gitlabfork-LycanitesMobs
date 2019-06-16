package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupRock;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.entity.projectile.EntityCrystalShard;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityVapula extends EntityCreatureTameable implements IMob, IGroupRock {

	public int vapulaBlockBreakRadius = 0; // TODO Creature flags.
	public float fireDamageAbsorbed = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVapula(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();

        this.stepHeight = 1.0F;
        this.attackPhaseMax = 8;
        this.setAttackCooldownMax(60);
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new SwimmingGoal(this));
		this.field_70714_bg.addTask(2, new AttackMeleeGoal(this).setLongMemory(false).setMaxChaseDistanceSq(3.0F));
		this.field_70714_bg.addTask(3, new AttackRangedGoal(this).setSpeed(0.75D).setRange(18.0F).setMinChaseDistance(10.0F).setCheckSight(false));
        this.field_70714_bg.addTask(4, this.aiSit);
        this.field_70714_bg.addTask(5, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(8, new WanderGoal(this));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));

        this.field_70715_bh.addTask(0, new OwnerRevengeTargetingGoal(this));
        this.field_70715_bh.addTask(1, new OwnerAttackTargetingGoal(this));
        this.field_70715_bh.addTask(2, new RevengeTargetingGoal(this).setHelpCall(true));
        this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(SilverfishEntity.class));
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

        if(!this.getEntityWorld().isRemote) {
			if (this.getSubspeciesIndex() == 3 && !this.isPetType("familiar")){
				// Random Charging:
				if (this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) > 1 && this.getRNG().nextInt(20) == 0) {
					if (this.posY - 1 > this.getAttackTarget().posY)
						this.leap(6.0F, -1.0D, this.getAttackTarget());
					else if (this.posY + 1 < this.getAttackTarget().posY)
						this.leap(6.0F, 1.0D, this.getAttackTarget());
					else
						this.leap(6.0F, 0D, this.getAttackTarget());
					if (this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.vapulaBlockBreakRadius > -1 && !this.isTamed()) {
						this.destroyArea((int) this.posX, (int) this.posY, (int) this.posZ, 10, true, this.vapulaBlockBreakRadius);
					}
				}
			}
		}

        // Particles:
		if(this.getEntityWorld().isRemote) {
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().addParticle(new BlockParticleData(ParticleTypes.BLOCK, Blocks.DIAMOND_BLOCK.getDefaultState()),
						this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
						this.posY + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height,
						this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width,
						0.0D, 0.0D, 0.0D);
			}
		}
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
        // Silverfish Extermination:
        if(this.hasAttackTarget() && this.getAttackTarget() instanceof SilverfishEntity)
            return 4.0F;
        return super.getAISpeedModifier();
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;

        // Silverfish Extermination:
        if(target instanceof SilverfishEntity) {
            target.remove();
        }
        return true;
    }

	// ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityCrystalShard.class, target, range, 0, new Vec3d(0, 0, 0), 0.6f, 2f, 1F);
		this.nextAttackPhase();
		super.attackRanged(target, range);
	}

	@Override
	public int getRangedCooldown() {
		if(this.getAttackPhase() < 7)
			return super.getRangedCooldown() / 24;
		return super.getRangedCooldown();
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
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    @Override
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.getTrueSource() != null) {
            // Silverfish Extermination:
            if(damageSrc.getTrueSource() instanceof SilverfishEntity) {
                return 0F;
            }

            // Pickaxe Damage:
    		Item heldItem = null;
    		if(damageSrc.getTrueSource() instanceof LivingEntity) {
                LivingEntity entityLiving = (LivingEntity)damageSrc.getTrueSource();
	    		if(!entityLiving.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
	    			heldItem = entityLiving.getHeldItem(Hand.MAIN_HAND).getItem();
	    		}
    		}
    		if(ObjectLists.isPickaxe(heldItem))
                return 4.0F;
    	}
    	return 1.0F;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall"))
    		return false;
    	if(source.isFireDamage()) {
    		this.fireDamageAbsorbed += damage;
    		return false;
		}
		return super.isInvulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBurn() {
    	return false;
    }
}
