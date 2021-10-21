package com.lycanitesmobs.core;

import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MobEventSound extends SimpleSoundInstance implements TickableSoundInstance {
    protected final Entity entity;
    protected boolean donePlaying;

    public MobEventSound(SoundEvent soundEvent, SoundSource categoryIn, Entity entity, float volume, float pitch) {
        super(soundEvent, categoryIn, volume, pitch, (float)entity.position().x(), (float)entity.position().y(), (float)entity.position().z());
        this.entity = entity;
        this.looping = true;
        this.volume = volume;
        this.pitch = 1.0F;
        this.looping = false;
    }

    @Override
    public void tick() {
        if (this.entity == null || !this.entity.isAlive()) {
            this.donePlaying = true;
        }
        else {
            this.x = (float)this.entity.position().x();
            this.y = (float)this.entity.position().y();
            this.z = (float)this.entity.position().z();
        }
    }

    @Override
    public boolean isStopped() {
        return this.donePlaying;
    }
}