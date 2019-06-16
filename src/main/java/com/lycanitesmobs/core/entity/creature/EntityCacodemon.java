package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupDemon;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.entity.projectile.EntityDemonicBlast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityCacodemon extends EntityCreatureRideable implements IGroupDemon {
    public boolean cacodemonGreifing = true;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCacodemon(World par1World) {
        super(par1World);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = false;

        this.cacodemonGreifing = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getBool("Features", "Cacodemon Griefing", this.cacodemonGreifing, "Set to false to disable Cacodemon projectile explosions.");
        this.setAttackCooldownMax(20);
        this.setupMob();

        this.stepHeight = 1.0F;
        this.hitAreaWidthScale = 1.5F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(1, new EntityAIMate(this));
        this.field_70714_bg.addTask(2, new EntityAIPlayerControl(this));
        this.field_70714_bg.addTask(3, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(4, new EntityAIAttackRanged(this).setSpeed(0.25D).setRange(40.0F).setMinChaseDistance(10.0F).setLongMemory(false));
        this.field_70714_bg.addTask(5, this.aiSit);
        this.field_70714_bg.addTask(6, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(7, new EntityAIFollowParent(this));
        this.field_70714_bg.addTask(8, new EntityAIWander(this).setPauseRate(30));
        this.field_70714_bg.addTask(9, new EntityAIBeg(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(3, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(4, new EntityAITargetOwnerThreats(this));
        this.field_70715_bh.addTask(4, new EntityAITargetRevenge(this));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(EntityGhast.class));
    }

    // ========== Set Size ==========
    @Override
    public void setSizeScale(double scale) {
        if(this.isRareSubspecies()) {
            super.setSizeScale(scale * 1.5D);
            return;
        }
        super.setSizeScale(scale);
    }


    // ==================================================
    //                      Updates
    // ==================================================
    // ========== Living Update ==========
    @Override
    public void livingTick() {
        if(!this.getEntityWorld().isRemote && this.getSubspeciesIndex() == 3 && this.hasAttackTarget() && this.ticksExisted % 20 == 0) {
            this.allyUpdate();
        }

        super.livingTick();
    }

    @Override
    public void riderEffects(LivingEntity rider) {
        if(rider.isPotionActive(MobEffects.WITHER))
            rider.removeEffectInstance(MobEffects.WITHER);
        if(rider.isBurning())
            rider.extinguish();
    }

    // ========== Spawn Minions ==========
    public void allyUpdate() {
        if(this.getEntityWorld().isRemote)
            return;

        // Spawn Minions:
        if(CreatureManager.getInstance().getCreature("wraith").enabled) {
            if (this.nearbyCreatureCount(CreatureManager.getInstance().getCreature("wraith").entityClass, 64D) < 10) {
                float random = this.rand.nextFloat();
                if (random <= 0.1F) {
                    this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
                }
            }
        }
    }

    public void spawnAlly(double x, double y, double z) {
        EntityWraith minion = new EntityWraith(this.getEntityWorld());
        minion.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
		minion.setMinion(true);
		minion.setMasterTarget(this);
        this.getEntityWorld().spawnEntity(minion);
        if(this.getAttackTarget() != null) {
            minion.setRevengeTarget(this.getAttackTarget());
        }
        minion.setSizeScale(this.sizeScale);
    }
	
	
	// ==================================================
  	//                     Abilities
  	// ==================================================
    // ========== Movement ==========
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
	
	
	// ==================================================
   	//                      Attacks
   	// ==================================================
    // ========== Set Attack Target ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
        if(targetClass.isAssignableFrom(EntityTrite.class) || targetClass.isAssignableFrom(EntityAstaroth.class) || targetClass.isAssignableFrom(EntityAsmodeus.class))
            return false;
        if(CreatureManager.getInstance().getCreature("wraith") != null) {
            if (targetClass.isAssignableFrom(CreatureManager.getInstance().getCreature("wraith").entityClass))
                return false;
        }
        return super.canAttackClass(targetClass);
    }

	// ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile(EntityDemonicBlast.class, target, range, 0, new Vec3d(0, 0, 0), 0.6f, 2f, 1F);
        super.attackRanged(target, range);
    }
    
    
    // ==================================================
    //                     Immunities
    // ==================================================
    @Override
    public boolean isInvulnerableTo(Entity entity) {
    	if(entity instanceof EntityCacodemon)
    		return false;
    	return super.isInvulnerableTo(entity);
    }
    
    @Override
    public boolean canBurn() { return false; }
    
    
    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
    	return ObjectLists.inItemList("CookedMeat", testStack);
    }


    // ==================================================
    //                      Movement
    // ==================================================
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.9D;
    }


    // ==================================================
    //                   Mount Ability
    // ==================================================
    @Override
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(this.abilityToggled)
            return;

        if(this.hasPickupEntity()) {
            this.dropPickupEntity();
            return;
        }

        if(this.getStamina() < this.getStaminaCost())
            return;

        if(rider instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)rider;
            EntityDemonicBlast projectile = new EntityDemonicBlast(this.getEntityWorld(), player);
            this.getEntityWorld().spawnEntity(projectile);
            this.playSound(projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.triggerAttackCooldown();
        }

        this.applyStaminaCost();
    }

    public float getStaminaCost() {
        return 10;
    }

    public int getStaminaRecoveryWarmup() {
        return 2 * 20;
    }

    public float getStaminaRecoveryMax() {
        return 1.0F;
    }


    // ==================================================
    //                   Brightness
    // ==================================================
    @Override
    public float getBrightness() {
        if(isAttackOnCooldown())
            return 1.0F;
        else
            return super.getBrightness();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getBrightnessForRender() {
        if(isAttackOnCooldown())
            return 15728880;
        else
            return super.getBrightnessForRender();
    }
}
