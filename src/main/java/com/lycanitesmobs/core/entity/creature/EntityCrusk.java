package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.material.Material;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCrusk extends EntityCreatureTameable implements IGroupPredator, IGroupHeavy {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCrusk(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.babySpawnChance = 0.25D;
        this.growthTime = -120000;
        this.setupMob();
        this.hitAreaWidthScale = 1.5F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(1, new EntityAIStealth(this).setStealthTime(60));
        this.field_70714_bg.addTask(2, this.aiSit);
        this.field_70714_bg.addTask(3, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(4, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(5, new EntityAIAttackMelee(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.field_70714_bg.addTask(6, new EntityAIAttackMelee(this));
        this.field_70714_bg.addTask(7, new EntityAIWander(this));
        this.field_70714_bg.addTask(9, new EntityAIBeg(this));

        this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetRevenge(this));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupAlpha.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class));
            this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(AnimalEntity.class));
        }
        this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    
    // ==================================================
   	//                      Stealth
   	// ==================================================
    @Override
    public boolean canStealth() {
    	if(this.isTamed() && this.isSitting())
    		return false;
        BlockState blockState = this.getEntityWorld().getBlockState(this.getPosition().add(0, -1, 0));
        if(blockState.getBlock() != Blocks.AIR) {
        	if(blockState.getMaterial() == Material.GROUND) return true;
        	if(blockState.getMaterial() == Material.GRASS) return true;
        	if(blockState.getMaterial() == Material.LEAVES) return true;
        	if(blockState.getMaterial() == Material.SAND) return true;
        	if(blockState.getMaterial() == Material.CLAY) return true;
        	if(blockState.getMaterial() == Material.SNOW) return true;
        	if(blockState.getMaterial() == Material.CRAFTED_SNOW) return true;
        }
        if(blockState.getBlock() == Blocks.NETHERRACK)
            return true;
    	return false;
    }
    
    
    // ==================================================
   	//                     Abilities
   	// ==================================================
    public boolean canBeTempted() {
    	return this.isChild();
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	if(type.equals("inWall")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityCrusk(this.getEntityWorld());
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
    	return ObjectLists.inItemList("cookedmeat", testStack);
    }
}
