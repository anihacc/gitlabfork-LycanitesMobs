package com.lycanitesmobs.core;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MobEventSound extends SimpleSound implements ITickableSound {
    protected final Entity entity;
    protected boolean donePlaying;

    public MobEventSound(SoundEvent soundEvent, SoundCategory categoryIn, Entity entity, float volume, float pitch) {
        super(soundEvent, categoryIn, volume, pitch, (float)entity.getPositionVec().getX(), (float)entity.getPositionVec().getY(), (float)entity.getPositionVec().getZ());
        this.entity = entity;
        this.repeat = true;
        this.volume = volume;
        this.pitch = 1.0F;
        this.repeat = false;
    }

    @Override
    public void tick() {
        if (this.entity == null || !this.entity.isAlive()) {
            this.donePlaying = true;
        }
        else {
            this.x = (float)this.entity.getPositionVec().getX();
            this.y = (float)this.entity.getPositionVec().getY();
            this.z = (float)this.entity.getPositionVec().getZ();
        }
    }

    @Override
    public boolean isDonePlaying() {
        return this.donePlaying;
    }
}