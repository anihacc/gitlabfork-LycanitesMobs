package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityCryptkeeper extends EntityCreatureAgeable implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityCryptkeeper(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = false;
        this.spreadFire = true;
        this.canGrow = false;
        this.babySpawnChance = 0.1D;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        if(this.getNavigator() instanceof PathNavigateGround) {
            PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
            pathNavigateGround.setBreakDoors(true);
            pathNavigateGround.setAvoidSun(true);
        }
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(1, new EntityAIBreakDoor(this));
        this.field_70714_bg.addTask(3, new EntityAIAttackMelee(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.field_70714_bg.addTask(4, new EntityAIAttackMelee(this));
        this.field_70714_bg.addTask(6, new EntityAIMoveVillage(this));
        this.field_70714_bg.addTask(7, new EntityAIWander(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(2, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(2, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class).setCheckSight(false));
        this.field_70715_bh.addTask(2, new EntityAITargetAttack(this).setTargetClass(EntityHusk.class));
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== On Kill ==========
    @Override
    public void onKillEntity(LivingEntity entityLivingBase) {
        super.onKillEntity(entityLivingBase);

        if(this.getEntityWorld().getDifficulty().getDifficultyId() >= 2 && entityLivingBase instanceof VillagerEntity) {
            if (this.getEntityWorld().getDifficulty().getDifficultyId() == 2 && this.rand.nextBoolean()) return;

            VillagerEntity entityvillager = (VillagerEntity)entityLivingBase;
            EntityZombieVillager entityzombievillager = new EntityZombieVillager(this.getEntityWorld());
            entityzombievillager.copyLocationAndAnglesFrom(entityvillager);
            this.getEntityWorld().removeEntity(entityvillager);
            entityzombievillager.onInitialSpawn(this.getEntityWorld().getDifficultyForLocation(new BlockPos(entityzombievillager)), new EntityCreatureBase.GroupData(false));
            entityzombievillager.setProfession(entityvillager.getProfession());
            entityzombievillager.setChild(entityvillager.isChild());
            entityzombievillager.setNoAI(entityvillager.isAIDisabled());

            if (entityvillager.hasCustomName()) {
                entityzombievillager.setCustomNameTag(entityvillager.getCustomNameTag());
                entityzombievillager.setAlwaysRenderNameTag(entityvillager.getAlwaysRenderNameTag());
            }

            this.getEntityWorld().spawnEntity(entityzombievillager);
            this.getEntityWorld().playEvent(null, 1016, entityzombievillager.getPosition(), 0);
        }
    }
	
	
	// ==================================================
  	//                      Breeding
  	// ==================================================
    // ========== Create Child ==========
    @Override
	public EntityCreatureAgeable createChild(EntityCreatureAgeable baby) {
		return new EntityCryptkeeper(this.getEntityWorld());
	}
}
