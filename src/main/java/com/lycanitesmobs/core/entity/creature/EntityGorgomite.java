package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAvoidTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityGorgomite extends BaseCreatureEntity implements IMob, IGroupPrey {
	private int gorgomiteSwarmLimit = 10; // TODO Creature flags.
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGorgomite(EntityType<? extends EntityGorgomite> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();
     }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(2, new AvoidGoal(this).setNearSpeed(2.0D).setFarSpeed(1.5D).setNearDistance(5.0D).setFarDistance(10.0D));
        this.goalSelector.addGoal(3, new AttackMeleeGoal(this).setLongMemory(true));
        this.goalSelector.addGoal(6, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(1, new FindAttackTargetGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(2, new FindAttackTargetGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(3, new FindAvoidTargetGoal(this).setTargetClass(IGroupHunter.class));
        this.targetSelector.addGoal(3, new FindAvoidTargetGoal(this).setTargetClass(IGroupPredator.class));
        this.targetSelector.addGoal(3, new FindAvoidTargetGoal(this).setTargetClass(IGroupAlpha.class));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
		if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.updateTick % 20 == 0) {
			this.allyUpdate();
		}
        
        super.livingTick();
    }
    
    // ========== Spawn Minions ==========
	public void allyUpdate() {
		if(this.getEntityWorld().isRemote)
			return;
		
		// Spawn Minions:
		if(this.gorgomiteSwarmLimit > 0 && this.nearbyCreatureCount(this.getClass(), 64D) < this.gorgomiteSwarmLimit) {
			float random = this.rand.nextFloat();
			if(random <= 0.25F)
				this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
		}
	}
	
    public void spawnAlly(double x, double y, double z) {
    	LivingEntity minion = CreatureManager.getInstance().getCreature("gorgomite").createEntity(getEntityWorld());
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
    	if(minion instanceof BaseCreatureEntity) {
    		((BaseCreatureEntity)minion).setMinion(true);
    		((BaseCreatureEntity)minion).applySubspecies(this.getSubspeciesIndex());
    	}
    	this.getEntityWorld().addEntity(minion);
        if(this.getAttackTarget() != null)
        	minion.setRevengeTarget(this.getAttackTarget());
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	@Override
	public boolean canAttack(LivingEntity target) {
		if(target instanceof IGroupAlpha || target instanceof IGroupPredator)
			return false;
		return super.canAttack(target);
	}
    
    
    // ==================================================
    //                       Death
    // ==================================================
    @Override
    public void onDeath(DamageSource par1DamageSource) {
    	allyUpdate();
        super.onDeath(par1DamageSource);
    }


	// ==================================================
	//                     Abilities
	// ==================================================
	// ========== Movement ==========
	@Override
	public boolean canClimb() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }
}
