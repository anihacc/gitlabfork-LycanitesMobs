package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityJoustAlpha extends AgeableCreatureEntity {
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityJoustAlpha(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.attackCooldownMax = 10;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
		this.targetTasks.addTask(this.nextFindTargetIndex++, new FindAttackTargetGoal(this).addTargets(this.getClass()));

		super.initEntityAI();

		this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
    }
	
	
	// ==================================================
   	//                      Movement
   	// ==================================================
	// Pathing Weight:
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
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
    public void setAttackTarget(EntityLivingBase entity) {
    	if(entity == null && this.getAttackTarget() instanceof EntityJoustAlpha && this.getHealth() < this.getMaxHealth()) {
    		this.heal((this.getMaxHealth() - this.getHealth()) / 2);
    		this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 10 * 20, 2, false, true));
    	}
    	super.setAttackTarget(entity);
    }

	@Override
	public boolean isProtective(Entity entity) {
		if(entity instanceof EntityJoust) {
			return true;
		}
		return super.isProtective(entity);
	}
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("cactus"))
    		return false;
    	return super.isDamageTypeApplicable(type, source, damage);
    }
    
    
    // ==================================================
    //                     Breeding
    // ==================================================
    // ========== Create Child ==========
	@Override
	public AgeableCreatureEntity createChild(AgeableCreatureEntity partner) {
		return (AgeableCreatureEntity) CreatureManager.getInstance().getCreature("joust").createEntity(this.getEntityWorld());
	}
}
