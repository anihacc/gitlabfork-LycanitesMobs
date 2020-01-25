package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.ModelManager;
import com.lycanitesmobs.client.model.EquipmentModel;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class EquipmentRenderer extends ItemStackTileEntityRenderer implements IItemModelRenderer {

	@Override
	public void render(ItemStack itemStack, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int brightness, int unknown) {
		if(!(itemStack.getItem() instanceof ItemEquipment)) {
			return;
		}

		Hand hand = null;

		// Position:
		matrixStack.push();
		matrixStack.translate(0.5F, 0.35F, 0.6F); // translate
		matrixStack.rotate(new Vector3f(1.0F, 0.0F, 0.0F).rotationDegrees(90)); // rotate
		matrixStack.rotate(new Vector3f(0.0F, 0.0F, 1.0F).rotationDegrees(-100)); // rotate
		matrixStack.translate(0F, -1.5F, 0F);
		EquipmentModel equipmentModel = ModelManager.getInstance().getEquipmentModel();

		float loop = 0;
		if(Minecraft.getInstance().player != null) {
			loop = Minecraft.getInstance().player.ticksExisted;
		}
		equipmentModel.render(itemStack, hand, matrixStack, renderTypeBuffer, this, loop, brightness);

		matrixStack.pop();
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
