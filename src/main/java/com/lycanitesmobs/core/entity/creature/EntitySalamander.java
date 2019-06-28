package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.CustomItemEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

public class EntitySalamander extends RideableCreatureEntity implements IMob, IGroupFire {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySalamander(EntityType<? extends EntitySalamander> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.isLavaCreature = true;
        this.hasAttackSound = true;
        this.hasJumpSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;
        this.solidCollision = false;
        this.setupMob();

        // Stats:
        this.stepHeight = 1.0F;

        this.setPathPriority(PathNodeType.LAVA, 0F);
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PaddleGoal(this));
        this.goalSelector.addGoal(1, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.goalSelector.addGoal(2, new AttackMeleeGoal(this));
        this.goalSelector.addGoal(3, this.stayGoal);
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(5, new StayByWaterGoal(this).setSpeed(1.25D));
        this.goalSelector.addGoal(6, new FollowParentGoal(this).setSpeed(1.0D));
        this.goalSelector.addGoal(7, new WanderGoal(this).setPauseRate(30));
        this.goalSelector.addGoal(9, new BegGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeRiderGoal(this));
        this.targetSelector.addGoal(1, new CopyRiderAttackTargetGoal(this));
        this.targetSelector.addGoal(2, new RevengeOwnerGoal(this));
        this.targetSelector.addGoal(3, new CopyOwnerAttackTargetGoal(this));
        this.targetSelector.addGoal(4, new DefendOwnerGoal(this));
        this.targetSelector.addGoal(5, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(6, new FindAttackTargetGoal(this).addTargets(EntityType.PLAYER));
        this.targetSelector.addGoal(6, new FindAttackTargetGoal(this).addTargets(EntityType.VILLAGER));
        this.targetSelector.addGoal(7, new FindAttackTargetGoal(this).setTargetClass(IGroupPrey.class));
        this.targetSelector.addGoal(8, new FindAttackTargetGoal(this).setTargetClass(IGroupAlpha.class).setPackHuntingScale(1, 1));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetSelector.addGoal(8, new FindAttackTargetGoal(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(1, 3));
            this.targetSelector.addGoal(8, new FindAttackTargetGoal(this).setTargetClass(AnimalEntity.class).setPackHuntingScale(1, 3));
        }
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
    }

    @Override
    public void riderEffects(LivingEntity rider) {
        rider.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, (5 * 20) + 5, 1));
        if(rider.isPotionActive(ObjectManager.getEffect("penetration")))
            rider.removePotionEffect(ObjectManager.getEffect("penetration"));
        if(rider.isBurning())
            rider.extinguish();
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
        if(this.lavaContact())
            return 2.0F;
        return 1.0F;
    }

    // Pathing Weight:
    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;
        BlockPos pos = new BlockPos(x, y, z);
        if(this.getEntityWorld().getBlockState(pos).getBlock() == Blocks.LAVA)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        return super.getBlockPathWeight(x, y, z);
    }

    // Pushed By Water:
    @Override
    public boolean isPushedByWater() {
        return false;
    }

    // ========== Mounted Offset ==========
    @Override
    public double getMountedYOffset() {
        return (double)this.getSize(Pose.STANDING).height * 0.85D;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Special Attack ==========
    public void specialAttack() {
        // Firey Burst:
        double distance = 5.0D;
        List<LivingEntity> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(distance, distance, distance), new Predicate<LivingEntity>() {
            @Override
            public boolean apply(LivingEntity possibleTarget) {
                if(!possibleTarget.isAlive()
                        || possibleTarget == EntitySalamander.this
                        || EntitySalamander.this.isRidingOrBeingRiddenBy(possibleTarget)
                        || EntitySalamander.this.isOnSameTeam(possibleTarget)
                        || !EntitySalamander.this.canAttack(possibleTarget.getType())
                        || !EntitySalamander.this.canAttack(possibleTarget))
                    return false;
                return true;
            }
        });
        if(!possibleTargets.isEmpty()) {
            for(LivingEntity possibleTarget : possibleTargets) {
                boolean doDamage = true;
                if(this.getRider() instanceof PlayerEntity) {
                    if(MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((PlayerEntity)this.getRider(), possibleTarget))) {
                        doDamage = false;
                    }
                }
                if(doDamage) {
                    possibleTarget.setFire(5);
                }
            }
        }
        this.playAttackSound();
        this.triggerAttackCooldown();
    }


    // ==================================================
    //                   Mount Ability
    // ==================================================
    @Override
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(this.abilityToggled)
            return;
        if(this.getStamina() < this.getStaminaCost())
            return;

        this.specialAttack();
        this.applyStaminaCost();
    }

    @Override
    public float getStaminaCost() {
        return 15;
    }

    @Override
    public int getStaminaRecoveryWarmup() {
        return 5 * 20;
    }

    @Override
    public float getStaminaRecoveryMax() {
        return 1.0F;
    }

    // Dismount:
    @Override
    public void onDismounted(Entity entity) {
        super.onDismounted(entity);
        if(entity != null && entity instanceof LivingEntity) {
            ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 5 * 20, 1));
        }
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 10; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean canBurn() { return false; }
    
    @Override
    public boolean waterDamage() { return true; }
    
    @Override
    public boolean canBreatheUnderlava() {
        return true;
    }
    
    @Override
    public boolean canBreatheAir() {
        return true;
    }

    @Override
    public float getFallResistance() {
        return 100;
    }
    
    
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
   	//                       Drops
   	// ==================================================
    // ========== Apply Drop Effects ==========
    /** Used to add effects or alter the dropped entity item. **/
    @Override
    public void applyDropEffects(CustomItemEntity entityItem) {
        entityItem.setCanBurn(false);
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        return 15728880;
    }
	

    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("cookedmeat", testStack);
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    @Override
    public boolean petControlsEnabled() { return true; }
}
