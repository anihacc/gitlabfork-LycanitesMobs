package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Treant extends BaseCreatureEntity implements Enemy, IGroupHeavy {
    
    public Treant(EntityType<? extends Treant> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsUnderground = false;
        this.hasAttackSound = true;
        this.spreadFire = true;
        this.solidCollision = true;
        this.setupMob();
        this.hitAreaWidthScale = 1.5F;

        this.maxUpStep = 2.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(Player.class).setLongMemory(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
    }

	@Override
    public void aiStep() {
        super.aiStep();

		if(this.getAirSupply() >= 0) {
			if (this.isInWater())
				this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 3 * 20, 1));
			else if (this.isInWaterRainOrBubble())
				this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 3 * 20, 0));
		}
    }

	@Override
	public boolean canAttack(LivingEntity target) {
		if(target instanceof Ent)
			return false;
		return super.canAttack(target);
	}
    
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;
  
		float leeching = Math.max(1, this.getAttackDamage(damageScale));
    	this.heal(leeching);
        
        return true;
    }
    
    

    public float getDamageModifier(DamageSource damageSrc) {
    	if(damageSrc.isFire())
    		return 2.0F;
		if(damageSrc.getEntity() != null) {
			ItemStack heldItem = ItemStack.EMPTY;
			if(damageSrc.getEntity() instanceof LivingEntity) {
				LivingEntity entityLiving = (LivingEntity)damageSrc.getEntity();
				if(!entityLiving.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
					heldItem = entityLiving.getItemInHand(InteractionHand.MAIN_HAND);
				}
			}
			if(ObjectLists.isAxe(heldItem)) {
				return 2.0F;
			}
		}
        return super.getDamageModifier(damageSrc);
    }
    
    

    @Override
    public float getFallResistance() {
    	return 100;
    }

    public boolean petControlsEnabled() { return false; }

	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.bagSize; }

	public ResourceLocation getTexture() {
		if(this.hasCustomName() && "Wicked Treant".equals(this.getCustomName().getString())) {
			String textureName = this.getTextureName() + "_wicked";
			if (TextureManager.getTexture(textureName) == null)
				TextureManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
			return TextureManager.getTexture(textureName);
		}

		if(this.hasCustomName() && "Salty Tree".equals(this.getCustomName().getString())) {
			String textureName = this.getTextureName() + "_saltytree";
			if (TextureManager.getTexture(textureName) == null)
				TextureManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
			return TextureManager.getTexture(textureName);
		}

		return super.getTexture();
	}

    @OnlyIn(Dist.CLIENT)
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(50, 20, 50).move(0, -10, 0);
    }
}
