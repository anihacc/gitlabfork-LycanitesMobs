package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupBoss;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityRoa extends RideableCreatureEntity implements IMob {

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
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));
    }

    @Override
    public void loadCreatureFlags() {
        this.whirlpoolRange = this.creatureInfo.getFlag("whirlpoolRange", this.whirlpoolRange);
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
                    if(entity instanceof EntityLivingBase) {
                        EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                        if(entityLivingBase.isPotionActive(ObjectManager.getEffect("weight")) || !this.canAttackEntity(entityLivingBase))
                            continue;
                        if(!entity.isInWater() && !this.spawnEventType.equalsIgnoreCase("sharknado"))
                            continue;
                    }
                    EntityPlayerMP player = null;
                    if (entity instanceof EntityPlayerMP) {
                        player = (EntityPlayerMP) entity;
                        if (player.capabilities.isCreativeMode)
                            continue;
                    }
                    double xDist = this.posX - entity.posX;
                    double zDist = this.posZ - entity.posZ;
                    double xzDist = Math.max(MathHelper.sqrt(xDist * xDist + zDist * zDist), 0.01D);
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
    public void riderEffects(EntityLivingBase rider) {
        rider.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, (5 * 20) + 5, 1));
        super.riderEffects(rider);
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
    public boolean canBreatheAir() {
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
        return 2;
    }

    @Override
    public int getStaminaRecoveryWarmup() {
        return 4 * 20;
    }

    @Override
    public float getStaminaRecoveryMax() {
        return 2.0F;
    }

    // Dismount:
    @Override
    public void onDismounted(Entity entity) {
        super.onDismounted(entity);
        if(entity != null && entity instanceof EntityLivingBase) {
            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 5 * 20, 1));
        }
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }




    // ==================================================
    //                     Pet Control
    // ==================================================
    @Override
    public boolean petControlsEnabled() { return true; }
}
