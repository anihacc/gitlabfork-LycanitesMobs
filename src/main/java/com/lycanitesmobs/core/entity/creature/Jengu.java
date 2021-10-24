package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.HashMap;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;

public class Jengu extends TameableCreatureEntity implements Enemy, IFusable {

    public Jengu(EntityType<? extends Jengu> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.spawnsInWater = true;
        this.hasAttackSound = false;
        this.setupMob();

        this.maxUpStep = 1.0F;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(5.0F));
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(this.getCommandSenderWorld().isClientSide)
	        for(int i = 0; i < 2; ++i) {
                this.getCommandSenderWorld().addParticle(ParticleTypes.CURRENT_DOWN, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
                this.getCommandSenderWorld().addParticle(ParticleTypes.DRIPPING_WATER, this.position().x() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, this.position().y() + this.random.nextDouble() * (double)this.getDimensions(Pose.STANDING).height, this.position().z() + (this.random.nextDouble() - 0.5D) * (double)this.getDimensions(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
            }
    }

    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("aquapulse", target, range, 0, new Vec3(0, 0, 0), 0.6f, 2f, 1F);
        super.attackRanged(target, range);
    }


    @Override
    public float getAISpeedModifier() {
        if(this.isInWater())
            return 1F;
        if(this.waterContact())
            return 0.75F;
        return 0.5F;
    }

    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return true; }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    

    public boolean petControlsEnabled() { return true; }
    
    

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
    
    @Override
    public boolean canBurn() { return false; }

    @Override
    public HashMap<Integer, String> getInteractCommands(Player player, ItemStack itemStack) {
        HashMap<Integer, String> commands = new HashMap<>();
        commands.putAll(super.getInteractCommands(player, itemStack));

        if(itemStack != null) {
            if(itemStack.getItem() == Items.BUCKET && this.isTamed())
                commands.put(COMMAND_PIORITIES.ITEM_USE.id, "Water");
        }

        return commands;
    }

    @Override
    public boolean performCommand(String command, Player player, ItemStack itemStack) {

        if(command.equals("Water")) {
            this.replacePlayersItem(player, itemStack, new ItemStack(Items.WATER_BUCKET));
            return true;
        }

        return super.performCommand(command, player, itemStack);
    }
    protected IFusable fusionTarget;

    @Override
    public IFusable getFusionTarget() {
        return this.fusionTarget;
    }

    @Override
    public void setFusionTarget(IFusable fusionTarget) {
        this.fusionTarget = fusionTarget;
    }

    @Override
    public EntityType<? extends LivingEntity> getFusionType(IFusable fusable) {
        if(fusable instanceof Cinder) {
            return CreatureManager.getInstance().getEntityType("xaphan");
        }
        if(fusable instanceof Geonach) {
            return CreatureManager.getInstance().getEntityType("spriggan");
        }
        if(fusable instanceof Zephyr) {
            return CreatureManager.getInstance().getEntityType("reiver");
        }
        if(fusable instanceof Aegis) {
            return CreatureManager.getInstance().getEntityType("nymph");
        }
        if(fusable instanceof Argus) {
            return CreatureManager.getInstance().getEntityType("eechetik");
        }
        return null;
    }
}
