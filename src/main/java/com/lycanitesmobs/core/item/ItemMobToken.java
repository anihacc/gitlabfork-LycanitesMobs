package com.lycanitesmobs.core.item;

import com.lycanitesmobs.core.info.ModInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ItemMobToken extends ItemBase {

	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemMobToken(ModInfo group) {
        super();
		this.itemName = "mobtoken";
		this.modInfo = group;
        this.setUnlocalizedName(this.itemName);
        this.setup();
    }

    @Override
    public void setup() {
        this.setRegistryName(this.modInfo.filename, this.itemName);
        this.setUnlocalizedName(this.itemName);
    }


    // ==================================================
    //                     Visuals
    // ==================================================
    // ========== Holding Angle ==========
    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean isFull3D() {
        return true;
    }
}
