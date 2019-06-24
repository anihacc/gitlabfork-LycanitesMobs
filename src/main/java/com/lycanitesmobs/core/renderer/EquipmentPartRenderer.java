package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.model.ModelEquipmentPart;
import com.lycanitesmobs.core.model.ModelItemBase;
import com.lycanitesmobs.core.renderer.layer.LayerItem;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class EquipmentPartRenderer extends ItemStackTileEntityRenderer implements IItemModelRenderer {
	protected List<LayerItem> renderLayers = new ArrayList<>();

	@Override
	public void renderByItem(ItemStack itemStack) {
		if(!(itemStack.getItem() instanceof ItemEquipmentPart)) {
			return;
		}

		Hand hand = null;

		ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();
		ModelItemBase modelItemBase = AssetManager.getItemModel(itemEquipmentPart.itemName);
		if(modelItemBase == null) {
			modelItemBase = new ModelEquipmentPart(itemEquipmentPart.itemName, itemEquipmentPart.modInfo);
			AssetManager.addItemModel(itemEquipmentPart.itemName, modelItemBase);
			return;
		}
		this.renderLayers.clear();
		modelItemBase.addCustomLayers(this);

		float loop = 0;
		if(Minecraft.getInstance().player != null) {
			loop = Minecraft.getInstance().player.ticksExisted;
		}

		GlStateManager.translatef(0.5F, 0.35F, 0.5F);

		GlStateManager.rotatef(190, 1, 0, 0);
		GlStateManager.rotatef(-45, 0, 1, 0);
		GlStateManager.rotatef(10, 0, 0, 1);

		GlStateManager.translatef(0F, -1.7F, 0F);
		if("head".equalsIgnoreCase(itemEquipmentPart.slotType)) {
			GlStateManager.translatef(0F, 0F, 0.5F);
		}
		else if("blade".equalsIgnoreCase(itemEquipmentPart.slotType) || "pike".equalsIgnoreCase(itemEquipmentPart.slotType) || "axe".equalsIgnoreCase(itemEquipmentPart.slotType)) {
			GlStateManager.translatef(0F, 0F, 1F);
		}

		GlStateManager.pushMatrix();

		modelItemBase.generateAnimationFrames(itemStack, null, loop, null);
		modelItemBase.render(itemStack, hand, this, null, null, loop, false);
		for(LayerItem renderLayer : this.renderLayers) {
			modelItemBase.render(itemStack, hand, this, null, renderLayer, loop, false);
		}
		modelItemBase.clearAnimationFrames();

		GlStateManager.popMatrix();
	}

	@Override
	public void bindItemTexture(ResourceLocation location) {
		if(location == null) {
			return;
		}
		Minecraft.getInstance().getTextureManager().bindTexture(location);
	}

	@Override
	public List<LayerItem> addLayer(LayerItem renderLayer) {
		if(!this.renderLayers.contains(renderLayer)) {
			this.renderLayers.add(renderLayer);
		}
		return this.renderLayers;
	}
}
