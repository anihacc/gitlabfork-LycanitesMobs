package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityJoustAlpha extends EntityCreatureAgeable implements IAnimals, IGroupAlpha {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityJoustAlpha(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.attackCooldownMax = 10;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(3, new EntityAIAttackMelee(this).setLongMemory(false));
        this.field_70714_bg.addTask(4, new EntityAIFollowParent(this).setSpeed(1.0D));
        this.field_70714_bg.addTask(6, new EntityAIWander(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));
        this.field_70715_bh.addTask(0, new EntityAITargetRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetAttack(this).setTargetClass(EntityJoustAlpha.class));
        this.field_70715_bh.addTask(2, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(2, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(IGroupPredator.class));
        this.field_70715_bh.addTask(4, new EntityAITargetParent(this).setSightCheck(false).setDistance(32.0D));
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        BlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
        if(blockState.getBlock() != Blocks.AIR) {
            if(blockState.getMaterial() == Material.SAND)
                return 10F;
            if(blockState.getMaterial() == Material.CLAY)
                return 7F;
            if(blockState.getMaterial() == Material.ROCK)
                return 5F;
        }
        return super.getBlockPathWeight(x, y, z);
    }
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Set Attack Target ==========
    @Override
    public void setAttackTarget(LivingEntity entity) {
    	if(entity == null && this.getAttackTarget() instanceof EntityJoustAlpha && this.getHealth() < this.getMaxHealth()) {
    		this.heal((this.getMaxHealth() - this.getHealth()) / 2);
    		this.addPotionEffect(new EffectInstance(MobEffects.REGENERATION, 10 * 20, 2, false, true));
    	}
    	super.setAttackTarget(entity);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus"))
    		return false;
    	return super.isInvulnerableTo(type, source, damage);
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable partner) {
		return new EntityJoust(this.getEntityWorld());
	}
}