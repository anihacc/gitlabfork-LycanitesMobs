package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.List;

public class EntityVolcan extends EntityCreatureTameable implements IMob, IGroupRock, IGroupFire {

	private EntityAIAttackMelee meleeAttackAI;

	public int volcanMeltRadius = 2;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityVolcan(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        
        this.volcanMeltRadius = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getInt("Features", "Volcan Block Melting Radius", this.volcanMeltRadius, "Controls how far Volcans melt blocks, set to 0 to disable.");
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.meleeAttackAI = new EntityAIAttackMelee(this).setLongMemory(true);
        this.field_70714_bg.addTask(2, meleeAttackAI);
        this.field_70714_bg.addTask(3, this.aiSit);
        this.field_70714_bg.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(8, new EntityAIWander(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
		this.field_70715_bh.addTask(2, new EntityAITargetAttack(this).setTargetClass(IGroupIce.class));
		this.field_70715_bh.addTask(2, new EntityAITargetAttack(this).setTargetClass(IGroupWater.class));
		this.field_70715_bh.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntitySnowman.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(EntitySilverfish.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
		this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(IGroupPlant.class));
        this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
    }

    // ========== Set Size ==========
    @Override
    public void setSize(float width, float height) {
        if(this.getSubspeciesIndex() == 3) {
            super.setSize(width * 2, height * 2);
            return;
        }
        super.setSize(width, height);
    }

    @Override
    public double getRenderScale() {
        if(this.getSubspeciesIndex() == 3) {
            return this.sizeScale * 2;
        }
        return this.sizeScale;
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
				if(target != this && !(target instanceof IGroupFire) && this.canAttackClass(entityObj.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
					target.setFire(2);
				}
			}
		}

		// Melt Blocks:
		if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0 && this.volcanMeltRadius > 0 && !this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
			int range = this.volcanMeltRadius;
			for (int w = -((int) Math.ceil(this.width) + range); w <= (Math.ceil(this.width) + range); w++) {
				for (int d = -((int) Math.ceil(this.width) + range); d <= (Math.ceil(this.width) + range); d++) {
					for (int h = -((int) Math.ceil(this.height) + range); h <= Math.ceil(this.height); h++) {
						Block block = this.getEntityWorld().getBlockState(this.getPosition().add(w, h, d)).getBlock();
						if (block == Blocks.OBSIDIAN || block == Blocks.COBBLESTONE || block == Blocks.GRAVEL) {
							BlockState blockState = Blocks.FLOWING_LAVA.getDefaultState().withProperty(BlockLiquid.LEVEL, 5);
							if (block == Blocks.OBSIDIAN)
								blockState = Blocks.LAVA.getDefaultState();
							this.getEntityWorld().setBlockState(this.getPosition().add(w, h, d), blockState);
						}
						/*else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.ICE || block == Blocks.SNOW_LAYER) {
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
	public void performCommand(String command, PlayerEntity player, ItemStack itemStack) {

		// Water:
		if(command.equals("Water")) {
			this.replacePlayersItem(player, itemStack, new ItemStack(Items.LAVA_BUCKET));
		}

		super.performCommand(command, player, itemStack);
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
