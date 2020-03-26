package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import com.lycanitesmobs.core.info.ObjectLists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityGrue extends TameableCreatureEntity implements IMob {
    
	private int teleportTime = 60;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityGrue(World world) {
        super(world);
        
        // Setup:
        this.attribute = EnumCreatureAttribute.UNDEFINED;
        this.hasAttackSound = true;
        this.spawnsInWater = true;
        this.setupMob();

        this.stepHeight = 1.0F;
    }

    // ========== Init AI ==========
    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(this.nextPriorityGoalIndex++, new StealthGoal(this).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
        this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this).setLongMemory(true));
    }
	
	
    // ==================================================
    //                      Updates
    // ==================================================
	// ========== Living Update ==========
	@Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        
        // Random Target Teleporting:
        if(!this.getEntityWorld().isRemote && this.hasAttackTarget()) {
	        if(this.teleportTime-- <= 0) {
	        	this.teleportTime = 60 + this.getRNG().nextInt(40);
        		BlockPos teleportPosition = this.getFacingPosition(this.getAttackTarget(), -this.getAttackTarget().width - 1D, 0);
        		if(this.canTeleportTo(teleportPosition)) {
					this.playJumpSound();
					this.setPosition(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
				}
	        }
        }
        
        // Particles:
        if(this.getEntityWorld().isRemote)
	        for(int i = 0; i < 2; ++i) {
	            this.getEntityWorld().spawnParticle(EnumParticleTypes.SPELL_WITCH, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D);
	        }
    }

	/**
	 * Checks if this entity can teleport to the provided block position.
	 * @param pos The position to teleport to.
	 * @return True if it's safe to teleport.
	 */
	public boolean canTeleportTo(BlockPos pos) {
		for (int y = 0; y <= 1; y++) {
			IBlockState blockState = this.getEntityWorld().getBlockState(pos.add(0, y, 0));
			if (blockState.isNormalCube())
				return false;
		}
        return true;
    }
    
    
    // ==================================================
   	//                     Stealth
   	// ==================================================
    @Override
    public boolean canStealth() {
    	if(this.getEntityWorld().isRemote) return false;
		if(this.isMoving()) return false;
    	return this.testLightLevel() <= 0;
    }
    
    @Override
    public void startStealth() {
    	if(this.getEntityWorld().isRemote) {
			EnumParticleTypes particle = EnumParticleTypes.SPELL_WITCH;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            for(int i = 0; i < 100; i++)
            	this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
        }
    	super.startStealth();
    }
    
    
    // ==================================================
    //                      Attacks
    // ==================================================
    // ========== Melee Attack ==========
    @Override
    public boolean attackMelee(Entity target, double damageScale) {
    	if(!super.attackMelee(target, damageScale))
    		return false;
    	
    	// Leech:
    	if(this.isRareVariant() && target instanceof EntityLivingBase) {
    		EntityLivingBase targetLiving = (EntityLivingBase)target;
    		List<Potion> goodEffects = new ArrayList<>();
    		for(PotionEffect effectInstance : targetLiving.getActivePotionEffects()) {
				if(ObjectLists.inEffectList("buffs", effectInstance.getPotion()))
					goodEffects.add(effectInstance.getPotion());
    		}
    		if(goodEffects.size() > 0) {
    			if(goodEffects.size() > 1)
    				targetLiving.removePotionEffect(goodEffects.get(this.getRNG().nextInt(goodEffects.size())));
    			else
    				targetLiving.removePotionEffect(goodEffects.get(0));
				float leeching = Math.max(1, this.getAttackDamage(damageScale) / 2);
		    	this.heal(leeching);
    		}
    	}
        
        return true;
    }
    
    
    // ==================================================
  	//                     Abilities
  	// ==================================================
    @Override
    public boolean isFlying() { return true; }

    @Override
    public boolean isStrongSwimmer() { return true; }
    
    
    // ==================================================
    //                     Pet Control
    // ==================================================
    public boolean petControlsEnabled() { return true; }
    
    
    // ==================================================
   	//                     Immunities
   	// ==================================================
    @Override
    public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
        if(type.equals("inWall")) return false;
        return super.isDamageTypeApplicable(type, source, damage);
    }
    
    /** Returns true if this mob should be damaged by the sun. **/
    public boolean daylightBurns() {
    	return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }


    // ==================================================
    //                       Visuals
    // ==================================================
    @Override
    public ResourceLocation getTexture(String suffix) {
        if(!"Shadow Clown".equals(this.getCustomNameTag()))
            return super.getTexture(suffix);

        String textureName = this.getTextureName() + "_shadowclown";
		if(!"".equals(suffix)) {
			textureName += "_" + suffix;
		}
        if(AssetManager.getTexture(textureName) == null)
            AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
        return AssetManager.getTexture(textureName);
    }
}
