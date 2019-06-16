package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.ai.*;
import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupHunter;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.api.IGroupPrey;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityGorgomite extends EntityCreatureBase implements IMob, IGroupPrey {
	private int gorgomiteSwarmLimit = 10;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGorgomite(World world) {
        super(world);
        
        // Setup:
        this.attribute = CreatureAttribute.ARTHROPOD;
        this.hasAttackSound = true;
        this.setupMob();
        
        this.gorgomiteSwarmLimit = ConfigBase.getConfig(this.creatureInfo.modInfo, "general").getInt("Features", "Gorgomite Swarm Limit", this.gorgomiteSwarmLimit, "Limits how many Gorgomites there can be when swarming.");
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.field_70714_bg.addTask(0, new EntityAISwimming(this));
        this.field_70714_bg.addTask(2, new EntityAIAvoid(this).setNearSpeed(2.0D).setFarSpeed(1.5D).setNearDistance(5.0D).setFarDistance(10.0D));
        this.field_70714_bg.addTask(3, new EntityAIAttackMelee(this).setLongMemory(true));
        this.field_70714_bg.addTask(6, new EntityAIWander(this));
        this.field_70714_bg.addTask(10, new EntityAIWatchClosest(this).setTargetClass(PlayerEntity.class));
        this.field_70714_bg.addTask(11, new EntityAILookIdle(this));

        this.field_70715_bh.addTask(0, new EntityAITargetRevenge(this).setHelpCall(true));
        this.field_70715_bh.addTask(1, new EntityAITargetAttack(this).setTargetClass(PlayerEntity.class));
        this.field_70715_bh.addTask(2, new EntityAITargetAttack(this).setTargetClass(VillagerEntity.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupHunter.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupPredator.class));
        this.field_70715_bh.addTask(3, new EntityAITargetAvoid(this).setTargetClass(IGroupAlpha.class));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void livingTick() {
		if(!this.getEntityWorld().isRemote && this.hasAttackTarget() && this.updateTick % 20 == 0) {
			this.allyUpdate();
		}
        
        super.livingTick();
    }
    
    // ========== Spawn Minions ==========
	public void allyUpdate() {
		if(this.getEntityWorld().isRemote)
			return;
		
		// Spawn Minions:
		if(this.gorgomiteSwarmLimit > 0 && this.nearbyCreatureCount(this.getClass(), 64D) < this.gorgomiteSwarmLimit) {
			float random = this.rand.nextFloat();
			if(random <= 0.25F)
				this.spawnAlly(this.posX - 2 + (random * 4), this.posY, this.posZ - 2 + (random * 4));
		}
	}
	
    public void spawnAlly(double x, double y, double z) {
    	LivingEntity minion = new EntityGorgomite(this.getEntityWorld());
    	minion.setLocationAndAngles(x, y, z, this.rand.nextFloat() * 360.0F, 0.0F);
    	if(minion instanceof EntityCreatureBase) {
    		((EntityCreatureBase)minion).setMinion(true);
    		((EntityCreatureBase)minion).applySubspecies(this.getSubspeciesIndex());
    	}
    	this.getEntityWorld().spawnEntity(minion);
        if(this.getAttackTarget() != null)
        	minion.setRevengeTarget(this.getAttackTarget());
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
	// ========== Attack Class ==========
    @Override
    public boolean canAttackClass(Class targetClass) {
    	if(targetClass.isAssignableFrom(IGroupAlpha.class))
        	return false;
        if(targetClass.isAssignableFrom(IGroupPredator.class))
            return false;
    	return super.canAttackClass(targetClass);
    }
    
    
    // ==================================================
    //                       Death
    // ==================================================
    @Override
    public void onDeath(DamageSource par1DamageSource) {
    	allyUpdate();
        super.onDeath(par1DamageSource);
    }


	// ==================================================
	//                     Abilities
	// ==================================================
	// ========== Movement ==========
	@Override
	public boolean canClimb() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isInvulnerableTo(String type, DamageSource source, float damage) {
    	if(type.equals("cactus")) return false;
    	return super.isInvulnerableTo(type, source, damage);
    }
}
