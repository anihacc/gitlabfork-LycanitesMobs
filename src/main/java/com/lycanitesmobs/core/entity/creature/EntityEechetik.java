package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.EffectBase;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import net.minecraft.block.BlockDirt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.util.List;

public class EntityEechetik extends EntityCreatureTameable implements IMob {

	private EntityAIAttackMelee meleeAttackAI;

	public int eechetikMyceliumRadius = 2;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityEechetik(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        
        this.eechetikMyceliumRadius = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getInt("Features", "Eechetik Mycelium Radius", this.eechetikMyceliumRadius, "Controls how far Volcans melt blocks, set to 0 to disable.");
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
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
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

		// Plague Aura Attack:
		if(!this.getEntityWorld().isRemote && this.updateTick % 40 == 0 && this.hasAttackTarget()) {
			EffectBase plague = ObjectManager.getEffect("plague");
			if(plague != null) {
				EffectInstance potionEffect = new EffectInstance(plague, this.getEffectDuration(5), 1);
				List aoeTargets = this.getNearbyEntities(LivingEntity.class, null, 4);
				for(Object entityObj : aoeTargets) {
					LivingEntity target = (LivingEntity) entityObj;
					if (target != this && this.canAttackClass(entityObj.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target) && target.isPotionApplicable(potionEffect)) {
						target.addPotionEffect(potionEffect);
					}
				}
			}
		}

		// Grow Mycelium:
		if(!this.getEntityWorld().isRemote && this.updateTick % 100 == 0 && this.eechetikMyceliumRadius > 0 && !this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
			int range = this.eechetikMyceliumRadius;
			for (int w = -((int) Math.ceil(this.width) + range); w <= (Math.ceil(this.width) + range); w++) {
				for (int d = -((int) Math.ceil(this.width) + range); d <= (Math.ceil(this.width) + range); d++) {
					for (int h = -((int) Math.ceil(this.height) + range); h <= Math.ceil(this.height); h++) {
						BlockPos blockPos = this.getPosition().add(w, h, d);
						BlockState blockState = this.getEntityWorld().getBlockState(blockPos);
						BlockState upperBlockState = this.getEntityWorld().getBlockState(blockPos.up());
						if (upperBlockState.getBlock() == Blocks.AIR && blockState.getBlock() == Blocks.DIRT && blockState.getValue(BlockDirt.VARIANT) == BlockDirt.DirtType.DIRT) {
							this.getEntityWorld().setBlockState(blockPos, Blocks.MYCELIUM.getDefaultState());
						}
					}
				}
			}
		}

		// Particles:
		if(this.getEntityWorld().isRemote) {
			for(int i = 0; i < 2; ++i) {
				this.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2, 0.0D, 0.0D, 0.0D);
				this.getEntityWorld().spawnParticle(EnumParticleTypes.TOWN_AURA, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width * 2, 0.0D, 0.0D, 0.0D);
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
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
	@Override
	public boolean isFlying() { return true; }

	@Override
	public boolean isStrongSwimmer() { return true; }
    
    
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
    	if(type.equals("inWall")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}
}
