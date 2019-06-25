package com.lycanitesmobs.core.model;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.renderer.RenderCreature;
import com.lycanitesmobs.core.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.core.renderer.layer.LayerEquipment;
import com.lycanitesmobs.core.renderer.layer.LayerCreatureSaddle;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;

@OnlyIn(Dist.CLIENT)
public class ModelCreatureBase extends EntityModel<EntityCreatureBase> {
    
	// ==================================================
  	//                    Constructors
  	// ==================================================
    public ModelCreatureBase() {
        this(1.0F);
    }
    
    public ModelCreatureBase(float shadowSize) {
    	// Texture:
    	textureWidth = 128;
        textureHeight = 128;
    }


    // ==================================================
    //             Add Custom Render Layers
    // ==================================================
    public void addCustomLayers(RenderCreature renderer) {
        renderer.addLayer(new LayerEquipment(renderer, "chest"));
        renderer.addLayer(new LayerCreatureSaddle(renderer));
    }
    
    
    // ==================================================
   	//                  Render Model
   	// ==================================================
    @Override
    public void func_78088_a(EntityCreatureBase entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
        this.render(entity, time, distance, loop, lookY, lookX, scale, null, true);
    }

	/**
	 * Renders this model. Can be rendered as a trophy (just head, mouth, etc) too, use scale for this.
	 * @param entity Can't be null but can be any entity. If the mob's exact entity or an EntityCreatureBase is used more animations will be used.
	 * @param time How long the model has been displayed for? This is currently unused.
	 * @param distance Used for movement animations, this should just count up form 0 every tick and stop back at 0 when not moving.
	 * @param loop A continuous loop counting every tick, used for constant idle animations, etc.
	 * @param lookY A y looking rotation used by the head, etc.
	 * @param lookX An x looking rotation used by the head, etc.
	 * @param layer The layer that is being rendered, if null the default base layer is being rendered.
	 * @param scale Use to scale this mob. The default scale is 0.0625 (not sure why)! For a trophy/head-only model, set the scale to a negative amount, -1 will return a head similar in size to that of a Zombie head.
	 * @param animate If true, animation frames will be generated and cleared after each render tick, if false, they must be generated and cleared manually.
	 */
    public void render(EntityCreatureBase entity, float time, float distance, float loop, float lookY, float lookX, float scale, LayerCreatureBase layer, boolean animate) {
        float sizeScale = 1F;
		if(entity != null) {
            sizeScale *= entity.getRenderScale();
        }
    	GL11.glScalef(sizeScale, sizeScale, sizeScale);
    	GL11.glTranslatef(0, 0.5f - sizeScale / 2, 0);
    }


    // ==================================================
    //                Can Render Part
    // ==================================================
    /** Returns true if the part can be rendered, this can do various checks such as Yale wool only rendering in the YaleWoolLayer or hiding body parts in place of armor parts, etc. **/
    public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
        if(layer == null)
            return this.canBaseRenderPart(partName, entity, trophy);
        if(entity instanceof EntityCreatureBase)
            return layer.canRenderPart(partName, (EntityCreatureBase)entity, trophy);
        return false;
    }

    /** Returns true if the part can be rendered on the base layer. **/
    public boolean canBaseRenderPart(String partName, Entity entity, boolean trophy) {
        return true;
    }


    // ==================================================
    //                Get Part Color
    // ==================================================
    /** Returns the coloring to be used for this part and layer. **/
    public Vector4f getPartColor(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
        if(layer == null || !(entity instanceof EntityCreatureBase))
            return this.getBasePartColor(partName, entity, trophy, loop);
        return layer.getPartColor(partName, (EntityCreatureBase)entity, trophy);
    }

    /** Returns the coloring to be used for this part on the base layer. **/
    public Vector4f getBasePartColor(String partName, Entity entity, boolean trophy, float loop) {
        return new Vector4f(1, 1, 1, 1);
    }


	// ==================================================
	//              Get Part Texture Offset
	// ==================================================
	/** Returns the texture offset to be used for this part and layer. **/
	public Vector2f getPartTextureOffset(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
		if(layer == null || !(entity instanceof EntityCreatureBase))
			return this.getBaseTextureOffset(partName, entity, trophy, loop);
		return layer.getTextureOffset(partName, (EntityCreatureBase)entity, trophy, loop);
	}

	/** Returns the texture offset to be used for this part on the base layer (for scrolling, etc). **/
	public Vector2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
		return new Vector2f(0, 0);
	}
}
