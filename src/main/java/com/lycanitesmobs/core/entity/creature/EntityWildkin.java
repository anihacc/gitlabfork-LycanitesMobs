package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.*;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityWildkin extends EntityCreatureTameable implements IMob {

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityWildkin(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;

        this.canGrow = true;
        this.babySpawnChance = 0.01D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        if(this.getNavigator() instanceof GroundPathNavigator) {
            GroundPathNavigator pathNavigateGround = (GroundPathNavigator)this.getNavigator();
            pathNavigateGround.setBreakDoors(true);
        }
        this.field_70714_bg.addTask(0, new SwimmingGoal(this));
        this.field_70714_bg.addTask(1, this.aiSit);
        this.field_70714_bg.addTask(2, new TemptGoal(this).setTemptDistanceMin(4.0D));
        this.field_70714_bg.addTask(3, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(4, new BreakDoorGoal(this));
        this.field_70714_bg.addTask(5, new AttackMeleeGoal(this));
        this.field_70714_bg.addTask(6, this.aiSit);
        this.field_70714_bg.addTask(7, new FollowOwnerGoal(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(8, new WanderGoal(this));
        this.field_70714_bg.addTask(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new LookIdleGoal(this));

        this.field_70715_bh.addTask(0, new OwnerRevengeTargetingGoal(this));
        this.field_70715_bh.addTask(1, new OwnerAttackTargetingGoal(this));
        this.field_70715_bh.addTask(2, new RevengeTargetingGoal(this).setHelpCall(true));
        this.field_70715_bh.addTask(3, new AttackTargetingGoal(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new AttackTargetingGoal(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(6, new OwnerDefenseTargetingGoal(this));
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Falling Speed Modifier ==========
    @Override
    public double getFallingMod() {
        return 0.8D;
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public float getFallResistance() {
    	return 100;
    }


    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 0; }
    public int getBagSize() { return 10; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }


    // ==================================================
    //                       Healing
    // ==================================================
    // ========== Healing Item ==========
    @Override
    public boolean isHealingItem(ItemStack testStack) {
        return ObjectLists.inItemList("cookedmeat", testStack);
    }


    // ==================================================
    //                       Visuals
    // ==================================================
    /** Returns this creature's main texture. Also checks for for subspecies. **/
    public ResourceLocation getTexture() {
        if(!"Gooderness".equals(this.getCustomName()))
            return super.getTexture();

        String textureName = this.getTextureName() + "_gooderness";
        if(AssetManager.getTexture(textureName) == null)
            AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
        return AssetManager.getTexture(textureName);
    }
}
