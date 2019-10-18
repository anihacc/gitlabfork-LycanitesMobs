package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import com.lycanitesmobs.core.entity.projectile.EntityBoulderBlast;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityTroll extends TameableCreatureEntity implements IMob {
	
	public boolean trollGreifing = true; // TODO Creature flags.
	
	// ========== Unique Entity Variables ==========
	public boolean stoneForm = false;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityTroll(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

        //this.canGrow = false;
        //this.babySpawnChance = 0.01D;
        
        this.solidCollision = true;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
		this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));

		if(this.getNavigator() instanceof PathNavigateGround) {
			PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
			pathNavigateGround.setBreakDoors(true);
		}
    }
    
    
    // ==================================================
    //                       Name
    // ==================================================
    public String getTextureName() {
    	if(this.stoneForm)
    		return super.getTextureName() + "_stone";
    	return super.getTextureName();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Daylight Stone Form:
        if(!this.stoneForm) {
        	if(this.isDaytime() && this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
        		this.stoneForm = true;
        	}
        }
        else {
        	if(!this.isDaytime() || !this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
                this.stoneForm = false;
            }
        }
        
        // Destroy Blocks:
 		if(!this.getEntityWorld().isRemote)
 	        if(this.getAttackTarget() != null && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.trollGreifing) {
 		    	float distance = this.getAttackTarget().getDistance(this);
 		    		if(distance <= this.width + 4.0F)
 		    			this.destroyArea((int)this.posX, (int)this.posY, (int)this.posZ, 0.5F, true);
 	        }
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(this.stoneForm) // Slower in stone form.
    		return 0.125F;
    	return 1.0F;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	// ========== Ranged Attack ==========
	@Override
	public void attackRanged(Entity target, float range) {
		this.fireProjectile(EntityBoulderBlast.class, target, range, 0, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
		super.attackRanged(target, range);
	}
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    /** A multiplier that alters how much damage this mob receives from the given DamageSource, use for resistances and weaknesses. Note: The defense multiplier is handled before this. **/
    public float getDamageModifier(DamageSource damageSrc) {
		if(damageSrc.getTrueSource() != null) {
			ItemStack heldItem = ItemStack.EMPTY;
			if(damageSrc.getTrueSource() instanceof EntityLivingBase) {
				EntityLivingBase entityLiving = (EntityLivingBase)damageSrc.getTrueSource();
				if(!entityLiving.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
					heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND);
				}
			}
			if(ObjectLists.isPickaxe(heldItem.getItem())) {
				return 3.0F;
			}
		}
		return super.getDamageModifier(damageSrc);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 50;
    }
    
    @Override
    public boolean canBurn() { return !this.stoneForm; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
