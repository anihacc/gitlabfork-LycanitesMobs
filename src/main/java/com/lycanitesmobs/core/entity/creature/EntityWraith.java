package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityWraith extends EntityCreatureTameable implements IMob, IGroupDemon {

    protected int detonateTimer = -1;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityWraith(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.hasAttackSound = true;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(2, new EntityAIAttackMelee(this).setSpeed(2.0D).setLongMemory(false));
        this.tasks.addTask(6, new EntityAIWander(this).setSpeed(1.0D).setPauseRate(0));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.targetTasks.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
    }
    
    
    // ==================================================
   	//                     Updates
   	// ==================================================
    // ========== Living ==========
    @Override
    public void onLivingUpdate() {
        
        // Detonate:
        if(!this.getEntityWorld().isRemote) {
            if(this.detonateTimer == 0) {
                this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, 1, true);
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
                        if (entity instanceof EntityCreatureTameable) {
                            EntityCreatureTameable entityCreature = (EntityCreatureTameable) entity;
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
				this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
				this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
			}
		}
        
        super.onLivingUpdate();
    }


    // ==================================================
    //                     Attacks
    // ==================================================
    public void chargeAttack() {
        this.leap(5, this.rotationPitch);
        this.detonateTimer = 10;
    }

	// ========== Set Attack Target ==========
	@Override
	public boolean canAttackClass(Class targetClass) {
		if(targetClass.isAssignableFrom(IGroupDemon.class))
			return false;
		return super.canAttackClass(targetClass);
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
		if(!this.getEntityWorld().isRemote && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
			int explosionRadius = 1;
			if(this.subspecies != null)
				explosionRadius = 3;
			explosionRadius = Math.max(1, Math.round((float)explosionRadius * (float)this.sizeScale));
			this.getEntityWorld().createExplosion(this, this.posX, this.posY, this.posZ, explosionRadius, true);
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
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return super.getBrightnessForRender();
    }


	// ==================================================
	//                        NBT
	// ==================================================
	// ========== Read ===========
	@Override
	public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
		super.readEntityFromNBT(nbtTagCompound);
		if(nbtTagCompound.contains("DetonateTimer")) {
			this.detonateTimer = nbtTagCompound.getInt("DetonateTimer");
		}
	}

	// ========== Write ==========
	@Override
	public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
		super.writeEntityToNBT(nbtTagCompound);
		if(this.detonateTimer > -1) {
			nbtTagCompound.putInt("DetonateTimer", this.detonateTimer);
		}
	}
}
