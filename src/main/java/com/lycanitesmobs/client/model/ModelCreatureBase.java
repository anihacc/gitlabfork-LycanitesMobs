package com.lycanitesmobs.client.model;

import com.lycanitesmobs.client.renderer.CreatureRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureSaddle;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureEquipment;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ModelCreatureBase extends EntityModel<BaseCreatureEntity> implements IAnimationModel {

	// Matrix:
	public MatrixStack matrixStack;

    public ModelCreatureBase() {
        this(1.0F);
    }

	public ModelCreatureBase(float shadowSize) {
    	// Texture:
    	this.textureWidth = 128;
		this.textureHeight = 128;
    }

    @Override
    public void func_225597_a_(BaseCreatureEntity entity, float time, float distance, float loop, float lookY, float lookX) {}

	@Override
	public void func_225598_a_(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int someIntA, int someIntB, float someFloatA, float someFloatB, float someFloatC, float someFloatD) {}

	/**
	 * Generates all animation frames for a render tick.
	 * @param entity The entity to render.
	 * @param time The current movement time for walk cycles, etc.
	 * @param distance The current movement amount for walk cycles, etc.
	 * @param loop A constant tick for looping animations.
	 * @param lookY The entity's yaw looking position for head rotation, etc.
	 * @param lookX The entity's pitch looking position for head rotation, etc.
	 * @param scale The base scale to render the model at, usually just 0.0625F which scales 1m unit in Blender to a 1m block unit in Minecraft.
	 * @param brightness The brightness of the mob based on block location, etc.
	 */
	public void generateAnimationFrames(BaseCreatureEntity entity, float time, float distance, float loop, float lookY, float lookX, float scale, int brightness) {}

	/**
	 * Clears all animation frames that were generated for a render tick.
	 */
	public void clearAnimationFrames() {}

	/**
	 * Renders this model based on an entity.
	 * @param entity The entity to render.
	 * @param matrixStack The matrix stack for animation.
	 * @param vertexBuilder The vertex builder for rendering the model.
	 * @param layer The layer to render, the base layer is null.
	 * @param time The current movement time for walk cycles, etc.
	 * @param distance The current movement amount for walk cycles, etc.
	 * @param loop A constant tick for looping animations.
	 * @param lookY The entity's yaw looking position for head rotation, etc.
	 * @param lookX The entity's pitch looking position for head rotation, etc.
	 * @param scale The base scale to render the model at, usually just 0.0625F which scales 1m unit in Blender to a 1m block unit in Minecraft.
	 * @param brightness The brightness of the mob based on block location, etc.
	 */
	public abstract void render(BaseCreatureEntity entity, MatrixStack matrixStack, IVertexBuilder vertexBuilder, LayerCreatureBase layer, float time, float distance, float loop, float lookY, float lookX, float scale, int brightness);

	/**
	 * Called by the renderer to add custom layers to it.
	 * @param renderer The renderer to add layers to.
	 */
	public void addCustomLayers(CreatureRenderer renderer) {
		renderer.addLayer(new LayerCreatureEquipment(renderer, "chest"));
		renderer.addLayer(new LayerCreatureSaddle(renderer));
	}

	/**
	 * Returns true if the part can be rendered, this can do various checks such as Yale wool only rendering in the YaleWoolLayer or hiding body parts in place of armor parts, etc.
	 * @param partName The model part name to render.
	 * @param entity The entity to render.
	 * @param layer The layer to render. Null for base layer
	 * @param trophy If true, the model is being a rendered as a trophy block, etc.
	 * @return True if the part can be rendered.
	 */
    public boolean canRenderPart(String partName, Entity entity, LayerCreatureBase layer, boolean trophy) {
        if(layer == null)
            return this.canBaseRenderPart(partName, entity, trophy);
        if(entity instanceof BaseCreatureEntity)
            return layer.canRenderPart(partName, (BaseCreatureEntity)entity, trophy);
        return false;
    }

	/**
	 * Returns true if the part can be rendered on the base layer.
	 * @param partName The model part name to render.
	 * @param entity The entity to render.
	 * @param trophy If true, the model is being a rendered as a trophy block, etc.
	 * @return True if the part can be rendered.
	 */
    public boolean canBaseRenderPart(String partName, Entity entity, boolean trophy) {
        return true;
    }

	/**
	 * Returns the coloring to be used for this part and layer.
	 * @param partName The model part name to render.
	 * @param entity The entity to render.
	 * @param layer The layer to render. Null for base layer
	 * @param trophy If true, the model is being a rendered as a trophy block, etc.
	 * @param loop The animation tick for looping effects.
	 * @return The color to render the part at.
	 */
    public Vector4f getPartColor(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
        if(layer == null || !(entity instanceof BaseCreatureEntity))
            return this.getBasePartColor(partName, entity, trophy, loop);
        return layer.getPartColor(partName, (BaseCreatureEntity)entity, trophy);
    }

	/**
	 * Returns the coloring to be used for this part on the base layer.
	 * @param partName The model part name to render.
	 * @param entity The entity to render.
	 * @param trophy If true, the model is being a rendered as a trophy block, etc.
	 * @param loop The animation tick for looping effects.
	 * @return The color to render the part at.
	 */
    public Vector4f getBasePartColor(String partName, Entity entity, boolean trophy, float loop) {
        return new Vector4f(1, 1, 1, 1);
    }

	/**
	 * Returns the texture offset to be used for this part and layer.
	 * @param partName The model part name to render.
	 * @param entity The entity to render.
	 * @param layer The layer to render. Null for base layer
	 * @param trophy If true, the model is being a rendered as a trophy block, etc.
	 * @param loop The animation tick for looping effects.
	 * @return The texture offset to render the part at.
	 */
	public Vec2f getPartTextureOffset(String partName, Entity entity, LayerCreatureBase layer, boolean trophy, float loop) {
		if(layer == null || !(entity instanceof BaseCreatureEntity))
			return this.getBaseTextureOffset(partName, entity, trophy, loop);
		return layer.getTextureOffset(partName, (BaseCreatureEntity)entity, trophy, loop);
	}

	/**
	 * Returns the texture offset to be used for this part on the base layer (for scrolling, etc).
	 * @param partName The model part name to render.
	 * @param entity The entity to render.
	 * @param trophy If true, the model is being a rendered as a trophy block, etc.
	 * @param loop The animation tick for looping effects.
	 * @return The texture offset to render the part at.
	 */
	public Vec2f getBaseTextureOffset(String partName, Entity entity, boolean trophy, float loop) {
		return new Vec2f(0, 0);
	}

	/**
	 * Gets the brightness to render the given part at.
	 * @param partName The name of the part to render.
	 * @param layer The layer to render, null for base layer.
	 * @param entity The entity to render.
	 * @param brightness The base brightness of the entity based on location.
	 * @return The brightness to render at.
	 */
	public int getBrightness(String partName, LayerCreatureBase layer, BaseCreatureEntity entity, int brightness) {
		if(layer != null) {
			return layer.getBrightness(partName, entity, brightness);
		}
		return brightness;
	}

	@Override
	public void rotate(float rotX, float rotY, float rotZ) {}

	@Override
	public void angle(float rotation, float angleX, float angleY, float angleZ) {}

	@Override
	public void translate(float posX, float posY, float posZ) {}

	@Override
	public void scale(float scaleX, float scaleY, float scaleZ) {}

	@Override
	public void doRotate(float rotX, float rotY, float rotZ) {
		this.matrixStack.func_227863_a_(new Vector3f(1F, 0F, 0F).func_229187_a_(rotX));
		this.matrixStack.func_227863_a_(new Vector3f(0F, 1F, 0F).func_229187_a_(rotY));
		this.matrixStack.func_227863_a_(new Vector3f(0F, 0F, 1F).func_229187_a_(rotZ));
	}

	@Override
	public void doAngle(float rotation, float angleX, float angleY, float angleZ) {
		this.matrixStack.func_227863_a_(new Vector3f(angleX, angleY, angleZ).func_229187_a_(rotation));
	}

	@Override
	public void doTranslate(float posX, float posY, float posZ) {
		this.matrixStack.func_227861_a_(posX, posY, posZ); // TODO Translation?
	}

	@Override
	public void doScale(float scaleX, float scaleY, float scaleZ) {
		this.matrixStack.func_227862_a_(scaleX, scaleY, scaleZ); // TODO Scaling?
	}

	@Override
	public double rotateToPoint(double aTarget, double bTarget) {
		return rotateToPoint(0, 0, aTarget, bTarget);
	}

	@Override
	public double rotateToPoint(double aCenter, double bCenter, double aTarget, double bTarget) {
		if(aTarget - aCenter == 0)
			if(aTarget > aCenter) return 0;
			else if(aTarget < aCenter) return 180;
		if(bTarget - bCenter == 0)
			if(bTarget > bCenter) return 90;
			else if(bTarget < bCenter) return -90;
		if(aTarget - aCenter == 0 && bTarget - bCenter == 0)
			return 0;
		return Math.toDegrees(Math.atan2(aCenter - aTarget, bCenter - bTarget) - Math.PI / 2);
	}

	@Override
	public double[] rotateToPoint(double xCenter, double yCenter, double zCenter, double xTarget, double yTarget, double zTarget) {
		double[] rotations = new double[3];
		rotations[0] = this.rotateToPoint(yCenter, -zCenter, yTarget, -zTarget);
		rotations[1] = this.rotateToPoint(-zCenter, xCenter, -zTarget, xTarget);
		rotations[2] = this.rotateToPoint(yCenter, xCenter, yTarget, xTarget);
		return rotations;
	}
}
