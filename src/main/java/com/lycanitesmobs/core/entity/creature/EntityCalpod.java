package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.WoodType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EntityCalpod extends BaseCreatureEntity implements IMob {
	private int swarmLimit = 5;
	private boolean griefing = true;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCalpod(EntityType<? extends EntityCalpod> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();
	}

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }

	@Override
	public boolean getDistanceSq(Vector3f vector3f) {
		return false;
	}

	@Override
	public void loadCreatureFlags() {
		this.swarmLimit = this.creatureInfo.getFlag("swarmLimit", this.swarmLimit);
		this.griefing = this.creatureInfo.getFlag("griefing", this.griefing);
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
		if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.getAttackTarget() instanceof PlayerEntity && this.updateTick % 60 == 0) {
			this.allyUpdate();
		}

		// Destroy Blocks:
		if(!this.getEntityWorld().isRemote)
			if(this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.griefing) {
				float distance = this.getAttackTarget().getDistance(this);
				if(distance <= this.getSize(Pose.STANDING).width + 1.0F)
					this.destroyAreaBlock((int)this.getPositionVec().getX(), (int)this.getPositionVec().getY(), (int)this.getPositionVec().getZ(), WoodType.class, true, 0);
			}
        
        super.livingTick();
    }
    
    // ========== Spawn Minions ==========
	public void allyUpdate() {
		if(this.getEntityWorld().isRemote)
			return;
		
		// Spawn Minions:
		if(this.swarmLimit > 0 && this.countAllies(64D) < this.swarmLimit) {
			float random = this.rand.nextFloat();
			if(random <= 0.125F)
				this.spawnAlly(this.getPositionVec().getX() - 2 + (random * 4), this.getPositionVec().getY(), this.getPositionVec().getZ() - 2 + (random * 4));
		}
	}
	
    public void spawnAlly(double x, double y, double z) {
		BaseCreatureEntity minion = (BaseCreatureEntity) this.creatureInfo.createEntity(this.getEntityWorld());
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
		minion.setMinion(true);
		minion.applyVariant(this.getVariantIndex());
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
    public float getFallResistance() {
        return 100;
    }
}
