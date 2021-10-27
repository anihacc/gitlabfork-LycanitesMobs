package com.lycanitesmobs.core.entity.damagesources;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;

public class MinionEntityDamageSource extends EntityDamageSource {
    protected DamageSource minionDamageSource;
    protected final Entity minionOwner;

	public MinionEntityDamageSource(DamageSource minionDamageSource, Entity owner) {
		super(minionDamageSource.damageType, minionDamageSource.getTrueSource());
        this.minionDamageSource = minionDamageSource;
        this.minionOwner = owner;
	}

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

    @Override
    public ITextComponent getDeathMessage(EntityLivingBase slainEntity) {
        return this.minionDamageSource.getDeathMessage(slainEntity);
    }
}
