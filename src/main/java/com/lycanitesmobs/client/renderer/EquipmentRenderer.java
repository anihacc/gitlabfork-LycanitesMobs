package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.ModelManager;
import com.lycanitesmobs.client.model.ModelEquipment;
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
	public void func_228364_a_(ItemStack itemStack, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int brightness, int unknown) {
		if(!(itemStack.getItem() instanceof ItemEquipment)) {
			return;
		}

		Hand hand = null;

		// Position:
		matrixStack.func_227860_a_();
		matrixStack.func_227861_a_(0.5F, 0.35F, 0.6F); // translate
		matrixStack.func_227863_a_(new Vector3f(1.0F, 0.0F, 0.0F).func_229187_a_(90)); // rotate
		matrixStack.func_227863_a_(new Vector3f(0.0F, 0.0F, 1.0F).func_229187_a_(-100)); // rotate
		matrixStack.func_227861_a_(0F, -1.5F, 0F);
		ModelEquipment modelEquipment = ModelManager.getInstance().getEquipmentModel();

		float loop = 0;
		if(Minecraft.getInstance().player != null) {
			loop = Minecraft.getInstance().player.ticksExisted;
		}
		modelEquipment.render(itemStack, hand, matrixStack, renderTypeBuffer, this, loop, brightness);

		matrixStack.func_227865_b_();
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
