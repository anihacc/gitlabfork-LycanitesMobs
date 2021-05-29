package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.GoalConditions;
import com.lycanitesmobs.core.entity.goals.actions.BuildAroundTargetGoal;
import com.lycanitesmobs.core.entity.goals.actions.ChaseGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.EffectAuraGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.FaceTargetGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.SummonMinionsGoal;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import com.lycanitesmobs.core.entity.BaseCreatureEntity.TARGET_BITS;
import com.lycanitesmobs.core.entity.BaseCreatureEntity.TARGET_TYPES;

public class EntityApollyon extends TameableCreatureEntity implements IMob {
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityApollyon(EntityType<? extends EntityBehemophet> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = false;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(this.nextIdleGoalIndex, new FaceTargetGoal(this));
        this.goalSelector.addGoal(this.nextCombatGoalIndex, new ChaseGoal(this).setMinDistance(16F).setMaxDistance(64F).setSpeed(1));
        this.goalSelector.addGoal(this.nextCombatGoalIndex, new BuildAroundTargetGoal(this).setBlock(ObjectManager.getBlock("doomfire")).setTickRate(40).setRange(3).setEnclose(true).setTargetBit(TARGET_BITS.ATTACK));
        this.goalSelector.addGoal(this.nextCombatGoalIndex, new EffectAuraGoal(this).setEffect(Effects.DAMAGE_BOOST).setAmplifier(2).setEffectSeconds(2).setRange(32).setCheckSight(false)
                .setTargetTypes(TARGET_TYPES.ALLY.id).setTargetCreatureType("demon"));
        this.goalSelector.addGoal(this.nextCombatGoalIndex, new EffectAuraGoal(this).setEffect(Effects.MOVEMENT_SPEED).setAmplifier(2).setEffectSeconds(2).setRange(32).setCheckSight(false)
                .setTargetTypes(TARGET_TYPES.ALLY.id).setTargetCreatureType("demon"));
        this.goalSelector.addGoal(this.nextCombatGoalIndex, new EffectAuraGoal(this).setEffect(Effects.DAMAGE_RESISTANCE).setAmplifier(2).setEffectSeconds(2).setRange(32).setCheckSight(false)
                .setTargetTypes(TARGET_TYPES.ALLY.id).setTargetCreatureType("demon"));
        this.goalSelector.addGoal(this.nextCombatGoalIndex, new SummonMinionsGoal(this).setMinionInfo("belphegor").setSummonCap(2)
                .setConditions(new GoalConditions().setRareVariantOnly(true)));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void aiStep() {
        super.aiStep();

        // Particles:
        if(this.getCommandSenderWorld().isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.getCommandSenderWorld().addParticle(ParticleTypes.SMOKE, this.position().x() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double) this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
                this.getCommandSenderWorld().addParticle(ParticleTypes.FLAME, this.position().x() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double) this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double) this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
            }
        }
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean canBurn() { return false; }

    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.BagSize; }
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
