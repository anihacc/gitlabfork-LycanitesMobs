package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

import java.util.List;

public class EntityShade extends EntityCreatureRideable implements IGroupPredator, IGroupHunter, IGroupShadow {

	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityShade(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.hasJumpSound = true;
        this.canGrow = false;
        this.setupMob();

        this.stepHeight = 1.0F;
        this.attackCooldownMax = 40;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        //this.field_70714_bg.addTask(2, this.aiSit);
        this.field_70714_bg.addTask(4, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(6, new EntityAIAttackMelee(this).setSpeed(1.5D));
        this.field_70714_bg.addTask(7, this.aiSit);
        this.field_70714_bg.addTask(8, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(9, new EntityAIWander(this).setSpeed(1.0D));
        this.field_70714_bg.addTask(10, new EntityAIBeg(this));
        this.field_70714_bg.addTask(11, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(12, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetRiderRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetRiderAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(3, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(4, new EntityAITargetOwnerThreats(this));
        this.field_70715_bh.addTask(5, new EntityAITargetRevenge(this));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntityCow.class).setTameTargetting(true));
            this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntityPig.class).setTameTargetting(true));
            this.field_70715_bh.addTask(6, new EntityAITargetAttack(this).setTargetClass(EntitySheep.class).setTameTargetting(true));
        }
        this.field_70715_bh.addTask(7, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(7, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(8, new EntityAITargetAttack(this).setTargetClass(EntityPigZombie.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(8, new EntityAITargetAttack(this).setTargetClass(IGroupAlpha.class));
            this.field_70715_bh.addTask(8, new EntityAITargetAttack(this).setTargetClass(AnimalEntity.class));
        }
        this.field_70715_bh.addTask(9, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        this.field_70715_bh.addTask(10, new EntityAITargetOwnerThreats(this));
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

    public float getStaminaCost() {
        return 100;
    }

    public int getStaminaRecoveryWarmup() {
        return 5 * 20;
    }

    public float getStaminaRecoveryMax() {
        return 1.0F;
    }


    // ==================================================
    //                     Movement
    // ==================================================
    // Mounted Y Offset:
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.85D;
    }

    @Override
    public double getMountedZOffset() {
        return (double)this.width * 0.25D;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
        if(!super.attackMelee(target, damageScale))
        	return false;

        // Leech:
        float leeching = this.getEffectStrength(this.getAttackDamage(damageScale) / 4);
        this.heal(leeching);

        if(this.getRNG().nextFloat() <= 0.1F)
            this.specialAttack();
    	
        return true;
    }

    // ========== Special Attack ==========
    public void specialAttack() {
        // Horrific Howl:
        double distance = 5.0D;
        List<LivingEntity> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, this.getEntityBoundingBox().grow(distance, distance, distance), possibleTarget -> {
			if(!possibleTarget.isAlive()
					|| possibleTarget == EntityShade.this
					|| EntityShade.this.isRidingOrBeingRiddenBy(possibleTarget)
					|| EntityShade.this.isOnSameTeam(possibleTarget)
					|| !EntityShade.this.canAttackClass(possibleTarget.getClass())
					|| !EntityShade.this.canAttackEntity(possibleTarget))
				return false;
			return true;
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
                    if (ObjectManager.getEffect("fear") != null)
                        possibleTarget.addPotionEffect(new EffectInstance(ObjectManager.getEffect("fear"), this.getEffectDuration(5), 1));
                    else
                        possibleTarget.addPotionEffect(new EffectInstance(MobEffects.WEAKNESS, 10 * 20, 0));
                }
            }
        }
        this.playAttackSound();
        this.triggerAttackCooldown();
    }
    
    
    // ==================================================
   	//                     Abilities
   	// ==================================================
    public boolean canBeTempted() {
    	return true;
    }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 10; }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isInvulnerableTo(type, source, damage);
    }

    @Override
    public float getFallResistance() {
        return 10;
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityShade(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
        return false;
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("cookedmeat", testStack);
    }
}
