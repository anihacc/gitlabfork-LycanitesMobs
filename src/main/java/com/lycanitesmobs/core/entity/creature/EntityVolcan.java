package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;

public class EntityVolcan extends TameableCreatureEntity implements IMob {

	public int blockMeltingRadius = 2;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVolcan(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(2, new AttackMeleeGoal(this).setLongMemory(true));
    }

	@Override
	public void loadCreatureFlags() {
		this.blockMeltingRadius = this.creatureInfo.getFlag("blockMeltingRadius", this.blockMeltingRadius);
	}
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

		// Burning Aura Attack:
		if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0) {
			List aoeTargets = this.getNearbyEntities(EntityLivingBase.class, null, 4);
			for(Object entityObj : aoeTargets) {
				EntityLivingBase target = (EntityLivingBase)entityObj;
				if(target != this && this.canAttackClass(target.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
					target.setFire(2);
				}
			}
		}

		// Melt Blocks:
		if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0 && this.blockMeltingRadius > 0 && !this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
			int range = this.blockMeltingRadius;
			for (int w = -((int) Math.ceil(this.width) + range); w <= (Math.ceil(this.width) + range); w++) {
				for (int d = -((int) Math.ceil(this.width) + range); d <= (Math.ceil(this.width) + range); d++) {
					for (int h = -((int) Math.ceil(this.height) + range); h <= Math.ceil(this.height); h++) {
						Block block = this.getEntityWorld().getBlockState(this.getPosition().add(w, h, d)).getBlock();
						if (block == Blocks.COBBLESTONE || block == Blocks.GRAVEL) {
							IBlockState blockState = Blocks.FLOWING_LAVA.getStateFromMeta(11);
							this.getEntityWorld().setBlockState(this.getPosition().add(w, h, d), blockState);
						}
						/*else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.ICE || block == Blocks.SNOW) {
							this.getEntityWorld().setBlockState(this.getPosition().add(w, h, d), Blocks.AIR.getDefaultState(), 3);
						}*/
					}
				}
			}
		}

		// Particles:
		if(this.getEntityWorld().isRemote) {
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
				this.getEntityWorld().spawnParticle(EnumParticleTypes.DRIP_LAVA, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
			}
			if(this.ticksExisted % 10 == 0)
				for(int i = 0; i < 2; ++i) {
					this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
				}
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

        // Silverfish Extermination:
        if(target instanceof EntitySilverfish) {
            target.setDead();
        }
        
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

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

	// ========== Perform Command ==========
	@Override
	public boolean performCommand(String command, EntityPlayer player, ItemStack itemStack) {

		// Water:
		if(command.equals("Water")) {
			this.replacePlayersItem(player, itemStack, new ItemStack(Items.LAVA_BUCKET));
			return true;
		}

		return super.performCommand(command, player, itemStack);
	}
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }

	// ==================================================
	//                     Equipment
	// ==================================================
	@Override
	public int getNoBagSize() { return 0; }
	@Override
	public int getBagSize() { return this.creatureInfo.BagSize; }


	// ==================================================
   	//                    Taking Damage
   	// ==================================================
	// ========== Damage Modifier ==========
	public float getDamageModifier(DamageSource damageSrc) {
		if(damageSrc.isFireDamage())
			return 0F;
		else return super.getDamageModifier(damageSrc);
	}
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall")) return false;
    	    return super.isDamageTypeApplicable(type, source, damage);
    }
    
    @Override
    public boolean canBurn() { return false; }

	@Override
	public boolean waterDamage() { return true; }
}
