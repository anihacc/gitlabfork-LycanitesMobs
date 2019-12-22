package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.ModelManager;
import com.lycanitesmobs.client.model.ModelEquipment;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class EquipmentRenderer extends ItemStackTileEntityRenderer implements IItemModelRenderer {

	@Override
	public void renderByItem(ItemStack itemStack) {
		if(!(itemStack.getItem() instanceof ItemEquipment)) {
			return;
		}

		Hand hand = null;

		// Position:
		RenderSystem.pushMatrix();
		RenderSystem.translatef(0.5F, 0.35F, 0.6F);
		RenderSystem.rotatef(90, 1, 0, 0);
		RenderSystem.rotatef(-100, 0, 0, 1);
		RenderSystem.translatef(0F, -1.5F, 0F);
		ModelEquipment modelEquipment = ModelManager.getInstance().getEquipmentModel();

		float loop = 0;
		if(Minecraft.getInstance().player != null) {
			loop = Minecraft.getInstance().player.ticksExisted;
		}
		modelEquipment.render(itemStack, hand, this, loop);

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
		return new ArrayList<>();
	}
}
