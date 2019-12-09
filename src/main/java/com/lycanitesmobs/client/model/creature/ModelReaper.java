package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.template.ModelTemplateBiped;
import com.lycanitesmobs.client.renderer.RenderCreature;
import com.lycanitesmobs.client.renderer.layer.LayerBase;
import com.lycanitesmobs.client.renderer.layer.LayerEffect;
import com.lycanitesmobs.client.renderer.layer.LayerScrolling;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.client.model.ModelObjOld;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@SideOnly(Side.CLIENT)
public class ModelReaper extends ModelTemplateBiped {
	
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelReaper() {
        this(1.0F);
    }
    
    public ModelReaper(float shadowSize) {
    	// Load Model:
    	this.initModel("reaper", LycanitesMobs.modInfo, "entity/reaper");

    	// Scales:
		this.flightBobScale = 0.15F;

        // Tropy:
        this.trophyScale = 1.2F;
        this.trophyOffset = new float[] {0.0F, 0.0F, 0.0F};
    }


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	@Override
	public void addCustomLayers(RenderCreature renderer) {
		super.addCustomLayers(renderer);
		renderer.addLayer(new LayerScrolling(renderer, "cape", false, LayerEffect.BLEND.SUB.id, true, new Vector2f(0, -1)));
	}
    
    
    // ==================================================
   	//                 Animate Part
   	// ==================================================
    @Override
    public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
    	super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);

		// Cloak and Cape:
		if(partName.equals("cloak")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos(loop * 0.1F) * 0.05F), 0, 0);
		}
		else if(partName.equals("cape")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.05F), 0, 0);
		}

		// Claws:
		else if(partName.equals("clawleft01") || partName.equals("clawright01")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.2F), 0, 0);
		}
		else if(partName.equals("clawleft02") || partName.equals("clawright02")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 20) * 0.2F) * 0.2F), 0, 0);
		}
		else if(partName.equals("clawleft03") || partName.equals("clawright03")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 40) * 0.2F) * 0.2F), 0, 0);
		}
    	
    	// Skulls:
		else if(partName.equals("skull01")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos(loop * 0.2F) * 0.2F - 0.2F), 0, 0);
		}
		else if(partName.equals("skull02")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 20) * 0.2F) * 0.2F - 0.2F), 0, 0);
		}
		else if(partName.equals("skull03")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 40) * 0.2F) * 0.2F - 0.2F), 0, 0);
		}
		else if(partName.equals("skull04")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 60) * 0.2F) * 0.2F - 0.2F), 0, 0);
		}
		else if(partName.equals("skull05")) {
			this.rotate((float)Math.toDegrees(MathHelper.cos((loop + 80) * 0.2F) * 0.2F - 0.2F), 0, 0);
		}
    }


	// ==================================================
	//                Can Render Part
	// ==================================================
	@Override
	public boolean canRenderPart(String partName, Entity entity, LayerBase layer, boolean trophy) {
		if("cape".equals(partName)) {
			return layer != null && "cape".equals(layer.name);
		}
		return layer == null;
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part and layer. **/
	@Override
	public Vector4f getPartColor(String partName, Entity entity, LayerBase layer, boolean trophy, float loop) {
		return super.getPartColor(partName, entity, layer, trophy, loop);
	}
}
