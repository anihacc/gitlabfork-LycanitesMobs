package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ExtendedEntity;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntitySkylus extends EntityCreatureTameable implements IMob, IGroupPredator {
	
	EntityAIWander wanderAI;
    public EntityAIAttackMelee attackAI;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntitySkylus(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0.01D;
        this.canGrow = true;
        this.setupMob();

        // Stats:
        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(1, new EntityAIStayByWater(this));
        this.field_70714_bg.addTask(2, this.aiSit);
        this.attackAI = new EntityAIAttackMelee(this).setLongMemory(false).setRange(1D);
        this.field_70714_bg.addTask(3, this.attackAI);
        this.field_70714_bg.addTask(4, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.wanderAI = new EntityAIWander(this);
        this.field_70714_bg.addTask(6, wanderAI);
        this.field_70714_bg.addTask(9, new EntityAIBeg(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class).setPackHuntingScale(1, 3));
            this.field_70715_bh.addTask(5, new EntityAITargetAttack(this).setTargetClass(AnimalEntity.class).setPackHuntingScale(1, 3));
        }
        this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
    }
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Wander Pause Rates:
        if(!this.getEntityWorld().isRemote) {
            if (this.isInWater())
                this.wanderAI.setPauseRate(20);
            else
                this.wanderAI.setPauseRate(0);
        }

        // Entity Pickup Update:
        if(!this.getEntityWorld().isRemote && this.getControllingPassenger() == null && this.hasPickupEntity()) {

            // Random Dropping:
            ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
            if (extendedEntity != null)
                extendedEntity.setPickedUpByEntity(this);
            if (this.ticksExisted % 100 == 0 && this.getRNG().nextBoolean()) {
                this.dropPickupEntity();
            }
        }
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
	// ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
    	if(this.getHealth() > (this.getMaxHealth() / 2)) // Slower with shell.
    		return 1.0F;
    	return 2.0F;
    }
	
    // Pathing Weight:
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;

        Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
        if(block == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(block == Blocks.FLOWING_WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * waterWeight;
        if(this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z)))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getAttackTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }


    // ==================================================
    //                     Abilities
    // ==================================================
    // Swimming:
    @Override
    public boolean isStrongSwimmer() {
        return true;
    }

    // Walking:
    @Override
    public boolean canWalk() {
        return false;
    }

    @Override
    public void pickupEntity(LivingEntity entity) {
        super.pickupEntity(entity);
        this.leap(-1.0F, -0.5D);
    }

    @Override
    public double[] getPickupOffset(Entity entity) {
        return new double[]{0, 0, 2D};
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;

        // Pickup:
        if(target instanceof LivingEntity) {
            LivingEntity entityLivingBase = (LivingEntity)target;
            if(this.canPickupEntity(entityLivingBase)) {
                this.pickupEntity(entityLivingBase);
            }
        }
        
        return true;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBreatheAboveWater() {
        return false;
    }
    
    
    // ==================================================
   	//                    Taking Damage
   	// ==================================================
    // ========== Damage Modifier ==========
    /** A multiplier that alters how much damage this mob receives from the given DamageSource, use for resistances and weaknesses. Note: The defense multiplier is handled before this. **/
    public float getDamageModifier(DamageSource damageSrc) {
    	if(this.getHealth() > (this.getMaxHealth() / 2)) // Stronger with shell.
    		return 0.25F;
    	return 1.0F;
    }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}