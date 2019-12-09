package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityStrider extends RideableCreatureEntity implements IGroupHeavy {
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
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
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
            // Drop Owner When Tamed:
            if(this.isTamed() && this.hasPickupEntity() && this.getPickupEntity() == this.getOwner())
                this.dropPickupEntity();

            // Entity Pickup Update:
            if(this.hasPickupEntity()) {
                ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
                if(extendedEntity != null)
                    extendedEntity.setPickedUpByEntity(this);

                if(this.isTamed() && !this.canAttack(this.getPickupEntity())) {
                    this.getPickupEntity().addPotionEffect(new EffectInstance(Effects.WATER_BREATHING, this.getEffectDuration(5), 1));
                }

                else if(this.pickupTime++ % 40 == 0) {
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
    @Override
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(this.abilityToggled)
            return;

        if(this.hasPickupEntity()) {
            this.dropPickupEntity();
            return;
        }

        if(this.getStamina() < this.getStaminaCost())
            return;

        LivingEntity nearestTarget = this.getNearestEntity(LivingEntity.class, null, 4, false);
        if(this.canPickupEntity(nearestTarget))
            this.pickupEntity(nearestTarget);

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
        if(this.hasPickupEntity() && !this.isTamed() && this.getRNG().nextFloat() <= 0.25F)
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
        if(!this.isTamed() && this.pickupCooldown > 0)
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
}
