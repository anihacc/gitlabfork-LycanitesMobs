package com.lycanitesmobs.core.model;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.renderer.EquipmentRenderer;
import com.lycanitesmobs.core.renderer.IItemModelRenderer;
import com.lycanitesmobs.core.renderer.layer.LayerItem;
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
	public ModelEquipment() {

	}


	/**
	 * Renders an Equipment Item Stack.
	 * @param itemStack The Equipment Item Stack to render models from.
	 * @param hand The hand that the equipment is held in.
	 * @param renderer The renderer to render with.
	 */
	public void render(ItemStack itemStack, Hand hand, EquipmentRenderer renderer, float loop) {
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
				modelPartBase = this.renderPart(partStack, hand, renderer, null, loop);
			}

			// Head:
			else if(slotId == 1) {
				if(modelPartBase == null || !modelPartBase.animationParts.containsKey("head")) {
					continue;
				}
				modelPartHead = this.renderPart(partStack, hand, renderer, modelPartBase.animationParts.get("head"), loop);
			}

			// Tip A:
			else if(slotId == 2) {
				if(modelPartHead == null || !modelPartHead.animationParts.containsKey("tipa")) {
					continue;
				}
				this.renderPart(partStack, hand, renderer, modelPartHead.animationParts.get("tipa"), loop);
			}

			// Tip B:
			else if(slotId == 3) {
				if(modelPartHead == null || !modelPartHead.animationParts.containsKey("tipb")) {
					continue;
				}
				this.renderPart(partStack, hand, renderer, modelPartHead.animationParts.get("tipb"), loop);
			}

			// Tip C:
			else if(slotId == 4) {
				if(modelPartHead == null || !modelPartHead.animationParts.containsKey("tipc")) {
					continue;
				}
				this.renderPart(partStack, hand, renderer, modelPartHead.animationParts.get("tipc"), loop);
			}

			// Pommel:
			if(slotId == 5) {
				if(modelPartBase == null || !modelPartBase.animationParts.containsKey("pommel")) {
					continue;
				}
				this.renderPart(partStack, hand, renderer, modelPartBase.animationParts.get("pommel"), loop);
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
	 * @param partStack The ItemStack to render the part from.
	 * @param hand The hand that the part is held in.
	 * @param renderer The renderer to render with.
	 */
	public ModelItemBase renderPart(ItemStack partStack, Hand hand, EquipmentRenderer renderer, ModelObjPart offsetPart, float loop) {
		if(partStack.isEmpty() || !(partStack.getItem() instanceof ItemEquipmentPart)) {
			return null;
		}

		ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)partStack.getItem();
		ModelItemBase modelItemBase = AssetManager.getItemModel(itemEquipmentPart.itemName);

		if(modelItemBase.animationParts.containsKey("base")) {
			modelItemBase.animationParts.get("base").setOffset(offsetPart);
		}

		this.renderLayers.clear();
		modelItemBase.addCustomLayers(this);
		modelItemBase.generateAnimationFrames(partStack, null, loop, offsetPart);
		modelItemBase.render(partStack, hand, renderer, offsetPart, null, loop, false);
		for(LayerItem renderLayer : renderLayers) {
			modelItemBase.render(partStack, hand, renderer, offsetPart, renderLayer, loop, false);
		}
		this.renderedModels.add(modelItemBase);

		return modelItemBase;
	}


	@Override
	public void bindItemTexture(ResourceLocation location) {

	}


	@Override
	public List<LayerItem> addLayer(LayerItem renderLayer) {
		if(!this.renderLayers.contains(renderLayer)) {
			this.renderLayers.add(renderLayer);
		}
		return this.renderLayers;
	}
}
