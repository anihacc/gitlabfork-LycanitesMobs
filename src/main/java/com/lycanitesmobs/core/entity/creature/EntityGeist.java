package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupShadow;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.*;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.entity.goals.targeting.RevengeGoal;
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

public class EntityGeist extends AgeableCreatureEntity implements IMob, IGroupShadow {

    public boolean geistShadowfireDeath = true; // TODO Creature flags.

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

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        if(this.getNavigator() instanceof GroundPathNavigator) {
            GroundPathNavigator pathNavigateGround = (GroundPathNavigator)this.getNavigator();
            pathNavigateGround.setBreakDoors(true);
            pathNavigateGround.setAvoidSun(true);
        }
        this.goalSelector.addGoal(0, new SwimmingGoal(this));
        this.goalSelector.addGoal(1, new BreakDoorGoal(this));
        this.goalSelector.addGoal(3, new AttackMeleeGoal(this).setTargetClass(PlayerEntity.class).setLongMemory(false));
        this.goalSelector.addGoal(4, new AttackMeleeGoal(this));
        this.goalSelector.addGoal(6, new MoveVillageGoal(this));
        this.goalSelector.addGoal(7, new WanderGoal(this));
        this.goalSelector.addGoal(10, new WatchClosestGoal(this).setTargetClass(PlayerEntity.class));
        this.goalSelector.addGoal(11, new LookIdleGoal(this));
        this.targetSelector.addGoal(0, new RevengeGoal(this).setHelpCall(true));
        this.targetSelector.addGoal(2, new FindAttackTargetGoal(this).setTargetClass(PlayerEntity.class));
        this.targetSelector.addGoal(2, new FindAttackTargetGoal(this).setTargetClass(VillagerEntity.class).setCheckSight(false));
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
    //                      Death
    // ==================================================
    @Override
    public void onDeath(DamageSource damageSource) {
        try {
            if(!this.getEntityWorld().isRemote && this.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING) && this.geistShadowfireDeath) {
                int shadowfireWidth = (int)Math.floor(this.getSize(Pose.STANDING).width) + 1;
                int shadowfireHeight = (int)Math.floor(this.getSize(Pose.STANDING).height) + 1;
                for(int x = (int)this.posX - shadowfireWidth; x <= (int)this.posX + shadowfireWidth; x++) {
                    for(int y = (int)this.posY - shadowfireHeight; y <= (int)this.posY + shadowfireHeight; y++) {
                        for(int z = (int)this.posZ - shadowfireWidth; z <= (int)this.posZ + shadowfireWidth; z++) {
                            Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
                            if(block != Blocks.AIR) {
                                BlockPos placePos = new BlockPos(x, y + 1, z);
                                Block upperBlock = this.getEntityWorld().getBlockState(placePos).getBlock();
                                if(upperBlock == Blocks.AIR) {
                                    this.getEntityWorld().setBlockState(placePos, ObjectManager.getBlock("shadowfire").getDefaultState(), 3);
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
    public boolean daylightBurns() { return !this.isChild(); }
}
