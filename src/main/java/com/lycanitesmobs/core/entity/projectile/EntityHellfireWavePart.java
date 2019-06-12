package com.lycanitesmobs.core.entity.projectile;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class EntityHellfireWavePart extends EntityHellfireWall {

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityHellfireWavePart(World par1World) {
        super(par1World);
    }

    public EntityHellfireWavePart(World par1World, LivingEntity par2LivingEntity) {
        super(par1World, par2LivingEntity);
    }

    public EntityHellfireWavePart(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    // ========== Setup Projectile ==========
    @Override
    public void setup() {
        this.entityName = "hellfirewavepart";
        super.setup();
        this.animationFrameMax = 59;
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    public String getTextureName() {
        return "hellfirewave";
    }
}
