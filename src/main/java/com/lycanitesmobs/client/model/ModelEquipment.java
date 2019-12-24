package com.lycanitesmobs.client.model;

import com.lycanitesmobs.client.ModelManager;
import com.lycanitesmobs.client.renderer.EquipmentRenderer;
import com.lycanitesmobs.client.renderer.IItemModelRenderer;
import com.lycanitesmobs.client.renderer.ObjRenderState;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ModelEquipment implements IItemModelRenderer {
	protected List<LayerItem> renderLayers = new ArrayList<>();
	protected List<ModelItemBase> renderedModels = new ArrayList<>();

	/**
	 * Constructor
	 */
	public ModelEquipment() {}


	/**
	 * Renders an Equipment Item Stack.
	 * @param itemStack The itemstack to render.
	 * @param hand The hand that is holding the item or null if in the inventory instead.
	 * @param matrixStack The matrix stack for animation.
	 * @param renderTypeBuffer  The render buffer to render with.
	 * @param renderer The renderer that is rendering this model, needed for texture binding.
	 * @param loop The animation tick for looping animations, etc.
	 * @param brightness The base brightness to render at.
	 */
	public void render(ItemStack itemStack, Hand hand, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, IItemModelRenderer renderer, float loop, int brightness) {
		if(!(itemStack.getItem() instanceof ItemEquipment)) {
			return;
		}
		ItemEquipment itemEquipment = (ItemEquipment)itemStack.getItem();
		NonNullList<ItemStack> equipmentPartStacks = itemEquipment.getEquipmentPartStacks(itemStack);

		int slotId = -1;
		ModelItemBase modelPartBase = null;
		ModelItemBase modelPartHead = null;
		for(ItemStack partStack : equipmentPartStacks) {
			slotId++;

			// Base:
			if(slotId == 0) {
				modelPartBase = this.renderPart(partStack, hand, matrixStack, renderTypeBuffer, renderer, null, loop, brightness);
			}

			// Head:
			else if(slotId == 1) {
				if(modelPartBase == null || !modelPartBase.animationParts.containsKey("head")) {
					continue;
				}
				modelPartHead = this.renderPart(partStack, hand, matrixStack, renderTypeBuffer, renderer, modelPartBase.animationParts.get("head"), loop, brightness);
			}

			// Tip A:
			else if(slotId == 2) {
				if(modelPartHead == null || !modelPartHead.animationParts.containsKey("tipa")) {
					continue;
				}
				this.renderPart(partStack, hand, matrixStack, renderTypeBuffer, renderer, modelPartHead.animationParts.get("tipa"), loop, brightness);
			}

			// Tip B:
			else if(slotId == 3) {
				if(modelPartHead == null || !modelPartHead.animationParts.containsKey("tipb")) {
					continue;
				}
				this.renderPart(partStack, hand, matrixStack, renderTypeBuffer, renderer, modelPartHead.animationParts.get("tipb"), loop, brightness);
			}

			// Tip C:
			else if(slotId == 4) {
				if(modelPartHead == null || !modelPartHead.animationParts.containsKey("tipc")) {
					continue;
				}
				this.renderPart(partStack, hand, matrixStack, renderTypeBuffer, renderer, modelPartHead.animationParts.get("tipc"), loop, brightness);
			}

			// Pommel:
			if(slotId == 5) {
				if(modelPartBase == null || !modelPartBase.animationParts.containsKey("pommel")) {
					continue;
				}
				this.renderPart(partStack, hand, matrixStack, renderTypeBuffer, renderer, modelPartBase.animationParts.get("pommel"), loop, brightness);
			}
		}

		// Clear Animation Frames:
		for(ModelItemBase modelItemBase : this.renderedModels) {
			modelItemBase.clearAnimationFrames();
		}
		this.renderedModels.clear();
	}


	/**
	 * Renders an Equipment Part.
	 * @param partStack The equipment part item stack to render.
	 * @param hand The hand that is holding the item or null if in the inventory instead.
	 * @param matrixStack The matrix stack for animation.
	 * @param renderTypeBuffer  The render buffer to render with.
	 * @param renderer The renderer that is rendering this model, needed for texture binding.
	 * @param offsetPart A ModelObjPart, if not null this model is offset by it, used by assembled equipment pieces to create a full model.
	 * @param loop The animation tick for looping animations, etc.
	 * @param brightness The base brightness to render at.
	 */
	public ModelItemBase renderPart(ItemStack partStack, Hand hand, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, IItemModelRenderer renderer, AnimationPart offsetPart, float loop, int brightness) {
		if(partStack.isEmpty() || !(partStack.getItem() instanceof ItemEquipmentPart)) {
			return null;
		}

		ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)partStack.getItem();
		ModelItemBase modelItemBase = ModelManager.getInstance().getEquipmentPartModel(itemEquipmentPart);
		if(modelItemBase == null) {
			return null;
		}

		if(modelItemBase.animationParts.containsKey("base")) {
			modelItemBase.animationParts.get("base").setOffset(offsetPart);
		}

		this.renderLayers.clear();
		modelItemBase.addCustomLayers(this);
		modelItemBase.generateAnimationFrames(partStack, null, loop, offsetPart);
		ResourceLocation texture = modelItemBase.getTexture(partStack, null);
		RenderType renderType = ObjRenderState.getObjRenderType(texture);
		modelItemBase.render(partStack, hand, matrixStack, renderTypeBuffer.getBuffer(renderType), renderer, offsetPart, null, loop, brightness);
		for(LayerItem layer : this.renderLayers) {
			texture = modelItemBase.getTexture(partStack, layer);
			renderType = ObjRenderState.getObjRenderType(texture);
			modelItemBase.render(partStack, hand, matrixStack, renderTypeBuffer.getBuffer(renderType), renderer, offsetPart, layer, loop, brightness);
		}
		this.renderedModels.add(modelItemBase);

		return modelItemBase;
	}


	@Override
	public void bindItemTexture(ResourceLocation location) {}


	@Override
	public List<LayerItem> addLayer(LayerItem renderLayer) {
		if(!this.renderLayers.contains(renderLayer)) {
			this.renderLayers.add(renderLayer);
		}
		return this.renderLayers;
	}
}
