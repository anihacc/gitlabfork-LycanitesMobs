package com.lycanitesmobs.core.pets;


import net.minecraft.entity.LivingEntity;

import java.util.UUID;

public class PetEntryFamiliar extends PetEntry {

    // ==================================================
    //                     Constructor
    // ==================================================
	public PetEntryFamiliar(UUID petEntryID, String name, LivingEntity host, String summonType) {
        super(petEntryID, name, "familiar", host, summonType);
	}
}
