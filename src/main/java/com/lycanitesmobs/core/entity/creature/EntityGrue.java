package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.api.IGroupShadow;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityGrue extends EntityCreatureTameable implements IMob, IGroupShadow {
    
	private int teleportTime = 60;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGrue(EntityType<? extends EntityGrue> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(1, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.goalSelector.addGoal(2, new AttackMeleeGoal(this).setLongMemory(true));
        this.goalSelector.addGoal(3, this.aiSit);
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(8, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new OwnerRevengeTargetingGoal(this));
        this.targetSelector.addGoal(1, new OwnerAttackTargetingGoal(this));
        this.targetSelector.addGoal(2, new RevengeTargetingGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.targetSelector.addGoal(6, new OwnerDefenseTargetingGoal(this));
    }

    // ========== Set Size ==========
    @Override
    public void setSizeScale(double scale) {
        if(this.isRareSubspecies()) {
            super.setSizeScale(scale * 1.5D);
            return;
        }
        super.setSizeScale(scale);
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Random Target Teleporting:
        if(!this.getEntityWorld().isRemote && this.hasAttackTarget()) {
	        if(this.teleportTime-- <= 0) {
	        	this.teleportTime = 60 + this.getRNG().nextInt(40);
        		BlockPos teleportPosition = this.getFacingPosition(this.getAttackTarget(), -this.getAttackTarget().getSize(Pose.STANDING).width - 1D, 0);
        		if(this.canTeleportTo(teleportPosition)) {
					this.playJumpSound();
					this.setPosition(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
				}
	        }
        }
        
        // Particles:
        if(this.getEntityWorld().isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().addParticle(ParticleTypes.WITCH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
    }

	/**
	 * Checks if this entity can teleport to the provided block position.
	 * @param pos The position to teleport to.
	 * @return True if it's safe to teleport.
	 */
	public boolean canTeleportTo(BlockPos pos) {
		for (int y = 0; y <= 1; y++) {
			BlockState blockState = this.getEntityWorld().getBlockState(pos.add(0, y, 0));
			if (blockState.isSolid())
				return false;
		}
        return true;
    }
    
    
    // ==================================================
   	//                     Stealth
   	// ==================================================
    @Override
    public boolean canStealth() {
    	if(this.getEntityWorld().isRemote) return false;
		if(this.isMoving()) return false;
    	return this.testLightLevel() <= 0;
    }
    
    @Override
    public void startStealth() {
    	if(this.getEntityWorld().isRemote) {
            IParticleData particle = ParticleTypes.WITCH;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            for(int i = 0; i < 100; i++)
            	this.getEntityWorld().addParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).height), this.posZ + (double)(this.rand.nextFloat() * this.getSize(Pose.STANDING).width * 2.0F) - (double)this.getSize(Pose.STANDING).width, d0, d1, d2);
        }
    	super.startStealth();
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;
    	
    	// Leech:
    	if(this.getSubspeciesIndex() > 2 && target instanceof LivingEntity) {
    		LivingEntity targetLiving = (LivingEntity)target;
    		List<Effect> goodEffects = new ArrayList<>();
    		for(EffectInstance effectInstance : targetLiving.getActivePotionEffects()) {
				if(ObjectLists.inEffectList("buffs", effectInstance.getPotion()))
					goodEffects.add(effectInstance.getPotion());
    		}
    		if(goodEffects.size() > 0) {
    			if(goodEffects.size() > 1)
    				targetLiving.removePotionEffect(goodEffects.get(this.getRNG().nextInt(goodEffects.size())));
    			else
    				targetLiving.removePotionEffect(goodEffects.get(0));
				float leeching = Math.max(1, this.getAttackDamage(damageScale) / 2);
		    	this.heal(leeching);
    		}
    	}
        
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isInvulnerableTo(type, source, damage);
    }
    
    /** Returns true if this mob should be damaged by the sun. **/
    public boolean daylightBurns() {
    	return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }


    // ==================================================
    //                       Visuals
    // ==================================================
    @Override
    public ResourceLocation getTexture(String suffix) {
        if(!"Shadow Clown".equals(this.getCustomName()))
            return super.getTexture(suffix);

        String textureName = this.getTextureName() + "_shadowclown";
		if(!"".equals(suffix)) {
			textureName += "_" + suffix;
		}
        if(AssetManager.getTexture(textureName) == null)
            AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
        return AssetManager.getTexture(textureName);
    }
}
