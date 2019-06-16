package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.api.IGroupPlant;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityTreant extends EntityCreatureBase implements IMob, IGroupPlant, IGroupHeavy {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTreant(World world) {
        super(world);
        
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
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(3, new EntityAIAttackMelee(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.field_70714_bg.addTask(4, new EntityAIAttackMelee(this));
        //this.field_70714_bg.addTask(5, this.aiSit);
        //this.field_70714_bg.addTask(6, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(7, new EntityAIWander(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));
        //this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        //this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityEnt.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupFire.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class).setCheckSight(false));
        this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        //this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
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
				this.addPotionEffect(new EffectInstance(MobEffects.REGENERATION, 3 * 20, 2));
			else if (this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(this.getPosition()))
				this.addPotionEffect(new EffectInstance(MobEffects.REGENERATION, 3 * 20, 1));
		}
    }
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(EntityEnt.class))
    		return false;
        return super.canAttackClass(targetClass);
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
	    		if(entityPlayer.getHeldItem(EnumHand.MAIN_HAND) != null) {
	    			heldItem = entityPlayer.getHeldItem(EnumHand.MAIN_HAND).getItem();
	    		}
    		}
    		else if(damageSrc.getTrueSource() instanceof EntityLiving) {
	    		EntityLiving entityLiving = (EntityLiving)damageSrc.getTrueSource();
	    		if(entityLiving.getHeldItem(EnumHand.MAIN_HAND) != null) {
	    			heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND).getItem();
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
		if("Wicked Treant".equals(this.getCustomNameTag())) {
			String textureName = this.getTextureName() + "_wicked";
			if (AssetManager.getTexture(textureName) == null)
				AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
			return AssetManager.getTexture(textureName);
		}

		if("Salty Tree".equals(this.getCustomNameTag())) {
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
        return this.getEntityBoundingBox().grow(50, 20, 50).offset(0, -10, 0);
    }
}
