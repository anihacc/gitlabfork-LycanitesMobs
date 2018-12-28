package com.lycanitesmobs.core.model;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelObjPart {
    /** The name of this model part. **/
    public String name;
    /** The parent part of this model part, null if this part has no parent. This will do all animations that the parent does. **/
    public ModelObjPart parent;
    /** The parent name of this model part, used for initial setup and null if this part has no parent. **/
    public String parentName;
    /** The child parts connected to this part, these will all do the animations that this does. **/
    public Map<String, ModelObjPart> children = new HashMap<>();
    /** The x center location of this part for rotating around. **/
    public float centerX;
    /** The y center location of this part for rotating around. **/
    public float centerY;
    /** The z center location of this part for rotating around. **/
    public float centerZ;
	/** The x rotation of this part. **/
	public float rotationX;
	/** The y rotation of this part. **/
	public float rotationY;
	/** The z rotation of this part. **/
	public float rotationZ;

    /** A list of animation frames to apply to this part on the next render frame. **/
    public List<ModelObjAnimationFrame> animationFrames = new ArrayList<>();


	/**
	 * Reads JSON dat into this ObjPart.
	 * @param jsonObject
	 */
	public void loadFromJson(JsonObject jsonObject) {
		this.name = jsonObject.get("name").getAsString().toLowerCase();
		this.parentName = jsonObject.get("parent").getAsString().toLowerCase();
		if (this.parentName.isEmpty())
			this.parentName = null;
		this.centerX = Float.parseFloat(jsonObject.get("centerX").getAsString());
		this.centerY = Float.parseFloat(jsonObject.get("centerY").getAsString());
		this.centerZ = Float.parseFloat(jsonObject.get("centerZ").getAsString());
		if(jsonObject.has("rotationX"))
			this.rotationX = Float.parseFloat(jsonObject.get("rotationX").getAsString());
		if(jsonObject.has("rotationY"))
			this.rotationY = Float.parseFloat(jsonObject.get("rotationY").getAsString());
		if(jsonObject.has("rotationZ"))
			this.rotationZ = Float.parseFloat(jsonObject.get("rotationZ").getAsString());
	}


	/**
	 * Adds child parts to this part.
	 * @param parts An array of child parts to add.
	 */
	public void addChildren(ModelObjPart[] parts) {
        for(ModelObjPart part : parts) {
            if(part == null || part == this || part.parentName == null)
                continue;
            if(this.children.containsKey(part.parentName))
                continue;
            if(this.name.equals(part.parentName)) {
                this.children.put(part.name, part);
                part.parent = this;
            }
        }
    }


	/**
	 * Adds a new animation frame to apply during the next render frame.
	 * @param frame The animation frame to add.
	 */
	public void addAnimationFrame(ModelObjAnimationFrame frame) {
        this.animationFrames.add(frame);
    }


	/**
	 * Applies all animation frames to this part and will then go through any parents and apply theirs also.
	 * @param animator The animator instance to use.
	 */
	public void applyAnimationFrames(Animator animator) {
        // Apply Parent Frames:
        if(this.parent != null) {
            this.parent.applyAnimationFrames(animator);
        }

        // Center Part:
        animator.doTranslate(this.centerX, this.centerY, this.centerZ);

        // Apply Frames:
        for(ModelObjAnimationFrame animationFrame : this.animationFrames) {
            animationFrame.apply(animator);
        }

        // Uncenter Part:
        animator.doTranslate(-this.centerX, -this.centerY, -this.centerZ);
    }


	/**
	 * Creates a new ModelObjPart that has the combined offsets of this part and the provided part.
	 * @param combinedWithPart The part to create the combined part with.
	 * @return A new instance of a combined ModelObjPart.
	 */
	public ModelObjPart createdCombinedPart(ModelObjPart combinedWithPart) {
		ModelObjPart combinedPart = new ModelObjPart();
		combinedPart.name = this.name + "-" + combinedWithPart.name;
		combinedPart.centerX = this.centerX + combinedWithPart.centerX;
		combinedPart.centerY = this.centerY + combinedWithPart.centerY;
		combinedPart.centerZ = combinedWithPart.centerZ;
		combinedPart.rotationX = this.rotationX + combinedWithPart.rotationX;
		combinedPart.rotationY = this.rotationY + combinedWithPart.rotationY;
		combinedPart.rotationZ = this.rotationZ + combinedWithPart.rotationZ;
		return combinedPart;
	}
}
