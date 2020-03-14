package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

public class EntityPixen extends TameableCreatureEntity implements IMob {

    protected boolean wantsToLand;
    protected boolean  isLanded;

    public int auraRate = 60;

    /** A list of beneficial potion effects that this element can grant. **/
    public List<String> auraEffects = new ArrayList<>();

    /** The duration (in ticks) of the random effect. **/
    public int auraDuration = 100;

    /** The random effect amplifier. **/
    public int auraAmplifier = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityPixen(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.hasAttackSound = false;
        this.flySoundSpeed = 5;
        this.stepHeight = 1.0F;
        this.setupMob();

        // Random Aura Effects:
        this.auraEffects.add("minecraft:speed");
        this.auraEffects.add("minecraft:haste");
        this.auraEffects.add("minecraft:jump_boost");
        this.auraEffects.add("lycanitesmobs:fallresist");
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextDistractionGoalIndex++, new TemptGoal(this).setAlwaysTempted(true));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(1D).setRange(14.0F).setMinChaseDistance(5.0F).setCheckSight(false));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        // Land/Fly:
        if(!this.getEntityWorld().isRemote) {
            if(this.isLanded) {
                this.wantsToLand = false;
                if(!this.isSitting() && this.updateTick % (5 * 20) == 0 && this.getRNG().nextBoolean()) {
                    this.leap(1.0D, 1.0D);
                    this.isLanded = false;
                }
            }
            else {
                if(this.wantsToLand) {
                    if(this.isSafeToLand()) {
                        this.isLanded = true;
                    }
                }
                else {
                    if (this.updateTick % (5 * 20) == 0 && this.getRNG().nextBoolean()) {
                        this.wantsToLand = true;
                    }
                }
            }
        }

        // Mischief Aura:
        if(!this.getEntityWorld().isRemote && this.auraRate > 0 && !this.isPetType("familiar")) {
            if (this.updateTick % this.auraRate == 0) {
                List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 4);
                for (Object entityObj : aoeTargets) {
                    EntityLivingBase target = (EntityLivingBase) entityObj;
                    if (target != this && !(target instanceof EntityPixen) && target != this.getAttackTarget() && target != this.getAvoidTarget()) {
                        int randomIndex = this.getRNG().nextInt(this.auraEffects.size());
                        Potion effect = GameRegistry.findRegistry(Potion.class).getValue(new ResourceLocation(this.auraEffects.get(randomIndex)));
                        if(effect != null) {
                            target.addPotionEffect(new PotionEffect(effect, this.auraDuration, this.auraAmplifier));
                        }
                    }
                }
            }
        }
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Get Wander Position ==========
    public BlockPos getWanderPosition(BlockPos wanderPosition) {
        if(this.wantsToLand || !this.isLanded) {
            BlockPos groundPos;
            for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && this.getEntityWorld().getBlockState(groundPos).getBlock() == Blocks.AIR; groundPos = groundPos.down()) {}
            if(this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid()) {
                return groundPos.up();
            }
        }
        return super.getWanderPosition(wanderPosition);
    }

    // ========== Get Flight Offset ==========
    public double getFlightOffset() {
        if(!this.wantsToLand) {
            super.getFlightOffset();
        }
        return 0;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("tricksterflare", target, range, 0, new Vec3d(0, 0, 0), 0.75f, 1f, 1F);
        super.attackRanged(target, range);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return !this.isLanded; }

    @Override
    public boolean isStrongSwimmer() { return false; }

    @Override
    public boolean canBreatheUnderwater() {
        return false;
    }

    @Override
    public boolean canBeTempted() {
        return !this.isInPack() && this.getRevengeTarget() == null;
    }

    @Override
    public boolean isAggressive() {
        if(!this.isInPack() && this.getRevengeTarget() == null) {
            return false;
        }
        return super.isAggressive();
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 5; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
        return 100;
    }
}
