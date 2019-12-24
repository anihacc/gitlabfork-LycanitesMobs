package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.ModelManager;
import com.lycanitesmobs.client.model.AnimationPart;
import com.lycanitesmobs.client.model.ModelItemBase;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class EquipmentPartRenderer extends ItemStackTileEntityRenderer implements IItemModelRenderer {
	protected List<LayerItem> renderLayers = new ArrayList<>();

	@Override
	public void func_228364_a_(ItemStack itemStack, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int brightness, int unknown) {
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

		matrixStack.func_227861_a_(0.6F, 0.35F, 0.5F); // translate
		matrixStack.func_227863_a_(new Vector3f(1.0F, 0.0F, 0.0F).func_229187_a_(190)); // rotate
		matrixStack.func_227863_a_(new Vector3f(0.0F, 1.0F, 0.0F).func_229187_a_(-45));
		matrixStack.func_227863_a_(new Vector3f(0.0F, 0.0F, 1.0F).func_229187_a_(10));
		matrixStack.func_227861_a_(0F, -1.7F, 0F);

		matrixStack.func_227860_a_();
		if("head".equalsIgnoreCase(itemEquipmentPart.slotType)) {
			matrixStack.func_227861_a_(0F, 0F, 0.5F);
		}
		else if("blade".equalsIgnoreCase(itemEquipmentPart.slotType) || "pike".equalsIgnoreCase(itemEquipmentPart.slotType) || "axe".equalsIgnoreCase(itemEquipmentPart.slotType)) {
			matrixStack.func_227861_a_(0F, 0F, 1F);
		}

		modelItemBase.generateAnimationFrames(itemStack, null, loop, null);
		this.renderModel(modelItemBase, itemStack, hand, matrixStack, renderTypeBuffer, null,null, loop, brightness);
		for(LayerItem renderLayer : this.renderLayers) {
			this.renderModel(modelItemBase, itemStack, hand, matrixStack, renderTypeBuffer, renderLayer, null, loop, brightness);
		}
		modelItemBase.clearAnimationFrames();
		matrixStack.func_227865_b_();
	}

	/**
	 * Renders the main model.
	 * @param itemStack The item stack to render.
	 * @param hand The hand that is holding the item or null if in the inventory instead.
	 * @param matrixStack The matrix stack for animation.
	 * @param renderTypeBuffer  The render type buffer for rendering with.
	 * @param layer The layer to render, the base layer is null.
	 * @param offsetObjPart A ModelObjPart, if not null this model is offset by it, used by assembled equipment pieces to create a full model.
	 * @param loop A constant tick for looping animations.
	 * @param brightness The brightness of the mob based on block location, etc.
	 */
	protected void renderModel(ModelItemBase model, ItemStack itemStack, Hand hand, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, LayerItem layer, AnimationPart offsetObjPart, float loop, int brightness) {
		ResourceLocation texture = model.getTexture(itemStack, layer);
		RenderType renderType = ObjRenderState.getObjRenderType(texture);
		model.render(itemStack, hand, matrixStack, renderTypeBuffer.getBuffer(renderType), this, offsetObjPart, layer, loop, brightness);
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
