package com.lycanitesmobs.core.entity.damagesources;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;

public class MinionEntityDamageSource extends EntityDamageSource {
    EntityDamageSource minionDamageSource;
    private Entity minionOwner;
	
    // ==================================================
  	//                     Constructor
  	// ==================================================
	public MinionEntityDamageSource(EntityDamageSource minionDamageSource, Entity owner) {
		super(minionDamageSource.damageType, minionDamageSource.getTrueSource());
        this.minionDamageSource = minionDamageSource;
        this.minionOwner = owner;
	}


    // ==================================================
    //                     Get Entity
    // ==================================================
    // This Entity Caused The Damage:
    @Override
    public Entity getImmediateSource() {
        return this.damageSourceEntity;
    }

    // This Entity Gets Credit for The Kill:
    @Override
    public Entity getTrueSource() {
        return this.minionOwner;
    }

    // ==================================================
    //                    Chat Message
    // ==================================================
    @Override
    public ITextComponent getDeathMessage(LivingEntity slainEntity) {
        return this.minionDamageSource.getDeathMessage(slainEntity);
    }
}
