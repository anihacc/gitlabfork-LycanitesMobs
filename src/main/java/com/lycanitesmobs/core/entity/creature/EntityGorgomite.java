package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAvoidTargetGoal;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityGorgomite extends BaseCreatureEntity implements IMob {
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
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
        this.targetSelector.addGoal(this.nextSpecialTargetIndex++, new FindAvoidTargetGoal(this).setTargetClass(EntityManticore.class));
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
		if(this.gorgomiteSwarmLimit > 0 && this.countAllies(64D) < this.gorgomiteSwarmLimit) {
			float random = this.rand.nextFloat();
			if(random <= 0.25F)
				this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
		}
	}
	
    public void spawnAlly(double x, double y, double z) {
		BaseCreatureEntity minion = (BaseCreatureEntity) this.creatureInfo.createEntity(this.getEntityWorld());
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
		minion.setMinion(true);
		minion.applySubspecies(this.getSubspeciesIndex());
    	this.getEntityWorld().addEntity(minion);
        if(this.getAttackTarget() != null)
        	minion.setRevengeTarget(this.getAttackTarget());
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
