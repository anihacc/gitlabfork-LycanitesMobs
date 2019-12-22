package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityWraith extends TameableCreatureEntity implements IMob {

    protected int detonateTimer = -1;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityWraith(EntityType<? extends EntityWraith> entityType, World world) {
        super(entityType, world);
        
        // Setup:
		this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(2.0D).setLongMemory(false));
    }
    
    
    // ==================================================
   	//                     Updates
   	// ==================================================
    // ========== Living ==========
    @Override
    public void livingTick() {
        
        // Detonate:
        if(!this.getEntityWorld().isRemote) {
            if(this.detonateTimer == 0) {
                this.getEntityWorld().createExplosion(this, this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), 1, Explosion.Mode.BREAK);
                this.remove();
            }
            else if(this.detonateTimer > 0) {
                this.detonateTimer--;
                if(this.getEntityWorld().getBlockState(this.getPosition()).getMaterial().isSolid()) {
                    this.detonateTimer = 0;
                }
                else {
                    for (LivingEntity entity : this.getNearbyEntities(LivingEntity.class, null, 1)) {
                        if (this.getPlayerOwner() != null && entity == this.getPlayerOwner())
                            continue;
                        if (entity instanceof TameableCreatureEntity) {
                            TameableCreatureEntity entityCreature = (TameableCreatureEntity) entity;
                            if (entityCreature.getPlayerOwner() != null && entityCreature.getPlayerOwner() == this.getPlayerOwner())
                                continue;
                        }
                        this.detonateTimer = 0;
                        this.attackEntityAsMob(entity, 4);
                    }
                }
            }
        }

        // Particles:
        if(this.getEntityWorld().isRemote && this.detonateTimer <= 5) {
			for (int i = 0; i < 2; ++i) {
				this.getEntityWorld().addParticle(ParticleTypes.SMOKE, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
				this.getEntityWorld().addParticle(ParticleTypes.FLAME, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double) this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double) this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
			}
		}
        
        super.livingTick();
    }


    // ==================================================
    //                     Attacks
    // ==================================================
    public void chargeAttack() {
        this.leap(5, this.rotationPitch);
        this.detonateTimer = 10;
    }
	
	
	// ==================================================
  	//                     Abilities
  	// ==================================================
    // ========== Movement ==========
    public boolean isFlying() { return true; }
    
    
    // ==================================================
   	//                      Death
   	// ==================================================
    @Override
    public void onDeath(DamageSource par1DamageSource) {
		if(!this.getEntityWorld().isRemote && this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
			int explosionRadius = 1;
			if(this.subspecies != null)
				explosionRadius = 3;
			explosionRadius = Math.max(1, Math.round((float)explosionRadius * (float)this.sizeScale));
			this.getEntityWorld().createExplosion(this, this.getPositionVec().getX(), this.getPositionVec().getY(), this.getPositionVec().getZ(), explosionRadius, Explosion.Mode.BREAK);
		}
        super.onDeath(par1DamageSource);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return true; }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness() {
        return super.getBrightness();
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return super.getBrightnessForRender();
    }


	// ==================================================
	//                        NBT
	// ==================================================
	// ========== Read ===========
	@Override
	public void readAdditional(CompoundNBT nbtTagCompound) {
		super.readAdditional(nbtTagCompound);
		if(nbtTagCompound.contains("DetonateTimer")) {
			this.detonateTimer = nbtTagCompound.getInt("DetonateTimer");
		}
	}

	// ========== Write ==========
	@Override
	public void writeAdditional(CompoundNBT nbtTagCompound) {
		super.writeAdditional(nbtTagCompound);
		if(this.detonateTimer > -1) {
			nbtTagCompound.putInt("DetonateTimer", this.detonateTimer);
		}
	}
}
