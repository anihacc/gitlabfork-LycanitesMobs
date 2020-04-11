package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEffect;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCacodemon extends ModelTemplateBiped {

    public ModelCacodemon() {
        this(1.0F);
    }
    
    public ModelCacodemon(float shadowSize) {
    	this.initModel("cacodemon", LycanitesMobs.modInfo, "entity/cacodemon");
    	this.flightBobScale = 0.1F;
        this.trophyScale = 0.5F;
    }

	@Override
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerCreatureEffect(renderer, "glow", true, LayerCreatureEffect.BLEND.ADD.id, true));
	}

	float maxLeg = 0F;
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		// Looking:
		float rotX = 0;
		float rotY = 0;
		if(partName.toLowerCase().equals("body")) {
			rotX += (Math.toDegrees(lookX / (180F / (float)Math.PI)) * 0.75F);
			rotY += (Math.toDegrees(lookY / (180F / (float)Math.PI))) * 0.75F;
		}
		if(partName.equals("eye")) {
			rotX += (Math.toDegrees(lookX / (180F / (float)Math.PI)) * 0.25F);
			rotY += (Math.toDegrees(lookY / (180F / (float)Math.PI))) * 0.25F;
		}
		this.rotate(rotX, rotY, 0);
    }
}
