package com.lycanitesmobs.client.model;

import com.google.gson.*;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.client.model.animation.ModelPartAnimation;
import com.lycanitesmobs.client.obj.ObjObject;
import com.lycanitesmobs.client.obj.TessellatorModel;
import com.lycanitesmobs.client.renderer.IItemModelRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class ModelItemBase implements IAnimationModel {

	// Global:
	/** An initial x rotation applied to make Blender models match Minecraft. **/
	public static float modelXRotOffset = 180F;
	/** An initial y offset applied to make Blender models match Minecraft. **/
	public static float modelYPosOffset = -1.5F;

	// Model:
	/** An INSTANCE of the model, the model should only be set once and not during every tick or things will get very laggy! **/
	public TessellatorModel wavefrontObject;

	/** A list of all parts that belong to this model's wavefront obj. **/
	public List<ObjObject> wavefrontParts;

	/** A list of all part definitions that this model will use when animating. **/
	public Map<String, ModelObjPart> animationParts = new HashMap<>();

	// Animating:
	/** The animator INSTANCE, this is a helper class that performs actual GL11 functions, etc. **/
	protected Animator animator;
	/** The animation data for this model. **/
	protected ModelAnimation animation;
	/** The current animation part that is having an animation frame generated for. **/
	protected ModelObjPart currentAnimationPart;
	/** A list of models states that hold unique render/animation data for a specific itemstack INSTANCE. **/
	protected Map<ItemStack, ModelObjState> modelStates = new HashMap<>();
	/** The current model state for the entity that is being animated and rendered. **/
	protected ModelObjState currentModelState;


	// ==================================================
	//                    Init Model
	// ==================================================
	public ModelItemBase initModel(String name, ModInfo groupInfo, String path) {
		// Load Obj Model:
		this.wavefrontObject = new TessellatorModel(new ResourceLocation(groupInfo.modid, "models/" + path + ".obj"));
		this.wavefrontParts = this.wavefrontObject.objObjects;
		if(this.wavefrontParts.isEmpty())
			LycanitesMobs.logWarning("", "Unable to load any parts for the " + name + " model!");

		// Create Animator:
		this.animator = new Animator();

		// Load Model Parts:
		ResourceLocation animPartsLoc = new ResourceLocation(groupInfo.modid, "models/" + path + "_parts.json");
		try {
			Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
			InputStream in = Minecraft.getInstance().getResourceManager().getResource(animPartsLoc).getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			try {
				JsonArray jsonArray = JSONUtils.fromJson(gson, reader, JsonArray.class, false);
				Iterator<JsonElement> jsonIterator = jsonArray.iterator();
				while (jsonIterator.hasNext()) {
					JsonObject partJson = jsonIterator.next().getAsJsonObject();
					ModelObjPart animationPart = new ModelObjPart();
					animationPart.loadFromJson(partJson);
					this.addAnimationPart(animationPart);
				}
			}
			finally {
				IOUtils.closeQuietly(reader);
			}
		}
		catch (Exception e) {
			LycanitesMobs.logWarning("", "There was a problem loading animation parts for " + name + ":");
			e.toString();
		}

		// Assign Model Part Children:
		for(ModelObjPart part : this.animationParts.values()) {
			part.addChildren(this.animationParts.values().toArray(new ModelObjPart[this.animationParts.size()]));
		}

		// Load Animations:
		ResourceLocation animationLocation = new ResourceLocation(groupInfo.modid, "models/" + path + "_animation.json");
		try {
			Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
			InputStream in = Minecraft.getInstance().getResourceManager().getResource(animationLocation).getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			try {
				JsonObject json = JSONUtils.fromJson(gson, reader, JsonObject.class, false);
				this.animation = new ModelAnimation();
				this.animation.loadFromJson(json);
			}
			finally {
				IOUtils.closeQuietly(reader);
			}
		}
		catch (Exception e) {
			LycanitesMobs.logWarning("Model", "Unable to load animation json for " + name + ".");
		}

		return this;
	}


	// ==================================================
	//                      Parts
	// ==================================================
	// ========== Add Animation Part ==========
	public void addAnimationPart(ModelObjPart animationPart) {
		if(this.animationParts.containsKey(animationPart.name)) {
			LycanitesMobs.logWarning("", "Tried to add an animation part that already exists: " + animationPart.name + ".");
			return;
		}
		if(animationPart.parentName != null) {
			if(animationPart.parentName.equals(animationPart.name))
				animationPart.parentName = null;
		}
		this.animationParts.put(animationPart.name, animationPart);
	}


	// ==================================================
	//             Add Custom Render Layers
	// ==================================================
	/**
	 * Adds extra texture layers to the renderer.
	 * @param renderer
	 */
	public void addCustomLayers(IItemModelRenderer renderer) {
		if(this.animation != null) {
			this.animation.addItemLayers(renderer);
		}
	}


	// ==================================================
	//                     Render
	// ==================================================
	/**
	 * Renders this model based on an itemstack.
	 * @param itemStack The itemstack to render.
	 * @param hand The hand that is holding the item or null if in the inventory instead.
	 * @param renderer The renderer that is rendering this model, needed for texture binding.
	 * @param offsetObjPart A ModelObjPart, if not null this model is offset by it, used by assembled equipment pieces to create a full model.
	 * @param animate If true, animation frames will be generated and cleared after each render tick, if false, they must be generated and cleared manually, used by Equipment Pieces so that multiple parts can share their animations with each other..
	 */
	public void render(ItemStack itemStack, Hand hand, IItemModelRenderer renderer, ModelObjPart offsetObjPart, LayerItem layer, float loop, boolean animate) {
		if(itemStack == null) {
			return;
		}

		if(layer == null && this.animation != null) {
			layer = this.animation.getBaseLayer(renderer);
		}

		// Bind Texture:
		renderer.bindItemTexture(this.getTexture(itemStack, layer));

		// Generate Animation Frames:
		if(animate) {
			this.generateAnimationFrames(itemStack, layer, loop, offsetObjPart);
		}

		// Render Parts:
		for(ObjObject part : this.wavefrontParts) {
			String partName = part.getName().toLowerCase();
			if(!this.canRenderPart(partName, itemStack, layer))
				continue;
			this.currentAnimationPart = this.animationParts.get(partName);

			// Begin Rendering Part:
			RenderSystem.pushMatrix();

			// Apply Initial Offsets: (To Match Blender OBJ Export)
			this.doAngle(modelXRotOffset, 1F, 0F, 0F);
			this.doTranslate(0F, modelYPosOffset, 0F);

			/*/ Animate and Offset By Equipment Piece Slot:
			if(offsetObjPart != null) {
				offsetObjPart.applyAnimationFrames(this.animator);
				this.doTranslate(offsetObjPart.centerX, offsetObjPart.centerY, offsetObjPart.centerZ);
				this.doRotate(-offsetObjPart.rotationX, -offsetObjPart.rotationY, -offsetObjPart.rotationZ);
			}*/

			// Apply Animation Frames:
			this.currentAnimationPart.applyAnimationFrames(this.animator);

			// Render Part:
			this.onRenderStart(layer, itemStack);
			this.wavefrontObject.renderGroup(null, null, null, 240, part, this.getPartColor(partName, itemStack, layer, loop), this.getPartTextureOffset(partName, itemStack, layer, loop));
			this.onRenderFinish(layer, itemStack);
			RenderSystem.popMatrix();
		}

		// Clear Animation Frames:
		if(animate) {
			this.clearAnimationFrames();
		}
	}

	/** Called just before a layer is rendered. **/
	public void onRenderStart(LayerItem layer, ItemStack itemStack) {
		if(!CreatureManager.getInstance().config.disableModelAlpha) {
			RenderSystem.enableBlend();
		}
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		if(layer != null) {
			layer.onRenderStart(itemStack);
		}
	}

	/** Called just after a layer is rendered. **/
	public void onRenderFinish(LayerItem layer, ItemStack itemStack) {
		if(!CreatureManager.getInstance().config.disableModelAlpha) {
			RenderSystem.disableBlend();
		}
		if(layer != null) {
			layer.onRenderFinish(itemStack);
		}
	}

	/** Generates all animation frames for a render tick. **/
	public void generateAnimationFrames(ItemStack itemStack, LayerItem layer, float loop, ModelObjPart offsetObjPart) {
		for(ObjObject part : this.wavefrontParts) {
			String partName = part.getName().toLowerCase();
			if(!this.canRenderPart(partName, itemStack, layer))
				continue;
			this.currentAnimationPart = this.animationParts.get(partName);

			// Animate:
			this.animatePart(partName, itemStack, loop);
		}
	}

	/** Clears all animation frames that were generated for a render tick. **/
	public void clearAnimationFrames() {
		for(ModelObjPart animationPart : this.animationParts.values()) {
			animationPart.animationFrames.clear();
		}
	}


	// ==================================================
	//                Can Render Part
	// ==================================================
	/** Returns true if the part can be rendered for the given stack. **/
	public boolean canRenderPart(String partName, ItemStack itemStack, LayerItem layer) {
		if(partName == null)
			return false;
		partName = partName.toLowerCase();

		// Check Animation Part:
		if(!this.animationParts.containsKey(partName))
			return false;

		/*/ Check Layer:
		if(layer != null) {
			return layer.canRenderPart(partName);
		}*/

		return true;
	}


	// ==================================================
	//                   Animate Part
	// ==================================================
	/**
	 * Animates the individual part.
	 * @param partName The name of the part (should be made all lowercase).
	 * @param itemStack The itemstack to render.
	 * @param loop A continuous loop counting every tick, used for constant idle animations, etc.
	 */
	public void animatePart(String partName, ItemStack itemStack, float loop) {
		if(this.animation != null) {
			for(ModelPartAnimation partAnimation : this.animation.partAnimations) {
				partAnimation.animatePart(this, partName, loop);
			}
		}
	}


	// ==================================================
	//                   Get Texture
	// ==================================================
	/** Returns a texture ResourceLocation for the provided itemstack. **/
	public ResourceLocation getTexture(ItemStack itemStack, LayerItem layer) {
		return null;
	}


	// ==================================================
	//                Get Part Color
	// ==================================================
	/** Returns the coloring to be used for this part for the given itemstack. **/
	public Vector4f getPartColor(String partName, ItemStack itemStack, LayerItem layer, float loop) {
		if(layer != null) {
			return layer.getPartColor(partName, itemStack, loop);
		}
		return new Vector4f(1, 1, 1, 1);
	}


	// ==================================================
	//             Get Part Texture Offset
	// ==================================================
	//	/** Returns the texture offset to be used for this part and layer. **/
	public Vec2f getPartTextureOffset(String partName, ItemStack itemStack, LayerItem layer, float loop) {
		if(layer != null) {
			return layer.getTextureOffset(partName, itemStack, loop);
		}

		return new Vec2f(0, 0);
	}


	// ==================================================
	//                  GLL Actions
	// ==================================================
	public void doAngle(float rotation, float angleX, float angleY, float angleZ) {
		GL11.glRotatef(rotation, angleX, angleY, angleZ);
	}
	public void doRotate(float rotX, float rotY, float rotZ) {
		GL11.glRotatef(rotX, 1F, 0F, 0F);
		GL11.glRotatef(rotY, 0F, 1F, 0F);
		GL11.glRotatef(rotZ, 0F, 0F, 1F);
	}
	public void doTranslate(float posX, float posY, float posZ) {
		GL11.glTranslatef(posX, posY, posZ);
	}
	public void doScale(float scaleX, float scaleY, float scaleZ) {
		GL11.glScalef(scaleX, scaleY, scaleZ);
	}


	// ==================================================
	//                  Create Frames
	// ==================================================
	@Override
	public void angle(float rotation, float angleX, float angleY, float angleZ) {
		this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame("angle", rotation, angleX, angleY, angleZ));
	}

	@Override
	public void rotate(float rotX, float rotY, float rotZ) {
		this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame("rotate", 1, rotX, rotY, rotZ));
	}

	@Override
	public void translate(float posX, float posY, float posZ) {
		this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame("translate", 1, posX, posY, posZ));
	}

	@Override
	public void scale(float scaleX, float scaleY, float scaleZ) {
		this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame("scale", 1, scaleX, scaleY, scaleZ));
	}


	// ==================================================
	//                  Rotate to Point
	// ==================================================
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
