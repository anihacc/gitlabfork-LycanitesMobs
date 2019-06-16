package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.ExtendedEntity;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityStrider extends EntityCreatureTameable implements IGroupHeavy {

    protected EntityAIWander wanderAI;
    protected EntityAIAttackMelee attackAI;

    protected int pickupCooldown = 100;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityStrider(World world) {
        super(world);
        
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
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this).setSink(true));
        this.field_70714_bg.addTask(2, this.aiSit);
        this.attackAI = new EntityAIAttackMelee(this).setLongMemory(false);
        this.field_70714_bg.addTask(3, this.attackAI);
        this.field_70714_bg.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(5, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(6, new EntityAIStayByWater(this).setSpeed(1.25D));
        this.wanderAI = new EntityAIWander(this);
        this.field_70714_bg.addTask(7, this.wanderAI);
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(0, new EntityAITargetRiderRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetRiderAttack(this));
        this.field_70715_bh.addTask(3, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class).setCheckSight(false));
        this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
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
        List<LivingEntity> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, this.getEntityBoundingBox().grow(distance, distance, distance), new Predicate<LivingEntity>() {
            @Override
            public boolean apply(LivingEntity possibleTarget) {
                if(!possibleTarget.isAlive()
                        || possibleTarget == EntityStrider.this
                        || EntityStrider.this.isRidingOrBeingRiddenBy(possibleTarget)
                        || EntityStrider.this.isOnSameTeam(possibleTarget)
                        || EntityStrider.this.canAttackClass(possibleTarget.getClass())
                        || EntityStrider.this.canAttackEntity(possibleTarget))
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
                        possibleTarget.addPotionEffect(new EffectInstance(MobEffects.WEAKNESS, 10 * 20, 0));
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
        return (double)this.height * 1.0D * this.sizeScale;
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
        if(block == Blocks.FLOWING_WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * waterWeight;
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
