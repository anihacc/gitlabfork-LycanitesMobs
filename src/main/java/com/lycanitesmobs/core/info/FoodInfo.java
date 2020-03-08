package com.lycanitesmobs.core.info;

import java.util.ArrayList;
import java.util.List;

public class FoodInfo {
	public int hunger;
	public float saturation;
	public boolean alwaysEdible;
	public boolean fastToEat;
	public boolean meat;
	public List<EffectEntry> effects = new ArrayList<>();

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

	public void effect(EffectEntry effectEntry) {
		this.effects.add(effectEntry);
	}
}
