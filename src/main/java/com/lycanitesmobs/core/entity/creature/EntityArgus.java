package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.entity.projectile.EntityChaosOrb;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityArgus extends EntityCreatureTameable implements IMob, IFusable {

	private int teleportTime = 60;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityArgus(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.setupMob();

        this.stepHeight = 1.0F;

        this.setAttackCooldownMax(40);
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
		this.field_70714_bg.addTask(1, new EntityAIFollowFuse(this).setLostDistance(16));
        this.field_70714_bg.addTask(2, new EntityAIStealth(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.field_70714_bg.addTask(3, new EntityAIAttackMelee(this).setLongMemory(true).setMaxChaseDistance(5.0F));
		this.field_70714_bg.addTask(4, new EntityAIAttackRanged(this).setSpeed(0.75D).setRange(16.0F).setMinChaseDistance(14.0F));
        this.field_70714_bg.addTask(5, this.aiSit);
        this.field_70714_bg.addTask(6, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(8, new EntityAIWander(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityAegis.class));
        this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
		this.field_70715_bh.addTask(7, new EntityAITargetFuse(this));
    }

    // ========== Set Size ==========
    @Override
    public void setSize(float width, float height) {
        if(this.getSubspeciesIndex() == 3) {
            super.setSize(width * 2, height * 2);
            return;
        }
        super.setSize(width, height);
    }

    @Override
    public double getRenderScale() {
        if(this.getSubspeciesIndex() == 3) {
            return this.sizeScale * 2;
        }
        return this.sizeScale;
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        /*/ Random Target Teleporting:
		if(!this.getEntityWorld().isRemote && this.hasAttackTarget()) {
			if(this.teleportTime-- <= 0) {
				this.teleportTime = 20 + this.getRNG().nextInt(20);
				BlockPos teleportPosition = this.getFacingPosition(this.getAttackTarget(), -this.getAttackTarget().width - 1D, 0);
				if(this.canTeleportTo(teleportPosition)) {
					this.playJumpSound();
					this.setPosition(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
				}
			}
		}*/
        
        // Particles:
        if(this.getEntityWorld().isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().spawnParticle(EnumParticleTypes.SUSPENDED_DEPTH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
    }

	/**
	 * Checks if this entity can teleport to the provided block position.
	 * @param pos The position to teleport to.
	 * @return True if it's safe to teleport.
	 */
	public boolean canTeleportTo(BlockPos pos) {
		for (int y = 0; y <= 1; y++) {
			BlockState blockState = this.getEntityWorld().getBlockState(pos.add(0, y, 0));
			if (blockState.isNormalCube())
				return false;
		}
		return true;
	}


	// ==================================================
	//                      Attacks
	// ==================================================
	// ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityChaosOrb.class, target, range, 0, new Vec3d(0, 0, 0), 0.6f, 1f, 1F);
		super.attackRanged(target, range);
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.5F;
	}
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isInvulnerableTo(type, source, damage);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
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
		if(fusable instanceof EntityCinder) {
			return EntityGrue.class;
		}
		if(fusable instanceof EntityJengu) {
			return EntityEechetik.class;
		}
		if(fusable instanceof EntityGeonach) {
			return EntityTremor.class;
		}
		if(fusable instanceof EntityDjinn) {
			return EntityWraith.class;
		}
		if(fusable instanceof EntityAegis) {
			return EntitySpectre.class;
		}
		return null;
	}
}
