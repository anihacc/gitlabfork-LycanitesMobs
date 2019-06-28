package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import com.lycanitesmobs.core.entity.goals.actions.MoveVillageGoal;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityJabberwock extends TameableCreatureEntity implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityJabberwock(EntityType<? extends EntityJabberwock> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;

        this.canGrow = false;
        this.babySpawnChance = 0.01D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(this.nextTravelGoalIndex++, new MoveVillageGoal(this));

        super.registerGoals();

        this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));

        if(this.getNavigator() instanceof GroundPathNavigator) {
            GroundPathNavigator pathNavigateGround = (GroundPathNavigator)this.getNavigator();
            pathNavigateGround.setBreakDoors(true);
        }
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Random Lunging:
        if(this.onGround && !this.getEntityWorld().isRemote) {
        	if(this.hasAttackTarget()) {
        		if(this.rand.nextInt(10) == 0)
        			this.leap(6.0F, 0.1D, this.getAttackTarget());
        	}
        }
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 50;
    }
	

    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                       Visuals
    // ==================================================
    /** Returns this creature's main texture. Also checks for for subspecies. **/
    public ResourceLocation getTexture() {
        if(!"Rudolph".equals(this.getCustomName()))
            return super.getTexture();

        String textureName = this.getTextureName() + "_rudolph";
        if(AssetManager.getTexture(textureName) == null)
            AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
        return AssetManager.getTexture(textureName);
    }
}
