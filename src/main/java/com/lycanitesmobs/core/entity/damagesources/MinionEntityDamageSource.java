package com.lycanitesmobs.core.entity.damagesources;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.network.chat.Component;

public class MinionEntityDamageSource extends EntityDamageSource {
    protected DamageSource minionDamageSource;
    protected final Entity minionOwner;

	public MinionEntityDamageSource(DamageSource minionDamageSource, Entity owner) {
		super(minionDamageSource.msgId, minionDamageSource.getEntity());
        this.minionDamageSource = minionDamageSource;
        this.minionOwner = owner;
	}

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

    @Override
    public Component getLocalizedDeathMessage(LivingEntity slainEntity) {
        return this.minionDamageSource.getLocalizedDeathMessage(slainEntity);
    }
}
