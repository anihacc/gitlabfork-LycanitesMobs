package com.lycanitesmobs.client.model;

import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureSaddle;
import com.lycanitesmobs.client.renderer.layer.LayerEquipment;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public abstract class ModelCreatureBase extends EntityModel<BaseCreatureEntity> {
    
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
    public void addCustomLayers(CreatureRenderer renderer) {
        renderer.addLayer(new LayerEquipment(renderer, "chest"));
        renderer.addLayer(new LayerCreatureSaddle(renderer));
    }
    
    
    // ==================================================
   	//                  Render Model
   	// ==================================================
    @Override
    public void func_225597_a_(BaseCreatureEntity entity, float time, float distance, float loop, float lookY, float lookX) {

    }

	@Override
	public void func_225598_a_(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int someIntA, int someIntB, float someFloatA, float someFloatB, float someFloatC, float someFloatD) {

	}

	/**
	 * Renders this model. Can be rendered as a trophy (just head, mouth, etc) too, use scale for this.
	 */
	public abstract void render(BaseCreatureEntity entity, MatrixStack matrixStack, IVertexBuilder vertexBuilder, LayerCreatureBase layer, float time, float distance, float loop, float lookY, float lookX, float scale, int brightness, boolean animate);


    // ==================================================
    //                Can Render Part
    // ==================================================
    /** Returns true if the part can be rendered, this can do various checks such as Yale wool only rendering in the YaleWoolLayer or hiding body parts in place of armor parts, etc. **/
    public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
        if(layer == null)
            return this.canBaseRenderPart(partName, entity, trophy);
        if(entity instanceof BaseCreatureEntity)
            return layer.canRenderPart(partName, (BaseCreatureEntity)entity, trophy);
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
        if(layer == null || !(entity instanceof BaseCreatureEntity))
            return this.getBasePartColor(partName, entity, trophy, loop);
        return layer.getPartColor(partName, (BaseCreatureEntity)entity, trophy);
    }

    /** Returns the coloring to be used for this part on the base layer. **/
    public Vector4f getBasePartColor(String partName, Entity entity, boolean trophy, float loop) {
        return new Vector4f(1, 1, 1, 1);
    }


	// ==================================================
	//              Get Part Texture Offset
	// ==================================================
	/** Returns the texture offset to be used for this part and layer. **/
	public Vec2f getPartTextureOffset(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
		if(layer == null || !(entity instanceof BaseCreatureEntity))
			return this.getBaseTextureOffset(partName, entity, trophy, loop);
		return layer.getTextureOffset(partName, (BaseCreatureEntity)entity, trophy, loop);
	}

	/** Returns the texture offset to be used for this part on the base layer (for scrolling, etc). **/
	public Vec2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
		return new Vec2f(0, 0);
	}
}
