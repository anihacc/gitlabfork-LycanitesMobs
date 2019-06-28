package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import com.lycanitesmobs.core.entity.goals.actions.MoveVillageGoal;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityGhoul extends AgeableCreatureEntity implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGhoul(EntityType<? extends EntityGhoul> entityType, World world) {
        super(entityType, world);
        
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
    protected void registerGoals() {
        this.goalSelector.addGoal(this.nextTravelGoalIndex++, new MoveVillageGoal(this));

        super.registerGoals();

        this.goalSelector.addGoal(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));

        if(this.getNavigator() instanceof GroundPathNavigator) {
            GroundPathNavigator pathNavigateGround = (GroundPathNavigator)this.getNavigator();
            pathNavigateGround.setBreakDoors(true);
            pathNavigateGround.setAvoidSun(true);
        }
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== On Kill ==========
    @Override
    public void onKillEntity(LivingEntity entityLivingBase) {
        super.onKillEntity(entityLivingBase);

        if(this.getEntityWorld().getDifficulty().getId() >= 2 && entityLivingBase instanceof VillagerEntity) {
            if (this.getEntityWorld().getDifficulty().getId() == 2 && this.rand.nextBoolean()) return;

            VillagerEntity villagerentity = (VillagerEntity)entityLivingBase;
            ZombieVillagerEntity zombievillagerentity = EntityType.ZOMBIE_VILLAGER.create(this.world);
            zombievillagerentity.copyLocationAndAnglesFrom(villagerentity);
            villagerentity.remove();
            zombievillagerentity.onInitialSpawn(this.getEntityWorld(), this.getEntityWorld().getDifficultyForLocation(new BlockPos(zombievillagerentity)), SpawnReason.CONVERSION, null, null);
            zombievillagerentity.func_213792_a(villagerentity.getVillagerData());
            zombievillagerentity.func_213790_g(villagerentity.getOffers().func_222199_a());
            zombievillagerentity.func_213789_a(villagerentity.getXp());
            zombievillagerentity.setChild(villagerentity.isChild());
            zombievillagerentity.setNoAI(villagerentity.isAIDisabled());

            if (villagerentity.hasCustomName()) {
                zombievillagerentity.setCustomName(villagerentity.getCustomName());
                zombievillagerentity.setCustomNameVisible(villagerentity.isCustomNameVisible());
            }

            this.getEntityWorld().addEntity(zombievillagerentity);
            this.getEntityWorld().playEvent(null, 1016, zombievillagerentity.getPosition(), 0);
        }
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean daylightBurns() { return !this.isChild(); }
}
