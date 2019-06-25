package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.core.gui.beastiary.list.GuiElementDescriptionList;
import com.lycanitesmobs.core.gui.beastiary.list.GuiElementList;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class GuiBeastiaryElements extends GuiBeastiary {
	public ElementInfo elementInfo;
	protected GuiElementList elementList;
	protected GuiElementDescriptionList descriptionList;

	public GuiBeastiaryElements(PlayerEntity player) {
		super(player);
	}


	@Override
	public ITextComponent getTitle() {
		if(this.elementInfo != null) {
			return new TranslationTextComponent("");
		}
		return new TranslationTextComponent(LanguageManager.translate("gui.beastiary.elements"));
	}


	@Override
	public void initWidgets() {
		super.initWidgets();

		this.elementList = new GuiElementList(this, this.colLeftWidth, this.colLeftHeight, this.colLeftY,this.colLeftY + this.colLeftHeight, this.colLeftX);

		int descriptionListY = this.colRightY;
		this.descriptionList = new GuiElementDescriptionList(this, this.colRightWidth, this.colRightHeight, descriptionListY, this.colRightY + this.colRightHeight, this.colRightX);
	}


	@Override
	public void drawBackground(int x, int y, float partialTicks) {
		super.drawBackground(x, y, partialTicks);
	}


	@Override
	protected void updateControls(int x, int y, float partialTicks) {
		this.elementList.render(x, y, partialTicks);

		if(this.elementInfo != null) {
			this.descriptionList.elementInfo = this.elementInfo;
			this.descriptionList.render(x, y, partialTicks);
		}
	}


	@Override
	public void drawForeground(int x, int y, float partialTicks) {
		super.drawForeground(x, y, partialTicks);

		if(this.elementInfo == null) {
			String info = LanguageManager.translate("gui.beastiary.elements.about");
			this.drawSplitString(info, colRightX + 1, colRightY + 12 + 1, colRightWidth, 0xFFFFFF, true);
		}
	}
}
