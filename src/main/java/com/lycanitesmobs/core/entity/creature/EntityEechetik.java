package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.PotionBase;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EntityEechetik extends TameableCreatureEntity implements IMob {

	public int myceliumRadius = 2;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEechetik(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }

	@Override
	public void loadCreatureFlags() {
		this.myceliumRadius = this.creatureInfo.getFlag("myceliumRadius", this.myceliumRadius);
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

		// Plague Aura Attack:
		if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0 && this.hasAttackTarget()) {
			PotionBase plague = ObjectManager.getEffect("plague");
			if(plague != null) {
				PotionEffect potionEffect = new PotionEffect(plague, this.getEffectDuration(2), 1);
				List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 2);
				for(Object entityObj : aoeTargets) {
					EntityLivingBase target = (EntityLivingBase) entityObj;
					if (target != this && this.canAttackClass(target.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target) && target.isPotionApplicable(potionEffect)) {
						target.addPotionEffect(potionEffect);
					}
				}
			}
		}

		// Grow Mycelium:
		if(!this.getEntityWorld().isRemote && this.updateTick % 100 == 0 && this.myceliumRadius > 0 && !this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
			int range = this.myceliumRadius;
			for (int w = -((int) Math.ceil(this.width) + range); w <= (Math.ceil(this.width) + range); w++) {
				for (int d = -((int) Math.ceil(this.width) + range); d <= (Math.ceil(this.width) + range); d++) {
					for (int h = -((int) Math.ceil(this.height) + range); h <= Math.ceil(this.height); h++) {
						BlockPos blockPos = this.getPosition().add(w, h, d);
						IBlockState blockState = this.getEntityWorld().getBlockState(blockPos);
						IBlockState upperIBlockState = this.getEntityWorld().getBlockState(blockPos.up());
						if (upperIBlockState.getBlock() == Blocks.AIR && blockState.getBlock() == Blocks.DIRT) {
							this.getEntityWorld().setBlockState(blockPos, Blocks.MYCELIUM.getDefaultState());
						}
					}
				}
			}
		}

		// Particles:
		if(this.getEntityWorld().isRemote) {
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2, 0.0D, 0.0D, 0.0D);
				this.getEntityWorld().spawnParticle(EnumParticleTypes.TOWN_AURA, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2, 0.0D, 0.0D, 0.0D);
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
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("inWall")) return false;
    	return super.isDamageTypeApplicable(type, source, damage);
    }

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}
}
