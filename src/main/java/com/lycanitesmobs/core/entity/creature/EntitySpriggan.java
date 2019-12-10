package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.projectile.EntityLifeDrain;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class EntitySpriggan extends TameableCreatureEntity implements IMob {
	public int farmingRate = 20;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySpriggan(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = false;

        this.setupMob();

        this.stepHeight = 1.0F;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F));
    }

	@Override
	public void loadCreatureFlags() {
		this.farmingRate = this.creatureInfo.getFlag("farmingRate", this.farmingRate);
	}


    // ==================================================
    //                      Updates
    // ==================================================
	private int farmingTick = 0;
    // ========== Living Update ==========
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

		// Water Healing:
		if(this.getAir() >= 0) {
			if (this.isInWater())
				this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 3 * 20, 2));
			else if (this.waterContact())
				this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 3 * 20, 1));
		}

		// Farming:
		int currentFarmingRate = this.farmingRate;
		if(this.isTamed() && currentFarmingRate > 0) {
			if(this.isPetType("familiar"))
				currentFarmingRate = this.farmingRate * 10;
			if(this.subspecies != null)
				currentFarmingRate = Math.max(1, Math.round((float)this.farmingRate / 3));
			this.farmingTick++;
			int farmingRange = 16;
			int farmingHeight = 4;
			for(int x = (int)this.posX - farmingRange; x <= (int)this.posX + farmingRange; x++) {
				for(int y = (int)this.posY - farmingHeight; y <= (int)this.posY + farmingHeight; y++) {
					for(int z = (int)this.posZ - farmingRange; z <= (int)this.posZ + farmingRange; z++) {
						BlockPos pos = new BlockPos(x, y, z);
						Block farmingBlock = this.getEntityWorld().getBlockState(pos).getBlock();
						if(farmingBlock instanceof IPlantable && farmingBlock instanceof IGrowable && farmingBlock != Blocks.TALLGRASS && farmingBlock != Blocks.DOUBLE_PLANT) {

							// Boost Crops Every X Seconds:
							if(!this.getEntityWorld().isRemote && this.farmingTick % (currentFarmingRate) == 0) {
								if(farmingBlock.getTickRandomly()) {
									this.getEntityWorld().scheduleBlockUpdate(pos, farmingBlock, currentFarmingRate, 1);
								}
		    	        		/*IGrowable growableBlock = (IGrowable)farmingBlock;
		    	        		if(growableBlock.func_149851_a(this.getEntityWorld(), x, y, z, this.getEntityWorld().isRemote)) {
	    	                        if(growableBlock.func_149852_a(this.getEntityWorld(), this.getRNG(), x, y, z)) {
	    	                        	growableBlock.func_149853_b(this.getEntityWorld(), this.getRNG(), x, y, z);
	    	                        }
		    	                }*/
							}

							// Crop Growth Effect:
							if(this.getEntityWorld().isRemote && this.farmingTick % 40 == 0) {
								double d0 = this.getRNG().nextGaussian() * 0.02D;
								double d1 = this.getRNG().nextGaussian() * 0.02D;
								double d2 = this.getRNG().nextGaussian() * 0.02D;
								this.getEntityWorld().spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, (double)((float)x + this.getRNG().nextFloat()), (double)y + (double)this.getRNG().nextFloat(), (double)((float)z + this.getRNG().nextFloat()), d0, d1, d2);
							}
						}
					}
				}
			}
		}

		// Particles:
		if(this.getEntityWorld().isRemote && !CreatureManager.getInstance().config.disableBlockParticles)
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().spawnParticle(EnumParticleTypes.BLOCK_CRACK,
						this.posX + (this.rand.nextDouble() - 0.5D) * (double) this.width,
						this.posY + this.rand.nextDouble() * (double) this.height,
						this.posZ + (this.rand.nextDouble() - 0.5D) * (double) this.width,
						0.0D, 0.0D, 0.0D,
						Blocks.TALLGRASS.getStateId(Blocks.TALLGRASS.getDefaultState()));
			}
    }


    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    EntityLifeDrain projectile = null;
    @Override
    public void attackRanged(Entity target, float range) {
    	// Update Laser:
    	if(this.projectile != null && this.projectile.isEntityAlive()) {
    		this.projectile.setTime(20);
    	}
    	else {
    		this.projectile = null;
    	}
    	
    	// Create New Laser:
    	if(this.projectile == null) {
	    	// Type:
	    	this.projectile = new EntityLifeDrain(this.getEntityWorld(), this, 25, 20);
	    	
	    	// Launch:
	        this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	        this.getEntityWorld().spawnEntity(projectile);
    	}

    	super.attackRanged(target, range);
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    public float getDamageModifier(DamageSource damageSrc) {
        if(damageSrc.isFireDamage())
            return 2.0F;
		if(damageSrc.getTrueSource() != null) {
			ItemStack heldItem = ItemStack.EMPTY;
			if(damageSrc.getTrueSource() instanceof EntityLivingBase) {
				EntityLivingBase entityLiving = (EntityLivingBase)damageSrc.getTrueSource();
				if(!entityLiving.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
					heldItem = entityLiving.getHeldItem(EnumHand.MAIN_HAND);
				}
			}
			if(ObjectLists.isAxe(heldItem)) {
				return 2.0F;
			}
		}
        return super.getDamageModifier(damageSrc);
    }


    // ==================================================
    //                     Abilities
    // ==================================================
    @Override
    public boolean isFlying() { return true; }


    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 5; }
}
