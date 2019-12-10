package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.projectile.EntityHellShield;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAstaroth extends BaseCreatureEntity implements IMob {
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAstaroth(EntityType<? extends EntityAstaroth> entityType, World world) {
        super(entityType, world);
        
        // Setup:
        this.attribute = CreatureAttribute.UNDEAD;
        this.hasAttackSound = false;
        this.solidCollision = false;
        this.setupMob();
        this.hitAreaWidthScale = 1.5F;

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(this.nextCombatGoalIndex++, new AttackRangedGoal(this).setSpeed(1.0D).setRange(40.0F).setMinChaseDistance(16.0F).setChaseTime(-1));
    }


    // ==================================================
    //                      Update
    // ==================================================
    // ========== Living Update ==========
    @Override
    public void livingTick() {
        super.livingTick();

        // Asmodeus Master:
        if(this.updateTick % 20 == 0) {
            if (this.getMasterTarget() != null && this.getMasterTarget() instanceof EntityAsmodeus && ((BaseCreatureEntity)this.getMasterTarget()).getBattlePhase() > 0) {
                EntityHellShield projectile = new EntityHellShield(ProjectileManager.getInstance().oldProjectileTypes.get(EntityHellShield.class), this.getEntityWorld(), this);
                projectile.setProjectileScale(3f);
                projectile.posY -= this.getSize(Pose.STANDING).height * 0.35D;
                double dX = this.getMasterTarget().posX - this.posX;
                double dY = this.getMasterTarget().posY + (this.getMasterTarget().getSize(Pose.STANDING).height * 0.75D) - projectile.posY;
                double dZ = this.getMasterTarget().posZ - this.posZ;
                double distance = MathHelper.sqrt(dX * dX + dZ * dZ) * 0.1F;
                float velocity = 0.8F;
                projectile.shoot(dX, dY + distance, dZ, velocity, 0.0F);
                this.getEntityWorld().addEntity(projectile);
            }
        }
    }
    
    
	// ==================================================
    //                      Attacks
    // ==================================================
    @Override
    public boolean canAttack(LivingEntity target) {
        if(target instanceof EntityTrite || target instanceof EntityCacodemon ||  target instanceof EntityAsmodeus)
            return false;
        return super.canAttack(target);
    }
    
    // ========== Ranged Attack ==========
    @Override
    public void attackRanged(Entity target, float range) {
        this.fireProjectile("devilstar", target, range, 0, new Vec3d(0, -2.8F, 0), 1.2f, 1f, 1F);
        super.attackRanged(target, range);
    }
	
	
	// ==================================================
   	//                      Death
   	// ==================================================
	@Override
	public void onDeath(DamageSource par1DamageSource) {
        if(!this.getEntityWorld().isRemote && CreatureManager.getInstance().getCreature("trite").enabled) {
            int j = 2 + this.rand.nextInt(5) + getEntityWorld().getDifficulty().getId() - 1;
            for(int k = 0; k < j; ++k) {
                float f = ((float)(k % 2) - 0.5F) * this.getSize(Pose.STANDING).width / 4.0F;
                float f1 = ((float)(k / 2) - 0.5F) * this.getSize(Pose.STANDING).width / 4.0F;
                EntityTrite trite =(EntityTrite)CreatureManager.getInstance().getCreature("trite").createEntity(this.getEntityWorld());
                trite.setLocationAndAngles(this.posX + (double)f, this.posY + 0.5D, this.posZ + (double)f1, this.rand.nextFloat() * 360.0F, 0.0F);
                trite.setMinion(true);
                trite.applySubspecies(this.getSubspeciesIndex());
                this.getEntityWorld().addEntity(trite);
                if(this.getAttackTarget() != null)
                	trite.setRevengeTarget(this.getAttackTarget());
            }
        }
        super.onDeath(par1DamageSource);
    }
}
