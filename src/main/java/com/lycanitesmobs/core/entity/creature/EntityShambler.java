package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupFire;
import com.lycanitesmobs.api.IGroupPlant;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityShambler extends EntityCreatureTameable implements IMob, IGroupPlant {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityShambler(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsUnderground = true;
        this.hasAttackSound = true;
        this.spreadFire = true;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(3, new EntityAIAttackMelee(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.field_70714_bg.addTask(4, new EntityAIAttackMelee(this));
        this.field_70714_bg.addTask(5, this.aiSit);
        this.field_70714_bg.addTask(6, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(7, new EntityAITempt(this).setTemptDistanceMin(2.0D));
        this.field_70714_bg.addTask(8, new EntityAIWander(this));
        this.field_70714_bg.addTask(10, new EntityAIBeg(this));
        this.field_70714_bg.addTask(11, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(12, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetRevenge(this).setHelpClasses(EntityTreant.class));
        this.field_70715_bh.addTask(2, new EntityAITargetAttack(this).setTargetClass(IGroupFire.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
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
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;
    	
    	// Leech:
    	float leeching = this.getEffectStrength(this.getAttackDamage(damageScale) / 2);
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
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityShambler(this.getEntityWorld());
	}
    
    
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
    	return ObjectLists.inItemList("vegetables", testStack) || ObjectLists.inItemList("fruit", testStack);
    }
}
