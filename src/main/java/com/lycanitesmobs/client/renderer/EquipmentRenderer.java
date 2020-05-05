package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.ModelManager;
import com.lycanitesmobs.client.model.EquipmentModel;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

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
		matrixStack.multiply(new Vector3f(1.0F, 0.0F, 0.0F).getDegreesQuaternion(90)); // rotate
		matrixStack.multiply(new Vector3f(0.0F, 0.0F, 1.0F).getDegreesQuaternion(-100)); // rotate
		matrixStack.translate(0F, -1.5F, 0F);
		EquipmentModel equipmentModel = ModelManager.getInstance().getEquipmentModel();

		float loop = 0;
		if(MinecraftClient.getInstance().player != null) {
			loop = MinecraftClient.getInstance().player.ticksExisted;
		}
		equipmentModel.render(itemStack, hand, matrixStack, renderTypeBuffer, this, loop, brightness);

		matrixStack.pop();
	}

	@Override
	public void bindItemTexture(Identifier location) {
		if(location == null) {
			return;
		}
		MinecraftClient.getInstance().getTextureManager().bindTexture(location);
	}

	@Override
	public List<LayerItem> addLayer(LayerItem renderLayer) {
		return new ArrayList<>();
	}
}
