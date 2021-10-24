package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class Skylus extends TameableCreatureEntity implements Enemy {
    
    public Skylus(EntityType<? extends Skylus> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsOnLand = false;
        this.spawnsInWater = true;
        this.hasAttackSound = true;

        this.babySpawnChance = 0.01D;
        this.canGrow = true;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false).setRange(1D));
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(!this.getCommandSenderWorld().isClientSide && this.getControllingPassenger() == null && this.hasPickupEntity()) {

            ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
            if (extendedEntity != null)
                extendedEntity.setPickedUpByEntity(this);
            if (this.tickCount % 100 == 0 && this.getRandom().nextBoolean()) {
                this.dropPickupEntity();
            }
        }
    }

    @Override
    public float getAISpeedModifier() {
    	if(this.getHealth() > (this.getMaxHealth() / 2)) 
    		return 1.0F;
    	return 2.0F;
    }
	
	@Override
	public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;

        Block block = this.getCommandSenderWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
        if(block == Blocks.WATER)
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        if(this.getCommandSenderWorld().isRaining() && this.getCommandSenderWorld().canSeeSkyFromBelowWater(new BlockPos(x, y, z)))
            return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);

        if(this.getTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.waterContact())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }

    @Override
    public boolean isStrongSwimmer() {
        return true;
    }

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

    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;

        if(target instanceof LivingEntity) {
            LivingEntity entityLivingBase = (LivingEntity)target;
            if(this.canPickupEntity(entityLivingBase)) {
                this.pickupEntity(entityLivingBase);
            }
        }
        
        return true;
    }
    
    

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canBreatheAir() {
        return false;
    }

    public float getDamageModifier(DamageSource damageSrc) {
    	if(this.getHealth() > (this.getMaxHealth() / 2))
    		return 0.25F;
    	return 1.0F;
    }

    public boolean petControlsEnabled() { return true; }
    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
}
