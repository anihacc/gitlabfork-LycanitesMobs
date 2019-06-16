package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityKobold extends EntityCreatureTameable implements IMob, IGroupPrey {
    public boolean torchGreifing = true;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityKobold(World world) {
        super(world);
        this.torchGreifing = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getBool("Features", "Kobold Torch Griefing", this.torchGreifing, "Set to false to stop Kobolds from stealing torches.");
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spreadFire = false;

        this.canGrow = false;
        this.babySpawnChance = 0.1D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(1, new EntityAIAttackMelee(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.field_70714_bg.addTask(2, new EntityAIAttackMelee(this));
        this.field_70714_bg.addTask(3, new EntityAIGetItem(this).setDistanceMax(32).setSpeed(1.2D));
        this.field_70714_bg.addTask(4, this.aiSit);
        this.field_70714_bg.addTask(5, new EntityAIFollowOwner(this).setStrayDistance(16).setLostDistance(32));
        this.field_70714_bg.addTask(6, new EntityAIAvoid(this).setNearSpeed(1.8D).setFarSpeed(1.4D).setNearDistance(3.0D).setFarDistance(16.0D));
        if(this.torchGreifing)
            this.field_70714_bg.addTask(7, new EntityAIGetBlock(this).setDistanceMax(8).setSpeed(1.2D).setBlockName("torch").setTamedLooting(false));
        this.field_70714_bg.addTask(8, new EntityAIWander(this).setPauseRate(30));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetOwnerRevenge(this));
        this.field_70715_bh.addTask(1, new EntityAITargetOwnerAttack(this));
        this.field_70715_bh.addTask(2, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAvoid(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAvoid(this).setTargetClass(IGroupHunter.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
        this.field_70715_bh.addTask(4, new EntityAITargetAvoid(this).setTargetClass(IGroupAlpha.class));
        this.field_70715_bh.addTask(5, new EntityAITargetAvoid(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(6, new EntityAITargetOwnerThreats(this));
    }
	
	
	// ==================================================
  	//                     Spawning
  	// ==================================================
    // ========== Despawning ==========
    @Override
    protected boolean canDespawnNaturally() {
    	if(this.inventory.hasBagItems()) return false;
        return super.canDespawnNaturally();
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
    private int torchLootingTime = 20;
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();
        
        // Torch Looting:
        if(!this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing") && this.torchGreifing) {
	        if(this.torchLootingTime-- <= 0) {
	        	this.torchLootingTime = 60;
	        	int distance = 2;
	        	String targetName = "torch";
	        	List possibleTargets = new ArrayList<BlockPos>();
	            for(int x = (int)this.posX - distance; x < (int)this.posX + distance; x++) {
	            	for(int y = (int)this.posY - distance; y < (int)this.posY + distance; y++) {
	            		for(int z = (int)this.posZ - distance; z < (int)this.posZ + distance; z++) {
                            BlockPos pos = new BlockPos(x, y, z);
	            			Block searchBlock = this.getEntityWorld().getBlockState(pos).getBlock();
	                    	if(searchBlock != null && searchBlock != Blocks.AIR) {
	                    		BlockPos possibleTarget = null;
	                			if(ObjectLists.isName(searchBlock, targetName)) {
	                				this.getEntityWorld().destroyBlock(pos, true);
	                				break;
	                			}
	                    	}
	                    }
	                }
	            }
	        }
        }
    }
    
	
    // ==================================================
    //                     Attacks
    // ==================================================
    @Override
	public boolean canAttackEntity(LivingEntity targetEntity) {
    	if((targetEntity.getHealth() / targetEntity.getMaxHealth()) > 0.5F)
			return false;
		return super.canAttackEntity(targetEntity);
	}
    
    
    // ==================================================
    //                     Equipment
    // ==================================================
    public int getNoBagSize() { return 10; }
    public int getBagSize() { return 10; }
    
    @Override
    public boolean canPickupItems() {
    	return ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getBool("Features", "Kobold Thievery", true, "Set to false to prevent Kobold from collecting items.");
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityKobold(this.getEntityWorld());
	}
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
}
