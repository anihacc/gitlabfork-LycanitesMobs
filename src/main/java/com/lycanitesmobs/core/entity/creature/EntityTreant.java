package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.AttackTargetingGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeTargetingGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.api.IGroupPlant;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityTreant extends EntityCreatureBase implements IMob, IGroupPlant, IGroupHeavy {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTreant(EntityType<? extends EntityTreant> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsUnderground = false;
        this.hasAttackSound = true;
        this.spreadFire = true;
        this.solidCollision = true;
        this.setupMob();
        this.hitAreaWidthScale = 1.5F;

        this.stepHeight = 2.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(3, new AttackMeleeGoal(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.goalSelector.addGoal(4, new AttackMeleeGoal(this));
        //this.goalSelector.addGoal(5, this.aiSit);
        //this.goalSelector.addGoal(6, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.goalSelector.addGoal(7, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));
        //this.targetSelector.addGoal(0, new EntityAITargetOwnerRevenge(this));
        //this.targetSelector.addGoal(1, new EntityAITargetOwnerAttack(this));
        this.targetSelector.addGoal(2, new RevengeTargetingGoal(this).setHelpClasses(EntityEnt.class));
        this.targetSelector.addGoal(3, new AttackTargetingGoal(this).setTargetClass(IGroupFire.class));
        this.targetSelector.addGoal(4, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class).setCheckSight(false));
        this.targetSelector.addGoal(5, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        //this.targetSelector.addGoal(6, new EntityAITargetOwnerThreats(this));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

        // Water Healing:
		if(this.getAir() >= 0) {
			if (this.isInWater())
				this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 3 * 20, 2));
			else if (this.isInWaterRainOrBubbleColumn())
				this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 3 * 20, 1));
		}
    }
    
    // ==================================================
    //                      Attacks
    // ==================================================
	@Override
	public boolean canAttack(LivingEntity target) {
		if(target instanceof EntityEnt)
			return false;
		return super.canAttack(target);
	}
    
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;
    	
    	// Leech:
		float leeching = Math.max(1, this.getAttackDamage(damageScale));
    	this.heal(leeching);
        
        return true;
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFireDamage())
    		return 2.0F;
    	if(damageSrc.getTrueSource() != null) {
    		Item heldItem = null;
    		if(damageSrc.getTrueSource() instanceof PlayerEntity) {
    			PlayerEntity entityPlayer = (PlayerEntity)damageSrc.getTrueSource();
	    		if(!entityPlayer.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
	    			heldItem = entityPlayer.getHeldItem(Hand.MAIN_HAND).getItem();
	    		}
    		}
    		else if(damageSrc.getTrueSource() instanceof LivingEntity) {
	    		LivingEntity entityLiving = (LivingEntity)damageSrc.getTrueSource();
	    		if(!entityLiving.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
	    			heldItem = entityLiving.getHeldItem(Hand.MAIN_HAND).getItem();
	    		}
    		}
    		if(ObjectLists.isAxe(heldItem))
				return 2.0F;
    	}
        return super.getDamageModifier(damageSrc);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 100;
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return false; }


	// ==================================================
	//                       Visuals
	// ==================================================
	/** Returns this creature's main texture. Also checks for for subspecies. **/
	public ResourceLocation getTexture() {
		if("Wicked Treant".equals(this.getCustomName())) {
			String textureName = this.getTextureName() + "_wicked";
			if (AssetManager.getTexture(textureName) == null)
				AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
			return AssetManager.getTexture(textureName);
		}

		if("Salty Tree".equals(this.getCustomName())) {
			String textureName = this.getTextureName() + "_saltytree";
			if (AssetManager.getTexture(textureName) == null)
				AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
			return AssetManager.getTexture(textureName);
		}

		return super.getTexture();
	}

    // ========== Rendering Distance ==========
    /** Returns a larger bounding box for rendering this large entity. **/
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getBoundingBox().grow(50, 20, 50).offset(0, -10, 0);
    }
}
