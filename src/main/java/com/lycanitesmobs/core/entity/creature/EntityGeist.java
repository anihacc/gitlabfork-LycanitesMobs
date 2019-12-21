package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import com.lycanitesmobs.core.entity.goals.actions.MoveVillageGoal;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EntityGeist extends AgeableCreatureEntity implements IMob {

    public boolean shadowfireDeath = true;

    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGeist(EntityType<? extends EntityGeist> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = true;
        this.spreadFire = true;

        this.canGrow = false;
        this.babySpawnChance = 0.01D;

        this.setupMob();
    }

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

    @Override
    public void loadCreatureFlags() {
        this.shadowfireDeath = this.creatureInfo.getFlag("shadowfireDeath", this.shadowfireDeath);
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
            zombievillagerentity.func_213790_g(villagerentity.getOffers().write());
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
    //                      Death
    // ==================================================
    @Override
    public void onDeath(DamageSource damageSource) {
        try {
            int shadowfireWidth = (int)Math.floor(this.getSize(this.getPose()).width) + 1;
            int shadowfireHeight = (int)Math.floor(this.getSize(this.getPose()).height) + 1;
            boolean permanent = false;
            if(damageSource.getTrueSource() == this) {
                permanent = true;
                shadowfireWidth *= 5;
            }

            if(!this.getEntityWorld().isRemote && (permanent || (this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.shadowfireDeath))) {
                for(int x = (int)this.posX - shadowfireWidth; x <= (int)this.posX + shadowfireWidth; x++) {
                    for(int y = (int)this.posY - shadowfireHeight; y <= (int)this.posY + shadowfireHeight; y++) {
                        for(int z = (int)this.posZ - shadowfireWidth; z <= (int)this.posZ + shadowfireWidth; z++) {
                            Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
                            if(block != Blocks.AIR) {
                                BlockPos placePos = new BlockPos(x, y + 1, z);
                                Block upperBlock = this.getEntityWorld().getBlockState(placePos).getBlock();
                                if(upperBlock == Blocks.AIR) {
                                    this.getEntityWorld().setBlockState(placePos, ObjectManager.getBlock("shadowfire").getDefaultState().with(BlockFireBase.PERMANENT, permanent));
                                }
                            }
                        }
                    }
                }
            }
        }
        catch(Exception e) {}
        super.onDeath(damageSource);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean daylightBurns() {
        return !this.isChild() && !this.isMinion();
    }
}
