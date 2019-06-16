package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.entity.projectile.EntityWhirlwind;
import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityDjinn extends EntityCreatureTameable implements IMob, IFusable {

	public float fireDamageAbsorbed = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityDjinn(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(1, new EntityAIFollowFuse(this).setLostDistance(16));
        this.field_70714_bg.addTask(2, new EntityAIAttackRanged(this).setSpeed(0.75D).setRange(16.0F).setMinChaseDistance(8.0F));
        this.field_70714_bg.addTask(3, this.aiSit);
        this.field_70714_bg.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(8, new EntityAIWander(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupFire.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
        this.field_70715_bh.addTask(7, new EntityAITargetFuse(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

		if(!this.getEntityWorld().isRemote) {

			// Environmental Transformation:
			if(!this.isTamed()) {
				if (this.fireDamageAbsorbed >= 10) {
					this.transform(EntityZephyr.class, null, false);
				}
			}
		}
        
        // Particles:
        //if(this.getEntityWorld().isRemote)
            //this.getEntityWorld().spawnParticle(EnumParticleTypes.SNOWBALL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
        return super.canAttackClass(targetClass);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
    	int projectileCount = 10;
    	for(int i = 0; i < projectileCount; i++) {
    		this.fireProjectile(EntityWhirlwind.class, target, range, (360 / projectileCount) * i, new Vec3d(0, 0, 0), 0.6f, 2f, 1F);
		}
        super.attackRanged(target, range);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return false; }


    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
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
        if(type.equals("lightningBolt") && !this.isTamed()) {
        	this.transform(EntityZephyr.class, null, false);
        	return false;
		}
        return super.isInvulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }


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
		if(fusable instanceof EntityCinder) {
			return EntityZephyr.class;
		}
        if(fusable instanceof EntityJengu) {
            return EntityReiver.class;
        }
        if(fusable instanceof EntityGeonach) {
            return EntityBanshee.class;
        }
        if(fusable instanceof EntityAegis) {
            return EntitySylph.class;
        }
		if(fusable instanceof EntityArgus) {
			return EntityWraith.class;
		}
        return null;
    }
}
