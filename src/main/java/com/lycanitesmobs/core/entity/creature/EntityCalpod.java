package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.AttackTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.AvoidTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeTargetingGoal;
import net.minecraft.block.LogBlock;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCalpod extends EntityCreatureBase implements IMob, IGroupPrey {
	private int calpodSwarmLimit = 5; // TODO Creature flags.
	private boolean calpodGreifing = true;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCalpod(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();
	}

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new SwimmingGoal(this));
        this.field_70714_bg.addTask(2, new AvoidGoal(this).setNearSpeed(2.0D).setFarSpeed(1.5D).setNearDistance(5.0D).setFarDistance(10.0D));
        this.field_70714_bg.addTask(3, new AttackMeleeGoal(this).setLongMemory(true));
        this.field_70714_bg.addTask(6, new WanderGoal(this));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));
        this.field_70715_bh.addTask(0, new RevengeTargetingGoal(this).setHelpCall(true));
        this.field_70715_bh.addTask(1, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(2, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(3, new AvoidTargetingGoal(this).setTargetClass(IGroupHunter.class));
        this.field_70715_bh.addTask(3, new AvoidTargetingGoal(this).setTargetClass(IGroupPredator.class));
        this.field_70715_bh.addTask(3, new AvoidTargetingGoal(this).setTargetClass(IGroupAlpha.class));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
		if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.updateTick % 40 == 0) {
			this.allyUpdate();
		}

		// Destroy Blocks:
		if(!this.getEntityWorld().isRemote)
			if(this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.calpodGreifing) {
				float distance = this.getAttackTarget().getDistance(this);
				if(distance <= this.getSize(Pose.STANDING).width + 1.0F)
					this.destroyAreaBlock((int)this.posX, (int)this.posY, (int)this.posZ, LogBlock.class, true, 0);
			}
        
        super.livingTick();
    }
    
    // ========== Spawn Minions ==========
	public void allyUpdate() {
		if(this.getEntityWorld().isRemote)
			return;
		
		// Spawn Minions:
		if(this.calpodSwarmLimit > 0 && this.nearbyCreatureCount(this.getClass(), 64D) < this.calpodSwarmLimit) {
			float random = this.rand.nextFloat();
			if(random <= 0.125F)
				this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
		}
	}
	
    public void spawnAlly(double x, double y, double z) {
    	EntityCreatureBase minion = new EntityCalpod(this.getEntityWorld());
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
		minion.setMinion(true);
		minion.applySubspecies(this.getSubspeciesIndex());
		this.getEntityWorld().func_217376_c(minion);
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
    public float getFallResistance() {
        return 100;
    }
}
