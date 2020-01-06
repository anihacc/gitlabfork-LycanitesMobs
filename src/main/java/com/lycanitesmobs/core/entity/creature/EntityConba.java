package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.AvoidGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAvoidTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityConba extends TameableCreatureEntity implements IMob {
	AttackRangedGoal aiAttackRanged;
	AttackMeleeGoal aiAttackMelee;
	AvoidGoal aiAvoid;
	public boolean vespidInfection = false;
	public int vespidInfectionTime = 0;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityConba(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
		this.aiAttackMelee = new AttackMeleeGoal(this).setLongMemory(true).setEnabled(false);
		this.tasks.addTask(this.nextPriorityGoalIndex++, this.aiAttackMelee); // Melee is a priority as it is used when infected.

		super.initEntityAI();

		//this.aiAvoid = new AvoidGoal(this).setNearSpeed(1.5D).setFarSpeed(1.3D).setNearDistance(4.0D).setFarDistance(8.0D);
		//this.tasks.addTask(this.nextPriorityGoalIndex++, this.aiAvoid);

		this.aiAttackRanged = new AttackRangedGoal(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(10.0F).setChaseTime(-1);
		this.tasks.addTask(this.nextCombatGoalIndex++, this.aiAttackRanged);

        //this.targetTasks.addTask(this.nextSpecialTargetIndex++, new FindAvoidTargetGoal(this).setTargetClass(EntityPlayer.class).setTameTargetting(false));
        //this.targetTasks.addTask(this.nextSpecialTargetIndex++, new FindAvoidTargetGoal(this).setTargetClass(EntityVillager.class));
    }
    
    
    // ==================================================
    //                       Name
    // ==================================================
    /** Returns the species name of this entity. **/
	@Override
    public String getSpeciesName() {
		String infection = "";
		if(this.vespidInfection) {
			String entityName = this.creatureInfo.getName();
	    	if(entityName != null) {
				infection = LanguageManager.translate("entity." + this.creatureInfo.modInfo.modid + "." + entityName + ".infected") + " ";
			}
		}
    	return super.getSpeciesName();
    }
    
    public String getTextureName() {
    	if(this.vespidInfection)
    		return super.getTextureName() + "_infected";
    	return super.getTextureName();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Random Leaping:
        if(this.onGround && !this.getEntityWorld().isRemote) {
        	if(this.hasAvoidTarget()) {
        		if(this.rand.nextInt(10) == 0)
        			this.leap(1.0F, 0.6D, this.getAttackTarget());
        	}
        	else {
        		if(this.rand.nextInt(50) == 0 && this.isMoving())
        			this.leap(1.0D, 0.6D);
        	}
        }
        
        // Infected AI:
        if(!this.getEntityWorld().isRemote) {
			// The Swarm:
			if(!this.vespidInfection && "theswarm".equals(this.spawnEventType)) {
				this.vespidInfection = true;
			}

            if (this.vespidInfection && !this.getEntityWorld().isRemote) {
                this.aiAttackMelee.setEnabled(true);
                this.aiAttackRanged.setEnabled(false);
                if (this.vespidInfectionTime++ >= 60 * 20) {
                    this.spawnVespidSwarm();
                    this.setDead();
                }
            } else {
                this.aiAttackMelee.setEnabled(false);
                this.aiAttackRanged.setEnabled(true);
            }
        }
        
        // Infected Visuals
        if(this.getEntityWorld().isRemote) {
        	this.vespidInfection = this.extraAnimation01();
        	if(this.vespidInfection) {
    	        for(int i = 0; i < 2; ++i) {
    	            this.getEntityWorld().spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
    	        }
        	}
        }
    }
	
	// ========== AI Update ==========
	@Override
    public void updateAITasks() {
        // Avoid Attack Target:
        if(!this.getEntityWorld().isRemote) {
	        if(this.getAttackTarget() != null && this.getAttackTarget() != this.getAvoidTarget())
	        	this.setAvoidTarget(this.getAttackTarget());
        }
		
        super.updateAITasks();
	}

	@Override
	public boolean shouldCreatureGroupFlee(EntityLivingBase target) {
		if(this.isTamed())
			return false;
		return super.shouldCreatureGroupFlee(target);
	}
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	@Override
	public boolean canAttackEntity(EntityLivingBase target) {
		if(target instanceof EntityVespid || target instanceof EntityVespidQueen)
			return false;
		return super.canAttackEntity(target);
	}
    
    // ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile("poop", target, range, 0, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}
    
    
    // ==================================================
   	//                      Death
   	// ==================================================
    @Override
    public void onDeath(DamageSource damageSource) {
		if(!this.getEntityWorld().isRemote && this.vespidInfection)
			this.spawnVespidSwarm();
        super.onDeath(damageSource);
    }
    
    public void spawnVespidSwarm() {
    	int j = 2 + this.rand.nextInt(5) + getEntityWorld().getDifficulty().getDifficultyId() - 1;
        for(int k = 0; k < j; ++k) {
            float f = ((float)(k % 2) - 0.5F) * this.width / 4.0F;
            float f1 = ((float)(k / 2) - 0.5F) * this.width / 4.0F;
            EntityVespid vespid = (EntityVespid)CreatureManager.getInstance().getCreature("vespid").createEntity(this.getEntityWorld());
            vespid.setLocationAndAngles(this.posX + (double)f, this.posY + 0.5D, this.posZ + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
            vespid.applySubspecies(this.getSubspeciesIndex());
            vespid.setGrowingAge(vespid.growthTime);
            vespid.spawnEventType = this.spawnEventType;
            this.getEntityWorld().spawnEntity(vespid);
            if(this.getAttackTarget() != null)
            	vespid.setRevengeTarget(this.getAttackTarget());
        }
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }

    
    // ==================================================
    //                      Abilities
    // ==================================================
    // ========== Extra Animations ==========
    /** An additional animation boolean that is passed to all clients through the animation mask. **/
    public boolean extraAnimation01() {
    	if(!this.getEntityWorld().isRemote)
    		return this.vespidInfection;
	    else
	    	return this.extraAnimation01;
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public float getFallResistance() {
    	return 100;
    }
    
    
    // ==================================================
    //                        NBT
    // ==================================================
   	// ========== Read ===========
    /** Used when loading this mob from a saved chunk. **/
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
    	super.readFromNBT(nbtTagCompound);
        
        if(nbtTagCompound.hasKey("VespidInfection")) {
        	this.vespidInfection = nbtTagCompound.getBoolean("VespidInfection");
        }
        if(nbtTagCompound.hasKey("VespidInfectionTime")) {
        	this.vespidInfectionTime = nbtTagCompound.getInteger("VespidInfectionTime");
        }
    }
    
    // ========== Write ==========
    /** Used when saving this mob to a chunk. **/
    @Override
    public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
        super.writeEntityToNBT(nbtTagCompound);
    	nbtTagCompound.setBoolean("VespidInfection", this.vespidInfection);
    	if(this.vespidInfection)
        	nbtTagCompound.setInteger("VespidInfectionTime", this.vespidInfectionTime);
    }
}
