package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.WanderGoal;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.monster.IMob;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityWendigo extends BaseCreatureEntity implements IMob {

	WanderGoal wanderAI;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityWendigo(EntityType<? extends EntityWendigo> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.spawnsOnLand = true;
        this.spawnsInWater = true;
        this.hasAttackSound = false;
        this.setupMob();
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(1.0D).setRange(16.0F).setMinChaseDistance(8.0F));
    }
    
    
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
        super.livingTick();

        // Trail:
        if(!this.getEntityWorld().isRemote && this.isMoving() && this.ticksExisted % 5 == 0) {
            int trailHeight = 1;
            int trailWidth = 1;
            if(this.isRareVariant())
                trailWidth = 3;
            for(int y = 0; y < trailHeight; y++) {
                Block block = this.getEntityWorld().getBlockState(this.getPosition().add(0, y, 0)).getBlock();
                if(block != null && (block == Blocks.AIR || block == Blocks.FIRE || block == Blocks.SNOW || block == Blocks.TALL_GRASS || block == ObjectManager.getBlock("scorchfire") || block == ObjectManager.getBlock("doomfire"))) {
                    if(trailWidth == 1)
                        this.getEntityWorld().setBlockState(this.getPosition().add(0, y, 0), ObjectManager.getBlock("frostfire").getDefaultState());
                    else
                        for(int x = -(trailWidth / 2); x < (trailWidth / 2) + 1; x++) {
                            for(int z = -(trailWidth / 2); z < (trailWidth / 2) + 1; z++) {
                                this.getEntityWorld().setBlockState(this.getPosition().add(x, y, z), ObjectManager.getBlock("frostfire").getDefaultState());
                            }
                        }
                }
            }
        }

        // Freeze Water:
        if(!this.getEntityWorld().isRemote && this.isMoving() && this.ticksExisted % 5 == 0) {
            Block block = this.getEntityWorld().getBlockState(this.getPosition().add(0, -1, 0)).getBlock();
            if(block == Blocks.WATER)
                this.getEntityWorld().setBlockState(this.getPosition().add(0, -1, 0), Blocks.ICE.getDefaultState());
        }
        
        // Particles:
        if(this.getEntityWorld().isRemote) {
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().addParticle(ParticleTypes.ITEM_SNOWBALL, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	            this.getEntityWorld().addParticle(ParticleTypes.ITEM_SNOWBALL, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
	        }
	        if(this.ticksExisted % 10 == 0)
		        for(int i = 0; i < 2; ++i) {
		            this.getEntityWorld().addParticle(ParticleTypes.ITEM_SNOWBALL, this.getPositionVec().getX() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, this.getPositionVec().getY() + this.rand.nextDouble() * (double)this.getSize(Pose.STANDING).height, this.getPositionVec().getZ() + (this.rand.nextDouble() - 0.5D) * (double)this.getSize(Pose.STANDING).width, 0.0D, 0.0D, 0.0D);
		        }
        }
    }


    // ==================================================
    //                      Movement
    // ==================================================
    // ========== Movement Speed Modifier ==========
    @Override
    public float getAISpeedModifier() {
        if(this.isInWater())
            return 2.0F;
        return 1.0F;
    }

    // Pathing Weight:
    @Override
    public float getBlockPathWeight(int x, int y, int z) {
        int waterWeight = 10;
        BlockPos pos = new BlockPos(x, y, z);
        if(ObjectManager.getBlock("ooze") != null) {
            if (this.getEntityWorld().getBlockState(pos).getBlock() == ObjectManager.getBlock("ooze"))
                return (super.getBlockPathWeight(x, y, z) + 1) * (waterWeight + 1);
        }

        if(this.getAttackTarget() != null)
            return super.getBlockPathWeight(x, y, z);
        if(this.isInWater())
            return -999999.0F;

        return super.getBlockPathWeight(x, y, z);
    }

    // Pushed By Water:
    @Override
    public boolean isPushedByWater() {
        return false;
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("tundra", target, range, 0, new Vec3d(0, 0, 0), 1.2f, 2f, 1F);
        super.attackRanged(target, range);
    }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
        if(type.equals("ooze")) return false;
        return super.isInvulnerableTo(type, source, damage);
    }


    // ==================================================
    //                     Abilities
    // ==================================================
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }
}
