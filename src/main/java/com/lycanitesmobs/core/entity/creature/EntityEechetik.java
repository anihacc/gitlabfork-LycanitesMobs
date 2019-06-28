package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.EffectBase;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.List;

public class EntityEechetik extends TameableCreatureEntity implements IMob {

	public int eechetikMyceliumRadius = 2; // TODO Creature flags.

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEechetik(EntityType<? extends EntityEechetik> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

		// Plague Aura Attack:
		if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0 && this.hasAttackTarget()) {
			EffectBase plague = ObjectManager.getEffect("plague");
			if(plague != null) {
				EffectInstance potionEffect = new EffectInstance(plague, this.getEffectDuration(5), 1);
				List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 4);
				for(Object entityObj : aoeTargets) {
					LivingEntity target = (LivingEntity) entityObj;
					if (target != this && this.canAttack(target.getType()) && this.canAttack(target) && this.getEntitySenses().canSee(target) && target.isPotionApplicable(potionEffect)) {
						target.addPotionEffect(potionEffect);
					}
				}
			}
		}

		// Grow Mycelium:
		if(!this.getEntityWorld().isRemote && this.updateTick % 100 == 0 && this.eechetikMyceliumRadius > 0 && !this.isTamed() && this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
			int range = this.eechetikMyceliumRadius;
			for (int w = -((int) Math.ceil(this.getSize(Pose.STANDING).width) + range); w <= (Math.ceil(this.getSize(Pose.STANDING).width) + range); w++) {
				for (int d = -((int) Math.ceil(this.getSize(Pose.STANDING).width) + range); d <= (Math.ceil(this.getSize(Pose.STANDING).width) + range); d++) {
					for (int h = -((int) Math.ceil(this.getSize(Pose.STANDING).height) + range); h <= Math.ceil(this.getSize(Pose.STANDING).height); h++) {
						BlockPos blockPos = this.getPosition().add(w, h, d);
						BlockState blockState = this.getEntityWorld().getBlockState(blockPos);
						BlockState upperBlockState = this.getEntityWorld().getBlockState(blockPos.up());
						if (upperBlockState.getBlock() == Blocks.AIR && blockState.getBlock() == Blocks.DIRT) {
							this.getEntityWorld().setBlockState(blockPos, Blocks.MYCELIUM.getDefaultState());
						}
					}
				}
			}
		}

		// Particles:
		if(this.getEntityWorld().isRemote) {
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().addParticle(ParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width * 2, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width * 2, 0.0D, 0.0D, 0.0D);
				this.getEntityWorld().addParticle(ParticleTypes.MYCELIUM, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width * 2, this.posY + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width * 2, 0.0D, 0.0D, 0.0D);
			}
		}
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;
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
   	//                    Taking Damage
   	// ==================================================
	// ========== Damage Modifier ==========
	public float getDamageModifier(DamageSource damageSrc) {
		if(damageSrc.isFireDamage())
			return 0F;
		else return super.getDamageModifier(damageSrc);
	}
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("inWall")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}
}
