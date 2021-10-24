package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class Reaper extends TameableCreatureEntity implements Enemy {
    
    public Reaper(EntityType<? extends Reaper> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEAD;
        this.hasAttackSound = false;
        this.setupMob();

        this.noPhysics = true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(0.75F).setCheckSight(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));

        this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(EntityType.PHANTOM));
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(this.getCommandSenderWorld().isClientSide)
	        for(int i = 0; i < 2; ++i) {
	            this.getCommandSenderWorld().addParticle(ParticleTypes.WITCH, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
    }

    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("spectralbolt", target, range, 0, new Vec3(0, 0, 0), 1.2f, 2f, 1F);
        super.attackRanged(target, range);
    }

    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean useDirectNavigator() {
        return true;
    }

    @Override
    public boolean hasLineOfSight(Entity target) {
        return true;
    }

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("inWall")) return false;
    	return super.isVulnerableTo(type, source, damage);
    }

    public boolean daylightBurns() { return super.daylightBurns(); }

    @Override
    public boolean canBurn() {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
    	if(this.hasAttackTarget()) {
    		if(this.getTarget() instanceof Player)
    			if("jbams".equalsIgnoreCase((this.getTarget()).getName().toString()))
    				return ObjectManager.getSound(this.creatureInfo.getName() + "_say_jon");
    	}
        if(this.isTamed() && this.getOwner() != null) {
            if("jbams".equalsIgnoreCase(this.getOwnerName().getString()))
                return ObjectManager.getSound(this.creatureInfo.getName() + "_say_jon");
        }
    	return super.getAmbientSound();
    }

    @Override
    public ResourceLocation getTexture(String suffix) {
        if(!this.hasCustomName() || !"Satan Claws".equals(this.getCustomName().getString()))
            return super.getTexture(suffix);

        String textureName = this.getTextureName() + "_satanclaws";
        if(!"".equals(suffix)) {
            textureName += "_" + suffix;
        }
        if(TextureManager.getTexture(textureName) == null)
            TextureManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
        return TextureManager.getTexture(textureName);
    }
}
