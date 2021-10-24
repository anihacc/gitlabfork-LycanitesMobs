package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import com.lycanitesmobs.core.entity.goals.actions.MoveVillageGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class Jabberwock extends TameableCreatureEntity implements Enemy {
    
    public Jabberwock(EntityType<? extends Jabberwock> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;

        this.canGrow = false;
        this.babySpawnChance = 0.01D;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(this.nextTravelGoalIndex++, new MoveVillageGoal(this));

        super.registerGoals();

        this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(Player.class).setLongMemory(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));

        if(this.getNavigation() instanceof GroundPathNavigation) {
            GroundPathNavigation pathNavigateGround = (GroundPathNavigation)this.getNavigation();
            pathNavigateGround.setCanOpenDoors(true);
        }
    }

	@Override
    public void aiStep() {
        super.aiStep();

        if(this.onGround && !this.getCommandSenderWorld().isClientSide) {
        	if(this.hasAttackTarget()) {
        		if(this.random.nextInt(10) == 0)
        			this.leap(6.0F, 0.1D, this.getTarget());
        	}
        }
    }

    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }

    @Override
    public float getFallResistance() {
    	return 50;
    }

    public boolean petControlsEnabled() { return true; }

    public ResourceLocation getTexture() {
        if(!this.hasCustomName() || !"Rudolph".equals(this.getCustomName().getString()))
            return super.getTexture();

        String textureName = this.getTextureName() + "_rudolph";
        if(TextureManager.getTexture(textureName) == null)
            TextureManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
        return TextureManager.getTexture(textureName);
    }
}
