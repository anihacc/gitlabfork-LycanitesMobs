package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAvoidTargetGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public class Gorgomite extends BaseCreatureEntity implements Enemy {
	private int swarmLimit = 5;
    
    public Gorgomite(EntityType<? extends Gorgomite> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
        this.targetSelector.addGoal(this.nextSpecialTargetIndex++, new FindAvoidTargetGoal(this).setTargetClass(Balayang.class));
    }

	@Override
	public void loadCreatureFlags() {
		this.swarmLimit = this.creatureInfo.getFlag("swarmLimit", this.swarmLimit);
	}

	@Override
    public void aiStep() {
		if(!this.getCommandSenderWorld().isClientSide && this.hasAttackTarget() && this.getTarget() instanceof Player && this.updateTick % 60 == 0) {
			this.allyUpdate();
		}
        
        super.aiStep();
    }

	public void allyUpdate() {
		if(this.getCommandSenderWorld().isClientSide)
			return;

		if(this.swarmLimit > 0 && this.countAllies(64D) < this.swarmLimit) {
			float random = this.random.nextFloat();
			if(random <= 0.25F)
				this.spawnAlly(this.position().x() - 2 + (random * 4), this.position().y(), this.position().z() - 2 + (random * 4));
		}
	}
	
    public void spawnAlly(double x, double y, double z) {
		BaseCreatureEntity minion = (BaseCreatureEntity) this.creatureInfo.createEntity(this.getCommandSenderWorld());
    	minion.moveTo(x, y, z, this.random.nextFloat() * 360.0F, 0.0F);
		minion.setMinion(true);
		minion.applyVariant(this.getVariantIndex());
    	this.getCommandSenderWorld().addFreshEntity(minion);
        if(this.getTarget() != null)
        	minion.setLastHurtByMob(this.getTarget());
    }
    

    @Override
    public void die(DamageSource par1DamageSource) {
    	allyUpdate();
        super.die(par1DamageSource);
    }



	@Override
	public boolean canClimb() { return true; }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	return super.isVulnerableTo(type, source, damage);
    }
}
