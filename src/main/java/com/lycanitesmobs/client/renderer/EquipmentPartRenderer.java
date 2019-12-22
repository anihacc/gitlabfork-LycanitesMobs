package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.ModelManager;
import com.lycanitesmobs.client.model.ModelItemBase;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class EquipmentPartRenderer extends ItemStackTileEntityRenderer implements IItemModelRenderer {
	protected List<LayerItem> renderLayers = new ArrayList<>();

	@Override
	public void func_228364_a_(ItemStack itemStack, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int ticksA, int ticksB) {
		if(!(itemStack.getItem() instanceof ItemEquipmentPart)) {
			return;
		}

		Hand hand = null;

		ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();
		ModelItemBase modelItemBase = ModelManager.getInstance().getEquipmentPartModel(itemEquipmentPart);
		if(modelItemBase == null) {
			return;
		}
		this.renderLayers.clear();
		modelItemBase.addCustomLayers(this);

		float loop = 0;
		if(Minecraft.getInstance().player != null) {
			loop = Minecraft.getInstance().player.ticksExisted;
		}

		RenderSystem.translatef(0.5F, 0.35F, 0.5F);

		RenderSystem.rotatef(190, 1, 0, 0);
		RenderSystem.rotatef(-45, 0, 1, 0);
		RenderSystem.rotatef(10, 0, 0, 1);

		RenderSystem.translatef(0F, -1.7F, 0F);
		if("head".equalsIgnoreCase(itemEquipmentPart.slotType)) {
			RenderSystem.translatef(0F, 0F, 0.5F);
		}
		else if("blade".equalsIgnoreCase(itemEquipmentPart.slotType) || "pike".equalsIgnoreCase(itemEquipmentPart.slotType) || "axe".equalsIgnoreCase(itemEquipmentPart.slotType)) {
			RenderSystem.translatef(0F, 0F, 1F);
		}

		RenderSystem.pushMatrix();

		modelItemBase.generateAnimationFrames(itemStack, null, loop, null);
		modelItemBase.render(itemStack, hand, this, null, null, loop, false);
		for(LayerItem renderLayer : this.renderLayers) {
			modelItemBase.render(itemStack, hand, this, null, renderLayer, loop, false);
		}
		modelItemBase.clearAnimationFrames();

		RenderSystem.popMatrix();
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
