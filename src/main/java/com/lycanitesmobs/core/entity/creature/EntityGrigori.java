package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.CopyMasterAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityGrigori extends TameableCreatureEntity implements IMob {
    public EntityGrigori(World world) {
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
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setSpeed(2.0D).setLongMemory(false));

		this.targetTasks.addTask(this.nextFindTargetIndex++, new FindMasterGoal(this).setTargetClass(EntityGrell.class).setSightCheck(false));
		this.targetTasks.addTask(this.nextFindTargetIndex++, new CopyMasterAttackTargetGoal(this));
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

	@Override
	public boolean rollWanderChance() {
		return this.getRNG().nextDouble() <= 0.25D;
	}
	
	public boolean isFlying() { return true; }

    @Override
    public boolean canBurn() { return false; }
}
