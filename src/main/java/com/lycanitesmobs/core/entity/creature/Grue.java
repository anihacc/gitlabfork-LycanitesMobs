package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Grue extends TameableCreatureEntity implements Enemy {
    
	private int teleportTime = 60;
	
    public Grue(EntityType<? extends Grue> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }

	@Override
    public void aiStep() {
        super.aiStep();
        
        if(!this.getCommandSenderWorld().isClientSide && this.hasAttackTarget()) {
	        if(this.teleportTime-- <= 0) {
	        	this.teleportTime = 60 + this.getRandom().nextInt(40);
        		BlockPos teleportPosition = this.getFacingPosition(this.getTarget(), -this.getTarget().getDimensions(Pose.STANDING).width - 1D, 0);
        		if(this.canTeleportTo(teleportPosition)) {
					this.playJumpSound();
					this.setPos(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
				}
	        }
        }

        if(this.getCommandSenderWorld().isClientSide)
	        for(int i = 0; i < 2; ++i) {
	            this.getCommandSenderWorld().addParticle(ParticleTypes.WITCH, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
    }

	/**
	 * Checks if this entity can teleport to the provided block position.
	 * @param pos The position to teleport to.
	 * @return True if it's safe to teleport.
	 */
	public boolean canTeleportTo(BlockPos pos) {
		for (int y = 0; y <= 1; y++) {
			BlockState blockState = this.getCommandSenderWorld().getBlockState(pos.offset(0, y, 0));
			if (blockState.canOcclude())
				return false;
		}
        return true;
    }

    @Override
    public boolean canStealth() {
    	if(this.getCommandSenderWorld().isClientSide) return false;
    	return this.testLightLevel() <= 0;
    }
    
    @Override
    public void startStealth() {
    	if(this.getCommandSenderWorld().isClientSide) {
            ParticleOptions particle = ParticleTypes.WITCH;
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            for(int i = 0; i < 100; i++)
            	this.getCommandSenderWorld().addParticle(particle, this.position().x() + (double)(this.random.nextFloat() * this.getDimensions(Pose.STANDING).width * 2.0F) - (double)this.getDimensions(Pose.STANDING).width, this.position().y() + 0.5D + (double)(this.random.nextFloat() * this.getDimensions(Pose.STANDING).height), this.position().z() + (double)(this.random.nextFloat() * this.getDimensions(Pose.STANDING).width * 2.0F) - (double)this.getDimensions(Pose.STANDING).width, d0, d1, d2);
        }
    	super.startStealth();
    }

    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;
  
    	if(this.isRareVariant() && target instanceof LivingEntity) {
    		LivingEntity targetLiving = (LivingEntity)target;
    		List<MobEffect> goodEffects = new ArrayList<>();
    		for(MobEffectInstance effectInstance : targetLiving.getActiveEffects()) {
				if(ObjectLists.inEffectList("buffs", effectInstance.getEffect()))
					goodEffects.add(effectInstance.getEffect());
    		}
    		if(goodEffects.size() > 0) {
    			if(goodEffects.size() > 1)
    				targetLiving.removeEffect(goodEffects.get(this.getRandom().nextInt(goodEffects.size())));
    			else
    				targetLiving.removeEffect(goodEffects.get(0));
				float leeching = Math.max(1, this.getAttackDamage(damageScale) / 2);
		    	this.heal(leeching);
    		}
    	}
        
        return true;
    }

    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return true; }

    public boolean petControlsEnabled() { return true; }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public boolean isVulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isVulnerableTo(type, source, damage);
    }
    
    public boolean daylightBurns() {
    	return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public ResourceLocation getTexture(String suffix) {
        if(!this.hasCustomName() || !"Shadow Clown".equals(this.getCustomName().getString()))
            return super.getTexture(suffix);

        String textureName = this.getTextureName() + "_shadowclown";
		if(!"".equals(suffix)) {
			textureName += "_" + suffix;
		}
        if(TextureManager.getTexture(textureName) == null)
            TextureManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
        return TextureManager.getTexture(textureName);
    }
}
