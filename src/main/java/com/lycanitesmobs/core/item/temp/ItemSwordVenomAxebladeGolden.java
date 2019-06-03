package com.lycanitesmobs.core.item.temp;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;

public class ItemSwordVenomAxebladeGolden extends ItemSwordVenomAxeblade {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSwordVenomAxebladeGolden(String itemName, String textureName) {
        super(itemName, textureName);
    }


    // ==================================================
    //                  Entity Spawning
    // ==================================================
    public void onSpawnEntity(Entity entity) {
        if(entity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
            entityCreature.applySubspecies(1);
            entityCreature.setTemporary(40 * 20);
        }
    }


    // ==================================================
    //                     Tool/Weapon
    // ==================================================
    // ========== Get Sword Damage ==========
    @Override
    public float getAttackDamage() {
        return 4F;
    }
}
