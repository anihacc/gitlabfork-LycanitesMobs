package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;

import com.lycanitesmobs.client.model.template.ModelTemplateInsect;
import com.lycanitesmobs.core.entity.creature.EntityDarkling;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDarkling extends ModelTemplateInsect {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelDarkling() {
        this(1.0F);
    }

    public ModelDarkling(float shadowSize) {
    	// Load Model:
        this.initModel("darkling", LycanitesMobs.modInfo, "entity/darkling");

        // Scaling:
    	this.lookHeadScaleX = 0;
		this.lookHeadScaleY = 0;
		this.mouthScaleX = 1F;
		this.mouthScaleY = 0.1F;

    	// Trophy:
        this.trophyScale = 1.0F;
    }


	// ==================================================
	//                 Animate Part
	// ==================================================
	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		// Latching:
		if(partName.equals("body") && entity instanceof EntityDarkling) {
			if(((EntityDarkling)entity).hasLatchTarget()) {
				this.rotate(-90F, 0, 0);
			}
		}
	}
}
