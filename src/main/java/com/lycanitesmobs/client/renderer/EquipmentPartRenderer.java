package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.renderer.layer.LayerItemDye;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.client.model.ModelItemBase;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector4f;
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

		float loop = 0;
		if(Minecraft.getMinecraft().player != null) {
			loop = Minecraft.getMinecraft().player.ticksExisted;
		}

		GlStateManager.translate(0.5F, 0.35F, 0.5F);

		GlStateManager.rotate(190, 1, 0, 0);
		GlStateManager.rotate(-45, 0, 1, 0);
		GlStateManager.rotate(10, 0, 0, 1);

		GlStateManager.translate(0F, -1.7F, 0F);
		if("head".equalsIgnoreCase(itemEquipmentPart.slotType)) {
			GlStateManager.translate(0F, 0F, 0.5F);
		}
		else if("blade".equalsIgnoreCase(itemEquipmentPart.slotType) || "pike".equalsIgnoreCase(itemEquipmentPart.slotType) || "axe".equalsIgnoreCase(itemEquipmentPart.slotType) || "jewel".equalsIgnoreCase(itemEquipmentPart.slotType)) {
			GlStateManager.translate(0F, 0F, 1F);
		}

		GlStateManager.pushMatrix();

		modelItemBase.generateAnimationFrames(itemStack, null, loop, null);
		modelItemBase.render(itemStack, hand, this, null, null, loop, false);
		for(LayerItem renderLayer : this.renderLayers) {
			modelItemBase.render(itemStack, hand, this, null, renderLayer, loop, false);
		}
//		Vector4f partColor = itemEquipmentPart.getColor(itemStack);
//		if(partColor.x < 1 && partColor.y < 1 && partColor.z < 1) {
//			modelItemBase.render(itemStack, hand, this, null, new LayerItemDye(this, "dye"), loop, false);
//		}
		modelItemBase.clearAnimationFrames();

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
