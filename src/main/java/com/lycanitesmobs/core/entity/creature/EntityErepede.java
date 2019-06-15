package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.entity.projectile.EntityMudshot;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityErepede extends EntityCreatureRideable implements IGroupPredator {
	
	int difficultyUpdate = -1;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityErepede(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;
        this.attackCooldownMax = 10;
        this.setupMob();
        
        // Stats:
        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPlayerControl(this));
        this.tasks.addTask(2, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.tasks.addTask(3, new EntityAIAttackRanged(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(6.0F));
		this.tasks.addTask(4, this.aiSit);
		this.tasks.addTask(5, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.tasks.addTask(6, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.tasks.addTask(7, new EntityAIWander(this));
        this.tasks.addTask(9, new EntityAIBeg(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

		this.targetTasks.addTask(0, new EntityAITargetOwnerRevenge(this));
		this.targetTasks.addTask(1, new EntityAITargetOwnerAttack(this));
		this.targetTasks.addTask(2, new EntityAITargetOwnerThreats(this));
        this.targetTasks.addTask(3, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            if(CreatureManager.getInstance().getCreature("Joust") != null)
                this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityJoust.class).setPackHuntingScale(1, 3));
            if(CreatureManager.getInstance().getCreature("JoustAlpha") != null)
                this.targetTasks.addTask(4, new EntityAITargetAttack(this).setTargetClass(EntityJoustAlpha.class).setPackHuntingScale(1, 1));
        }

        this.targetTasks.addTask(0, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }
    
    public void riderEffects(LivingEntity rider) {
    	if(rider.isPotionActive(MobEffects.WEAKNESS))
    		rider.removePotionEffect(MobEffects.WEAKNESS);
    	if(rider.isPotionActive(MobEffects.HUNGER))
    		rider.removePotionEffect(MobEffects.HUNGER);
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(this.hasRiderTarget()) {
            BlockState blockState = this.getEntityWorld().getBlockState(this.getPosition().add(0, -1, 0));
            if (blockState.getMaterial() == Material.SAND
                    || (blockState == Material.AIR && this.getEntityWorld().getBlockState(this.getPosition().add(0, -2, 0)).getMaterial() == Material.SAND))
                return 1.8F;
        }
    	return 1.0F;
    }
    
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.9D;
    }

    
    // ==================================================
    //                   Mount Ability
    // ==================================================
    public void mountAbility(Entity rider) {
    	if(this.getEntityWorld().isRemote)
    		return;
    	
    	if(this.abilityToggled)
    		return;
    	if(this.getStamina() < this.getStaminaCost())
    		return;
    	
    	if(rider instanceof PlayerEntity) {
    		PlayerEntity player = (PlayerEntity)rider;
	    	EntityMudshot projectile = new EntityMudshot(this.getEntityWorld(), player);
	    	this.getEntityWorld().spawnEntity(projectile);
	    	this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	    	this.triggerAttackCooldown();
    	}
    	
    	this.applyStaminaCost();
    }
    
    public float getStaminaCost() {
    	return 5;
    }
    
    public int getStaminaRecoveryWarmup() {
    	return 5 * 20;
    }
    
    public float getStaminaRecoveryMax() {
    	return 1.0F;
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityMudshot.class, target, range, 0, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 15; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }
    
    @Override
    public float getFallResistance() {
    	return 10;
    }


	// ==================================================
	//                     Pet Control
	// ==================================================
	public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityErepede(this.getEntityWorld());
	}
	
	// ========== Breeding Item ==========
	@Override
	public boolean isBreedingItem(ItemStack par1ItemStack) {
		return false;
    }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("CookedMeat", testStack);
    }
}
