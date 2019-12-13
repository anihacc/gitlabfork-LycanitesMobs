package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.entity.projectile.EntitySpectralbolt;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityReaper extends TameableCreatureEntity implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityReaper(EntityType<? extends EntityReaper> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = false;
        this.setupMob();
        
        // No Block Collision:
        this.noClip = true;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(0.75F).setCheckSight(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));

        this.targetSelector.addGoal(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(EntityType.PHANTOM));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Particles:
        if(this.getEntityWorld().isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().addParticle(ParticleTypes.WITCH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile(EntitySpectralbolt.class, target, range, 0, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
        super.attackRanged(target, range);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean useDirectNavigator() {
        return true;
    }

    @Override
    public boolean canSeeThroughWalls() {
        return true;
    }
    
    
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
    public boolean daylightBurns() { return super.daylightBurns(); }
    
    
    // ==================================================
   	//                       Sounds
   	// ==================================================
    // ========== Idle ==========
    /** Returns the sound to play when this creature is making a random ambient roar, grunt, etc. **/
    @Override
    protected SoundEvent getAmbientSound() {
    	if(this.hasAttackTarget()) {
    		if(this.getAttackTarget() instanceof PlayerEntity)
    			if("jbams".equalsIgnoreCase((this.getAttackTarget()).getName().toString())) // JonBams special sound!
    				return ObjectManager.getSound(this.creatureInfo.getName() + "_say_jon");
    	}
        if(this.isTamed() && this.getOwner() != null) {
            if("jbams".equalsIgnoreCase(this.getOwnerName().getFormattedText()))
                return ObjectManager.getSound(this.creatureInfo.getName() + "_say_jon");
        }
    	return super.getAmbientSound();
    }


    // ==================================================
    //                       Visuals
    // ==================================================
    /** Returns this creature's main texture. Also checks for for subspecies. **/
    @Override
    public ResourceLocation getTexture(String suffix) {
        if(!this.hasCustomName() || !"Satan Claws".equals(this.getCustomName().getFormattedText()))
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
