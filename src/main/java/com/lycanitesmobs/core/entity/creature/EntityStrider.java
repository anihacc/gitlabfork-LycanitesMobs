package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.ExtendedEntity;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityStrider extends TameableCreatureEntity implements IGroupHeavy {

    protected WanderGoal wanderAI;
    protected AttackMeleeGoal attackAI;

    protected int pickupCooldown = 100;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityStrider(EntityType<? extends EntityStrider> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0D;
        this.canGrow = true;
        this.setupMob();
        this.hitAreaWidthScale = 1.5f;
        this.hitAreaHeightScale = 1;

        this.setPathPriority(PathNodeType.WATER, 0F);
        this.stepHeight = 4.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PaddleGoal(this).setSink(true));
        this.goalSelector.addGoal(2, this.stayGoal);
        this.attackAI = new AttackMeleeGoal(this).setLongMemory(false);
        this.goalSelector.addGoal(3, this.attackAI);
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(5, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.goalSelector.addGoal(6, new StayByWaterGoal(this).setSpeed(1.25D));
        this.wanderAI = new WanderGoal(this);
        this.goalSelector.addGoal(7, this.wanderAI);
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));

        this.targetSelector.addGoal(0, new RevengeOwnerGoal(this));
        this.targetSelector.addGoal(1, new CopyOwnerAttackTargetGoal(this));
        this.targetSelector.addGoal(0, new RevengeRiderGoal(this));
        this.targetSelector.addGoal(1, new CopyRiderAttackTargetGoal(this));
        this.targetSelector.addGoal(3, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(4, new FindAttackTargetGoal(this).setTargetClass(PlayerEntity.class).setCheckSight(false));
        this.targetSelector.addGoal(5, new FindAttackTargetGoal(this).addTargets(EntityType.VILLAGER));
        this.targetSelector.addGoal(6, new DefendOwnerGoal(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
    private int pickupTime = 0;
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        if(!this.getEntityWorld().isRemote) {
            // Wander Pause Rates:
            if(this.isInWater())
                this.wanderAI.setPauseRate(120);
            else
                this.wanderAI.setPauseRate(0);

            // Drop Owner When Tamed:
            if(this.isTamed() && this.hasPickupEntity() && this.getPickupEntity() == this.getOwner())
                this.dropPickupEntity();

            // Entity Pickup Update:
            this.attackAI.setEnabled(!this.hasPickupEntity());
            if(this.hasPickupEntity()) {
                ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
                if(extendedEntity != null)
                    extendedEntity.setPickedUpByEntity(this);
                if(this.pickupTime++ % 40 == 0) {
                    this.attackEntityAsMob(this.getPickupEntity(), 0.5F);
                    if(this.getPickupEntity() instanceof LivingEntity) {
                        if(ObjectManager.getEffect("penetration") != null)
                            this.getPickupEntity().addPotionEffect(new EffectInstance(ObjectManager.getEffect("penetration"), this.getEffectDuration(5), 1));
                    }
                }
            }
            else {
                if(this.pickupCooldown > 0)
                    this.pickupCooldown--;
                this.pickupTime = 0;
            }
        }
    }


    // ==================================================
    //                   Mount Ability
    // ==================================================
    /*@Override
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(this.abilityToggled)
            return;
        if(this.getStamina() < this.getStaminaCost())
            return;

        // Penetrating Screech:
        double distance = 10.0D;
        List<LivingEntity> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(distance, distance, distance), new Predicate<LivingEntity>() {
            @Override
            public boolean apply(LivingEntity possibleTarget) {
                if(!possibleTarget.isAlive()
                        || possibleTarget == EntityStrider.this
                        || EntityStrider.this.isRidingOrBeingRiddenBy(possibleTarget)
                        || EntityStrider.this.isOnSameTeam(possibleTarget)
                        || EntityStrider.this.canAttack(possibleTarget.getType())
                        || EntityStrider.this.canAttack(possibleTarget))
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
                    if (ObjectManager.getEffectInstance("penetration") != null)
                        possibleTarget.addPotionEffect(new EffectInstance(ObjectManager.getEffectInstance("penetration"), this.getEffectDuration(5), 1));
                    else
                        possibleTarget.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 10 * 20, 0));
                }
            }
        }
        this.playAttackSound();
        this.triggerAttackCooldown();

        this.applyStaminaCost();
    }

    public float getStaminaCost() {
        return 20;
    }

    public int getStaminaRecoveryWarmup() {
        return 5 * 20;
    }

    public float getStaminaRecoveryMax() {
        return 1.0F;
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) { return false; }

    // Mounted Y Offset:
    @Override
    public double getMountedYOffset() {
        return (double)this.getSize(Pose.STANDING).height * 1.0D * this.sizeScale;
    }

    // Mount/Dismount:
    @Override
    public void onDismounted(Entity entity) {
        super.onDismounted(entity);
        if(entity != null && entity instanceof LivingEntity) {
            if(ObjectManager.getEffectInstance("fallresist") != null)
                ((LivingEntity)entity).addPotionEffect(new EffectInstance(ObjectManager.getEffectInstance("fallresist"), 3 * 20, 1));
        }
    }*/

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
	@Override
    public float getAISpeedModifier() {
    	if(this.isInWater()) // Checks specifically just for water.
            return 2F;
    	if(this.waterContact()) // Checks for water, rain, etc.
    		return 1.5F;
        return super.getAISpeedModifier();
    }

    // Pushed By Water:
    @Override
    public boolean isPushedByWater() {
        return false;
    }
    
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;

        Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
        if(block == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z)))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getAttackTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }


    // ========== Get Wander Position ==========
    public BlockPos getWanderPosition(BlockPos wanderPosition) {
        BlockPos groundPos;
        for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && !this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid(); groundPos = groundPos.down()) {}
        return groundPos.up();
    }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        if(!super.attackMelee(target, damageScale))
            return false;

        // Pickup:
        if(target instanceof LivingEntity) {
            LivingEntity entityLivingBase = (LivingEntity)target;
            if (this.canPickupEntity(entityLivingBase) && this.canEntityBeSeen(target)) {
                this.pickupEntity(entityLivingBase);
                this.pickupCooldown = 100;
            }
        }

        return true;
    }


    // ==================================================
    //                   Taking Damage
    // ==================================================
    // ========== On Damage ==========
    /** Called when this mob has received damage. Here there is a random chance of this mob dropping any picked up entities. **/
    @Override
    public void onDamage(DamageSource damageSrc, float damage) {
        if(this.hasPickupEntity() && this.getRNG().nextFloat() <= 0.25F)
            this.dropPickupEntity();
        super.onDamage(damageSrc, damage);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public float getFallResistance() {
        return 20;
    }


    // ==================================================
    //                     Abilities
    // ==================================================
    @Override
    public double[] getPickupOffset(Entity entity) {
        return new double[]{0, 5.5D, 0};
    }

    // ========== Pickup ==========
    public boolean canPickupEntity(LivingEntity entity) {
        if(this.pickupCooldown > 0)
            return false;
        return super.canPickupEntity(entity);
    }

    public void dropPickupEntity() {
        ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
        if(extendedEntity != null)
            extendedEntity.setPickedUpByEntity(null);
        this.pickupEntity = null;
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getBagSize() { return 15; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("cookedmeat", testStack);
    }
}
