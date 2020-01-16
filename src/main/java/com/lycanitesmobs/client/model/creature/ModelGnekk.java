package com.lycanitesmobs.client.model.creature;


import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerEffect;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelGnekk extends ModelTemplateBiped {
    public ModelGnekk() {
        this(1.0F);
    }

    public ModelGnekk(float shadowSize) {
    	// Load Model:
    	this.initModel("gnekk", LycanitesMobs.modInfo, "entity/gnekk");

        // Trophy:
        this.trophyScale = 1.8F;
    }

	@Override
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerEffect(renderer, "glow", true, LayerEffect.BLEND.ADD.id, true));
	}


	@Override
	public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
		super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		// Perching:
		if(entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).hasPerchTarget()) {
			if("armleft".equalsIgnoreCase(partName) || "armright".equalsIgnoreCase(partName))
				this.rotate(45, 0, 0);
		}
	}
}