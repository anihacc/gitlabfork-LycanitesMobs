package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.List;

public class EntityVolcan extends TameableCreatureEntity implements IMob {

	public int blockMeltingRadius = 2;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVolcan(EntityType<? extends EntityVolcan> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new AttackMeleeGoal(this).setLongMemory(true));
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
    public void livingTick() {
        super.livingTick();

		// Burning Aura Attack:
		if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0) {
			List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 4);
			for(Object entityObj : aoeTargets) {
				LivingEntity target = (LivingEntity)entityObj;
				if(target != this && this.canAttack(target.getType()) && this.canAttack(target) && this.getEntitySenses().canSee(target)) {
					target.setFire(2);
				}
			}
		}

		// Melt Blocks:
		if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0 && this.blockMeltingRadius > 0 && !this.isTamed() && this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
			int range = this.blockMeltingRadius;
			for (int w = -((int) Math.ceil(this.getSize(Pose.STANDING).width) + range); w <= (Math.ceil(this.getSize(Pose.STANDING).width) + range); w++) {
				for (int d = -((int) Math.ceil(this.getSize(Pose.STANDING).width) + range); d <= (Math.ceil(this.getSize(Pose.STANDING).width) + range); d++) {
					for (int h = -((int) Math.ceil(this.getSize(Pose.STANDING).height) + range); h <= Math.ceil(this.getSize(Pose.STANDING).height); h++) {
						Block block = this.getEntityWorld().getBlockState(this.getPosition().add(w, h, d)).getBlock();
						if (block == Blocks.COBBLESTONE || block == Blocks.GRAVEL) {
							BlockState blockState = Blocks.LAVA.getDefaultState().with(FlowingFluidBlock.LEVEL, 5);
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
				this.getEntityWorld().addParticle(ParticleTypes.FLAME, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
				this.getEntityWorld().addParticle(ParticleTypes.DRIPPING_LAVA, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
			}
			if(this.ticksExisted % 10 == 0)
				for(int i = 0; i < 2; ++i) {
					this.getEntityWorld().addParticle(ParticleTypes.FLAME, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
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
        if(target instanceof SilverfishEntity) {
            target.remove();
        }
        
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

	// ========== Perform Command ==========
	@Override
	public boolean performCommand(String command, PlayerEntity player, ItemStack itemStack) {

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
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus") || type.equals("inWall")) return false;
    	    return super.isInvulnerableTo(type, source, damage);
    }
    
    @Override
    public boolean canBurn() { return false; }

	@Override
	public boolean waterDamage() { return true; }
}
