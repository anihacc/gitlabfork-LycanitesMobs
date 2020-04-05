package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.BuildAroundTargetGoal;
import com.lycanitesmobs.core.entity.goals.actions.ChaseGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.EffectAuraGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FaceTargetGoal;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityArchvile extends TameableCreatureEntity implements IMob {
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityArchvile(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEAD;
        this.hasAttackSound = false;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();

        this.tasks.addTask(this.nextIdleGoalIndex, new FaceTargetGoal(this));
        this.tasks.addTask(this.nextCombatGoalIndex, new ChaseGoal(this).setMinDistance(16F).setMaxDistance(64F).setSpeed(1));
        this.tasks.addTask(this.nextCombatGoalIndex, new BuildAroundTargetGoal(this).setBlock(ObjectManager.getBlock("doomfire")).setTickRate(40).setRange(3).setTargetBit(TARGET_BITS.ATTACK).setEnclose(true));
        this.tasks.addTask(this.nextCombatGoalIndex, new EffectAuraGoal(this).setEffect(MobEffects.STRENGTH).setAmplifier(2).setEffectSeconds(2).setRange(32).setCheckSight(false)
                .setTargetTypes(TARGET_TYPES.ALLY.id).setTargetCreatureType("demon"));
        this.tasks.addTask(this.nextCombatGoalIndex, new EffectAuraGoal(this).setEffect(MobEffects.SPEED).setAmplifier(2).setEffectSeconds(2).setRange(32).setCheckSight(false)
                .setTargetTypes(TARGET_TYPES.ALLY.id).setTargetCreatureType("demon"));
        this.tasks.addTask(this.nextCombatGoalIndex, new EffectAuraGoal(this).setEffect(MobEffects.RESISTANCE).setAmplifier(2).setEffectSeconds(2).setRange(32).setCheckSight(false)
                .setTargetTypes(TARGET_TYPES.ALLY.id).setTargetCreatureType("demon"));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Particles:
        if(this.getEntityWorld().isRemote) {
            for (int i = 0; i < 2; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
                this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width, this.posY + this.rand.nextDouble() * (double) this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width, 0.0D, 0.0D, 0.0D);
            }
        }
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean canBurn() { return false; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
