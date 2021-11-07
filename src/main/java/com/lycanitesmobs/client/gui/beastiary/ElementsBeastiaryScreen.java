package com.lycanitesmobs.client.gui.beastiary;

import com.lycanitesmobs.client.gui.beastiary.lists.ElementDescriptionList;
import com.lycanitesmobs.client.gui.beastiary.lists.ElementList;
import com.lycanitesmobs.core.info.ElementInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

public class ElementsBeastiaryScreen extends BeastiaryScreen {
	public ElementInfo elementInfo;
	protected ElementList elementList;
	protected ElementDescriptionList descriptionList;

	public ElementsBeastiaryScreen(Player player) {
		super(player);
	}

	@Override
	public void initWidgets() {
		super.initWidgets();

		this.elementList = new ElementList(this, this.colLeftWidth, this.colLeftHeight, this.colLeftY,this.colLeftY + this.colLeftHeight, this.colLeftX);
		this.addRenderableWidget(this.elementList);

		int descriptionListY = this.colRightY;
		this.descriptionList = new ElementDescriptionList(this, this.colRightWidth, this.colRightHeight, descriptionListY, this.colRightY + this.colRightHeight, this.colRightX);
		this.addRenderableWidget(this.descriptionList);
	}

	@Override
	public void renderBackground(PoseStack matrixStack, int x, int y, float partialTicks) {
		super.renderBackground(matrixStack, x, y, partialTicks);
	}

	@Override
	protected void renderWidgets(PoseStack matrixStack, int x, int y, float partialTicks) {
		this.elementList.render(matrixStack, x, y, partialTicks);

		if(this.elementInfo != null) {
			this.descriptionList.setElementInfo(this.elementInfo);
			this.descriptionList.render(matrixStack, x, y, partialTicks);
		}
	}

	@Override
	public void renderForeground(PoseStack matrixStack, int x, int y, float partialTicks) {
		super.renderForeground(matrixStack, x, y, partialTicks);

		if(this.elementInfo == null) {
			String info = new TranslatableComponent("gui.beastiary.elements.about").getString();
			this.drawHelper.drawStringWrapped(matrixStack, info, colRightX + 1, colRightY + 12 + 1, colRightWidth, 0xFFFFFF, true);
		}
	}

	@Override
	public Component getTitle() {
		if(this.elementInfo != null) {
			return new TextComponent("");
		}
		return new TranslatableComponent("gui.beastiary.elements");
	}
}
