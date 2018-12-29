package com.lycanitesmobs.core.renderer;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.model.ModelItemBase;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class EquipmentPartRenderer extends TileEntitySpecialRenderer<TileEntityEquipmentPart> implements IItemModelRenderer {
	protected List<LayerItem> renderLayers = new ArrayList<>();

	@Override
	public void render(TileEntityEquipmentPart tileEntityEquipmentPart, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		ItemStack itemStack = ItemEquipmentPart.ITEMSTACK_TO_RENDER; // This is disgusting haxx, I am sorry, but I can't see another way. :(
		ItemEquipmentPart.ITEMSTACK_TO_RENDER = null;

		if(!(itemStack.getItem() instanceof ItemEquipmentPart)) {
			return;
		}

		EnumHand hand = null;

		ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();
		ModelItemBase modelItemBase = AssetManager.getItemModel(itemEquipmentPart.itemName);
		this.renderLayers.clear();
		modelItemBase.addCustomLayers(this);

		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.translate(0.8F, -1.5F, -1.5F);
		GlStateManager.pushMatrix();
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.rotate(45, 0, 0, 1);

		float loop = 0;
		if(Minecraft.getMinecraft().player != null) {
			loop = Minecraft.getMinecraft().player.ticksExisted;
		}
		modelItemBase.render(itemStack, hand, this, null, null, loop);
		for(LayerItem renderLayer : this.renderLayers) {
			modelItemBase.render(itemStack, hand, this, null, renderLayer, loop);
		}

		GlStateManager.popMatrix();

		if(tileEntityEquipmentPart != null) {
			super.render(tileEntityEquipmentPart, x, y, z, partialTicks, destroyStage, alpha);
		}
	}

	@Override
	public void bindItemTexture(ResourceLocation location) {
		if(location == null) {
			return;
		}
		this.bindTexture(location);
	}

	@Override
	public List<LayerItem> addLayer(LayerItem renderLayer) {
		if(!this.renderLayers.contains(renderLayer)) {
			this.renderLayers.add(renderLayer);
		}
		return this.renderLayers;
	}
}
