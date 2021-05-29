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
		super(minionDamageSource.msgId, minionDamageSource.getEntity());
        this.minionDamageSource = minionDamageSource;
        this.minionOwner = owner;
	}


    // ==================================================
    //                     Get Entity
    // ==================================================
    // This Entity Caused The Damage:
    @Override
    public Entity getDirectEntity() {
        return this.entity;
    }

    // This Entity Gets Credit for The Kill:
    @Override
    public Entity getEntity() {
        return this.minionOwner;
    }

    // ==================================================
    //                    Chat Message
    // ==================================================
    @Override
    public ITextComponent getLocalizedDeathMessage(LivingEntity slainEntity) {
        return this.minionDamageSource.getLocalizedDeathMessage(slainEntity);
    }
}
