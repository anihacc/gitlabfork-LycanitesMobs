package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.projectile.EntityAcidSplash;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

public class EntityXaphan extends TameableCreatureEntity implements IMob {
	private int nextSplash = 20;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityXaphan(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.spawnsInWater = true;
        this.hasAttackSound = false;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(10.0F));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

		if(!this.getEntityWorld().isRemote && this.updateTick % this.nextSplash == 0) {
			this.fireProjectile(EntityAcidSplash.class, null, 0, 0, new Vec3d(0.5D - this.getRNG().nextDouble(), 0, 0.5D - this.getRNG().nextDouble()), 0f, (float)this.nextSplash / 20, 1F);
			this.nextSplash = 20 + this.getRNG().nextInt(20);
		}
        
        /*/ Particles:
        if(this.getEntityWorld().isRemote)
	        for(int i = 0; i < 2; ++i) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
                this.getEntityWorld().spawnParticle(EnumParticleTypes.DRIP_WATER, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
            }*/
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	@Override
	public boolean isDamageEntityApplicable(Entity entity) {
		if(entity instanceof EntityXaphan && this.getPlayerOwner() == ((EntityXaphan)entity).getPlayerOwner()) {
			return false;
		}
		return super.isDamageEntityApplicable(entity);
	}
    
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        for(int row = -1; row <= 1; row++) {
			int projectileCount = 10;
			for (int i = 0; i < projectileCount; i++) {
				this.fireProjectile(EntityAcidSplash.class, target, range, (90 / projectileCount) * i, new Vec3d(0, 3 * row, 0), 0.6f, 2f, 1F);
			}
		}
        super.attackRanged(target, range);
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return true; }


    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }


    // ==================================================
    //                     Interact
    // ==================================================
    // ========== Get Interact Commands ==========
    @Override
    public HashMap<Integer, String> getInteractCommands(EntityPlayer player, ItemStack itemStack) {
        HashMap<Integer, String> commands = new HashMap<>();
        commands.putAll(super.getInteractCommands(player, itemStack));

        if(itemStack != null) {
            // Water:
            if(itemStack.getItem() == Items.BUCKET && this.isTamed())
                commands.put(COMMAND_PIORITIES.ITEM_USE.id, "Water");
        }

        return commands;
    }


	// ==================================================
	//                   Brightness
	// ==================================================
	@Override
	public float getBrightness() {
		return 1.0F;
	}

	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender() {
		return 15728880;
	}
}
