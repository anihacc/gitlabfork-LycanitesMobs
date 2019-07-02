package com.lycanitesmobs.client.gui.beastiary;

import com.lycanitesmobs.client.gui.beastiary.lists.ElementDescriptionList;
import com.lycanitesmobs.client.gui.beastiary.lists.ElementList;
import com.lycanitesmobs.core.info.ElementInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ElementsBeastiaryScreen extends BeastiaryScreen {
	public ElementInfo elementInfo;
	protected ElementList elementList;
	protected ElementDescriptionList descriptionList;

	public ElementsBeastiaryScreen(PlayerEntity player) {
		super(player);
	}

	@Override
	public void initWidgets() {
		super.initWidgets();

		this.elementList = new ElementList(this, this.colLeftWidth, this.colLeftHeight, this.colLeftY,this.colLeftY + this.colLeftHeight, this.colLeftX);
		this.children.add(this.elementList);

		int descriptionListY = this.colRightY;
		this.descriptionList = new ElementDescriptionList(this, this.colRightWidth, this.colRightHeight, descriptionListY, this.colRightY + this.colRightHeight, this.colRightX);
		this.children.add(this.descriptionList);
	}

	@Override
	public void renderBackground(int x, int y, float partialTicks) {
		super.renderBackground(x, y, partialTicks);
	}

	@Override
	protected void renderWidgets(int x, int y, float partialTicks) {
		this.elementList.render(x, y, partialTicks);

		if(this.elementInfo != null) {
			this.descriptionList.elementInfo = this.elementInfo;
			this.descriptionList.render(x, y, partialTicks);
		}
	}

	@Override
	public void renderForeground(int x, int y, float partialTicks) {
		super.renderForeground(x, y, partialTicks);

		if(this.elementInfo == null) {
			String info = new TranslationTextComponent("gui.beastiary.elements.about").getFormattedText();
			this.drawSplitString(info, colRightX + 1, colRightY + 12 + 1, colRightWidth, 0xFFFFFF, true);
		}
	}

	@Override
	public ITextComponent getTitle() {
		if(this.elementInfo != null) {
			return new TranslationTextComponent("");
		}
		return new TranslationTextComponent("gui.beastiary.elements");
	}
}
