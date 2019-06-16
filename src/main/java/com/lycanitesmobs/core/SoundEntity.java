package com.lycanitesmobs.core;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoundEntity extends MovingSound {
    private final Entity entity;

    public SoundEntity(SoundEvent soundIn, SoundCategory categoryIn, Entity entity, float volume) {
        super(soundIn, categoryIn);
        this.entity = entity;
        this.repeat = true;
        this.volume = volume;
        this.pitch = 1.0F;
        this.repeat = false;
    }

    public void update() {
        if (this.entity == null || this.entity.isDead) {
            this.donePlaying = true;
        }
        else {
            this.xPosF = (float)this.entity.posX;
            this.yPosF = (float)this.entity.posY;
            this.zPosF = (float)this.entity.posZ;
        }
    }
}