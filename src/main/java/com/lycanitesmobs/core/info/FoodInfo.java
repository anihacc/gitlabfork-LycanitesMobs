package com.lycanitesmobs.core.info;

import net.minecraft.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

public class FoodInfo {
	public int hunger;
	public float saturation;
	public boolean alwaysEdible;
	public boolean fastToEat;
	public boolean meat;
	public Map<PotionEffect, Float> effects = new HashMap<>();

	public void hunger(int hunger) {
		this.hunger = hunger;
	}

	public void saturation(float saturation) {
		this.saturation = saturation;
	}

	public void setAlwaysEdible() {
		this.alwaysEdible = true;
	}

	public void fastToEat() {
		this.fastToEat = true;
	}

	public void meat() {
		this.meat = true;
	}

	public void effect(PotionEffect effectInstance, float chance) {
		this.effects.put(effectInstance, chance);
	}
}
