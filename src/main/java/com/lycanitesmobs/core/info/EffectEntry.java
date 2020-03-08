package com.lycanitesmobs.core.info;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EffectEntry {
    public String effectId;
    public int duration = 20;
    public int amplifier = 0;
    public float chance = 1;

    public EffectEntry(String effectId, int duration, int amplifier, float chance) {
        this.effectId = effectId;
        this.duration = duration;
        this.amplifier = amplifier;
        this.chance = chance;
    }

    public PotionEffect createEffectInstance(World world) {
        if(this.chance < 1 && world.rand.nextFloat() > this.chance) {
            return null;
        }
        Potion potion = GameRegistry.findRegistry(Potion.class).getValue(new ResourceLocation(this.effectId));
        if(potion != null) {
            return new PotionEffect(potion, this.duration, this.amplifier);
        }
        LycanitesMobs.logWarning("", "Unable to create a food effect from the id: " + this.effectId);
        return null;
    }
}
