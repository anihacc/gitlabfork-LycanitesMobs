package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.*;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntityMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityRoa extends EntityCreatureRideable implements IMob, IGroupPredator {

	EntityAIWander wanderAI;
    protected int whirlpoolRange = 6;
    protected int whirlpoolEnergy = 0;
    protected int whirlpoolEnergyMax = 5 * 20;
    protected boolean whirlpoolRecharging = true;
    protected int mountedWhirlpool = 0;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityRoa(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0D;
        this.canGrow = true;
        this.setupMob();

        this.whirlpoolRange = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getInt("Features", "Roa Whirlpool Range", this.whirlpoolRange, "The range (in blocks) of the Roa's whirlpool pull effect, set to 0 to disable, note that the Roa is nearly 2 blocks in size itself which the range must cover.");
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAIStayByWater(this));
        this.tasks.addTask(2, new EntityAIPlayerControl(this));
        this.tasks.addTask(3, new EntityAITempt(this).setTemptDistanceMin(4.0D));
        this.tasks.addTask(4, new EntityAIAttackMelee(this).setLongMemory(false));
        this.tasks.addTask(5, this.aiSit);
        this.tasks.addTask(6, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.wanderAI = new EntityAIWander(this);
        this.tasks.addTask(7, wanderAI.setPauseRate(60));
        this.tasks.addTask(9, new EntityAIBeg(this));
        this.tasks.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.tasks.addTask(11, new EntityAILookIdle(this));

        this.targetTasks.addTask(0, new EntityAITargetRiderRevenge(this));
        this.targetTasks.addTask(1, new EntityAITargetRiderAttack(this));
        this.targetTasks.addTask(2, new EntityAITargetOwnerRevenge(this));
        this.targetTasks.addTask(3, new EntityAITargetOwnerAttack(this));
        this.targetTasks.addTask(4, new EntityAITargetOwnerThreats(this));
        this.targetTasks.addTask(5, new EntityAITargetRevenge(this).setHelpCall(true));
        this.targetTasks.addTask(6, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.targetTasks.addTask(7, new EntityAITargetAttack(this).setTargetClass(EntityVillager.class));
        this.targetTasks.addTask(8, new EntityAITargetAttack(this).setTargetClass(IGroupPrey.class));
        if(CreatureManager.getInstance().config.predatorsAttackAnimals) {
            this.targetTasks.addTask(8, new EntityAITargetAttack(this).setTargetClass(IGroupAnimal.class));
            this.targetTasks.addTask(8, new EntityAITargetAttack(this).setTargetClass(EntityAnimal.class));
            this.targetTasks.addTask(8, new EntityAITargetAttack(this).setTargetClass(EntitySquid.class));
        }
        this.targetTasks.addTask(9, new EntityAITargetOwnerThreats(this));
    }
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

		// Whirlpool:
        if(!this.getEntityWorld().isRemote) {
            if(this.whirlpoolRecharging) {
                if(++this.whirlpoolEnergy >= this.whirlpoolEnergyMax)
                    this.whirlpoolRecharging = false;
            }
            this.whirlpoolEnergy = Math.min(this.whirlpoolEnergy, this.whirlpoolEnergyMax);
            if(this.canWhirlpool()) {
                for (Entity entity : this.getNearbyEntities(Entity.class, null, this.whirlpoolRange)) {
                    if (entity == this || entity.getClass() == this.getClass() || entity == this.getControllingPassenger() || entity instanceof IGroupBoss || entity instanceof IGroupHeavy)
                        continue;
                    if(entity instanceof LivingEntity) {
                        LivingEntity entityLivingBase = (LivingEntity)entity;
                        if(entityLivingBase.isPotionActive(ObjectManager.getEffect("weight")) || !this.canAttackEntity(entityLivingBase))
                            continue;
                        if(!entity.isInWater() && !this.spawnEventType.equalsIgnoreCase("sharknado"))
                            continue;
                    }
                    PlayerEntityMP player = null;
                    if (entity instanceof PlayerEntityMP) {
                        player = (PlayerEntityMP) entity;
                        if (player.capabilities.isCreativeMode)
                            continue;
                    }
                    double xDist = this.posX - entity.posX;
                    double zDist = this.posZ - entity.posZ;
                    double xzDist = MathHelper.sqrt(xDist * xDist + zDist * zDist);
                    double factor = 0.1D;
                    double motionCap = 10;
                    if(entity.motionX < motionCap && entity.motionX > -motionCap && entity.motionZ < motionCap && entity.motionZ > -motionCap) {
                        entity.addVelocity(
                                xDist / xzDist * factor + entity.motionX * factor,
                                0,
                                zDist / xzDist * factor + entity.motionZ * factor
                        );
                    }
                    if (player != null)
                        player.connection.sendPacket(new SPacketEntityVelocity(entity));
                }
                if(--this.whirlpoolEnergy <= 0)
                    this.whirlpoolRecharging = true;
            }
        }

        if(this.mountedWhirlpool > 0)
            this.mountedWhirlpool--;
    }

    @Override
    public void riderEffects(LivingEntity rider) {
        rider.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, (5 * 20) + 5, 1));
        if(rider.isPotionActive(MobEffects.BLINDNESS))
            rider.removePotionEffect(MobEffects.BLINDNESS);
        if(rider.isPotionActive(ObjectManager.getEffect("weight")))
            rider.removePotionEffect(ObjectManager.getEffect("weight"));
    }

    // ========== Extra Animations ==========
    /** An additional animation boolean that is passed to all clients through the animation mask. **/
    public boolean extraAnimation01() {
        if(this.getEntityWorld().isRemote) {
            return super.extraAnimation01();
        }
        return this.canWhirlpool();
    }

    // ========== Whirlpool ==========
    public boolean canWhirlpool() {
        if(this.getEntityWorld().isRemote) {
            return this.extraAnimation01();
        }

        // Sharknado:
        if("sharknado".equals(this.spawnEventType)) {
            return true;
        }

        // Out of Water:
        if(!this.isInWater()) {
            return false;
        }

        // Mounted:
        if(this.getControllingPassenger() != null && this.mountedWhirlpool > 0) {
            return true;
        }

        // Attack Target:
        return !this.whirlpoolRecharging && this.hasAttackTarget() && this.getDistance(this.getAttackTarget()) <= (this.whirlpoolRange * 3);
    }

	
    // ==================================================
    //                      Movement
    // ==================================================
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

    // Flying:
    @Override
    public boolean isFlying() {
        if("sharknado".equals(this.spawnEventType)) {
            return true;
        }
        return super.isFlying();
    }

    // ========== Mounted Offset ==========
    @Override
    public double getMountedYOffset() {
        return (double)this.height * 0.25D;
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
        return "sharknado".equals(this.spawnEventType);
    }


    // ==================================================
    //                   Mount Ability
    // ==================================================
    @Override
    public void mountAbility(Entity rider) {
        if(this.getEntityWorld().isRemote)
            return;

        if(this.getStamina() < this.getStaminaCost()) {
            return;
        }

        this.applyStaminaCost();
        this.mountedWhirlpool = 20;
    }

    @Override
    public float getStaminaCost() {
        return 1;
    }

    @Override
    public int getStaminaRecoveryWarmup() {
        return 0;
    }

    @Override
    public float getStaminaRecoveryMax() {
        return 2.0F;
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }

    // Dismount:
    @Override
    public void onDismounted(Entity entity) {
        super.onDismounted(entity);
        if(entity != null && entity instanceof LivingEntity) {
            ((LivingEntity)entity).addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 5 * 20, 1));
        }
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return 10; }


    // ==================================================
    //                      Breeding
    // ==================================================
    // ========== Create Child ==========
    @Override
    public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
        return new EntityRoa(this.getEntityWorld());
    }


    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("cookedmeat", testStack) || ObjectLists.inItemList("cookedfish", testStack);
    }


    // ==================================================
    //                     Pet Control
    // ==================================================
    @Override
    public boolean petControlsEnabled() { return true; }
}
