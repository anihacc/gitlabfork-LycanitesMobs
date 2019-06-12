package com.lycanitesmobs.core.pets;


import net.minecraft.entity.LivingEntity;

public class PetEntryFamiliar extends PetEntry {

    // ==================================================
    //                     Constructor
    // ==================================================
	public PetEntryFamiliar(String name, LivingEntity host, String summonType) {
        super(name, "familiar", host, summonType);
	}
}
