package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.TextureManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class Wildkin extends TameableCreatureEntity implements Enemy {

    public Wildkin(EntityType<? extends Wildkin> entityType, Level world) {
        super(entityType, world);

        this.attribute = MobType.UNDEFINED;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;
        this.setupMob();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(false));

        if(this.getNavigation() instanceof GroundPathNavigation) {
            GroundPathNavigation pathNavigateGround = (GroundPathNavigation)this.getNavigation();
            pathNavigateGround.setCanOpenDoors(true);
        }
    }

    @Override
    public double getFallingMod() {
        return 0.8D;
    }
    
    

    @Override
    public float getFallResistance() {
    	return 100;
    }


    @Override
    public int getNoBagSize() { return 0; }
    @Override
    public int getBagSize() { return this.creatureInfo.bagSize; }
    

    public boolean petControlsEnabled() { return true; }

    @Override
    public ResourceLocation getTexture() {
        if(!this.hasCustomName() || !"Gooderness".equals(this.getCustomName().getString()))
            return super.getTexture();

        String textureName = this.getTextureName() + "_gooderness";
        if(TextureManager.getTexture(textureName) == null)
            TextureManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
        return TextureManager.getTexture(textureName);
    }
}
