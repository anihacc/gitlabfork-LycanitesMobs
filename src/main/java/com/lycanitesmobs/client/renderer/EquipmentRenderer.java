package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.ClientEventListener;
import com.lycanitesmobs.core.item.equipment.ItemEquipment;
import com.lycanitesmobs.client.model.ModelEquipment;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.tileentity.TileEntityEquipment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class EquipmentRenderer extends TileEntitySpecialRenderer<TileEntityEquipment> implements IItemModelRenderer {

	@Override
	public void render(TileEntityEquipment tileEntityEquipment, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		ItemStack itemStack = ItemEquipment.ITEMSTACK_TO_RENDER; // This is disgusting haxx, I am sorry, but I can't see another way. :(
		ItemEquipment.ITEMSTACK_TO_RENDER = null;

		if(!(itemStack.getItem() instanceof ItemEquipment)) {
			return;
		}

		EnumHand hand = null;

		// Position:
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 0.2F, 0.6F);
		GlStateManager.rotate(90, 1, 0, 0);
		GlStateManager.rotate(-100, 0, 0, 1);
		GlStateManager.translate(0F, -1.5F, 0F);
		ModelEquipment modelEquipment = new ModelEquipment();

		float loop = 0;
		if(Minecraft.getMinecraft().player != null) {
			loop = Minecraft.getMinecraft().player.ticksExisted;
		}
		modelEquipment.render(itemStack, hand, this, loop);

		GlStateManager.popMatrix();

		// Mana Bar:
		if (ClientEventListener.ITEM_RENDER_MODE == 1) {
			ItemEquipment itemEquipment = (ItemEquipment) itemStack.getItem();
			double manaNormal = (double) itemEquipment.getMana(itemStack) / ItemEquipment.MANA_MAX;
			if (manaNormal < 1) {
				GlStateManager.pushMatrix();
				GlStateManager.scale(1F, 1F, 1F);
				GlStateManager.translate(0.5F, 0.45F, 0F);
				GlStateManager.disableDepth();
				GlStateManager.disableTexture2D();
				GlStateManager.disableAlpha();
				GlStateManager.disableBlend();
				GlStateManager.disableLighting();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder vertexBuilder = tessellator.getBuffer();
				double barX = -0.375D;
				double barWidth = 0.825D;
				double barFill = barWidth * manaNormal;

				vertexBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
				vertexBuilder.pos(barX + barWidth, -0.225D, 1).color(0, 0, 0, 255).endVertex();
				vertexBuilder.pos(barX + barWidth, -0.075D, 1).color(0, 0, 0, 255).endVertex();
				vertexBuilder.pos(barX, -0.075D, 1).color(0, 0, 0, 255).endVertex();
				vertexBuilder.pos(barX, -0.225D, 1).color(0, 0, 0, 255).endVertex();

				vertexBuilder.pos(barX + barFill, -0.2D, 1).color(0, 128, 255, 255).endVertex();
				vertexBuilder.pos(barX + barFill, -0.15D, 1).color(0, 128, 255, 255).endVertex();
				vertexBuilder.pos(barX, -0.15D, 1).color(0, 128, 255, 255).endVertex();
				vertexBuilder.pos(barX, -0.2D, 1).color(0, 128, 255, 255).endVertex();
				Tessellator.getInstance().draw();

				GlStateManager.enableLighting();
				GlStateManager.enableBlend();
				GlStateManager.enableAlpha();
				GlStateManager.enableTexture2D();
				GlStateManager.enableBlend();
				GlStateManager.enableDepth();
				GlStateManager.popMatrix();
			}
		}

		if(tileEntityEquipment != null) {
			super.render(tileEntityEquipment, x, y, z, partialTicks, destroyStage, alpha);
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
		return new ArrayList<>();
	}
}
