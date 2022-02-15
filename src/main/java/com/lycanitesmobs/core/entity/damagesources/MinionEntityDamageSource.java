package com.lycanitesmobs.core.entity.damagesources;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.ITextComponent;

public class MinionEntityDamageSource extends EntityDamageSource {
    protected DamageSource minionDamageSource;
    protected final Entity minionOwner;
    protected final boolean playerCredit;

	public MinionEntityDamageSource(DamageSource minionDamageSource, Entity owner, boolean playerCredit) {
		super(minionDamageSource.damageType, minionDamageSource.getTrueSource());
        this.minionDamageSource = minionDamageSource;
        this.minionOwner = owner;
        this.playerCredit = playerCredit;
	}

    // This Entity That Caused The Damage:
    @Override
    public Entity getImmediateSource() {
        return this.minionDamageSource.getImmediateSource();
    }

    // This Entity Gets Credit for The Kill and Enmity:
    @Override
    public Entity getTrueSource() {
        if (this.playerCredit) {
            return this.minionOwner;
        }
        return this.minionDamageSource.getTrueSource();
    }

    // Gets the minion entity causing the damage:
    public Entity getMinion() {
        return this.minionDamageSource.getTrueSource();
    }

    @Override
    public ITextComponent getDeathMessage(EntityLivingBase slainEntity) {
        return this.minionDamageSource.getDeathMessage(slainEntity);
    }
}
