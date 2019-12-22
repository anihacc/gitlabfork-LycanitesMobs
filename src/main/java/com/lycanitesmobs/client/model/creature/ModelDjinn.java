package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateElemental;
import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.specific.LayerDjinn;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelDjinn extends ModelTemplateElemental {

	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelDjinn() {
        this(1.0F);
    }

    public ModelDjinn(float shadowSize) {

		// Load Model:
		this.initModel("djinn", LycanitesMobs.modInfo, "entity/djinn");

		// Trophy:
		this.trophyScale = 1.2F;
		this.trophyOffset = new float[] {0.0F, 0.0F, -0.4F};
		this.trophyMouthOffset = new float[] {0.0F, -0.25F, 0.0F};
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(CreatureRenderer renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerDjinn(renderer));
	}


	// ==================================================
	//                Can Render Part
	// ==================================================
	/** Returns true if the part can be rendered on the base layer. **/
	@Override
	public boolean canBaseRenderPart(String partName, Entity entity, boolean trophy) {
		if(partName.contains("ribbon")) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
		if(partName.contains("ribbon")) {
			return layer instanceof LayerDjinn;
		}
		return super.canRenderPart(partName, entity, layer, trophy);
	}


	// ==================================================
	//              Get Part Texture Offset
	// ==================================================
	@Override
	public Vec2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
		if(partName.contains("ribbon")) {
			return new Vec2f(-loop * 25, 0);
		}
		return super.getBaseTextureOffset(partName, entity, trophy, loop);
	}
}
