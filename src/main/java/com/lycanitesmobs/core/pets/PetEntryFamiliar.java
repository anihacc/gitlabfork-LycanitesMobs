package com.lycanitesmobs.core.pets;


import net.minecraft.entity.EntityLivingBase;

import java.util.UUID;

public class PetEntryFamiliar extends PetEntry {

    // ==================================================
    //                     Constructor
    // ==================================================
	public PetEntryFamiliar(UUID petEntryID, EntityLivingBase host, String summonType) {
        super(petEntryID, "familiar", host, summonType);
	}
}
