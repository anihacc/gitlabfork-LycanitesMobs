package com.lycanitesmobs.core.gui.beastiary.list;

import com.lycanitesmobs.core.gui.widgets.BaseList;
import com.lycanitesmobs.core.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiElementDescriptionList extends BaseList {
	public ElementInfo elementInfo;

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiElementDescriptionList(BeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
		super(parentGui, width, height, top, bottom, x, 10800);
	}

	@Override
	public void createEntries() {

	}

	@Override
	protected int getItemCount() {
		return 1;
	}


	/*@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.selectedIndex = index;
	}


	@Override
	protected boolean isSelected(int index) {
		return false;
	}


	@Override
	protected void drawBackground() {}


	@Override
	protected int getContentHeight() {
		return this.parentGui.getFontRenderer().getWordWrappedHeight(this.getContent(), this.listWidth) + 10;
	}


	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		if(index == 0 && this.elementInfo != null) {
			this.parentGui.drawSplitString(this.getContent(), this.left + 6, boxTop, this.listWidth - 20, 0xFFFFFF, true);
		}
	}*/


	public String getContent() {
		if(this.elementInfo == null) {
			return "";
		}

		// Summary:
		ITextComponent text = new StringTextComponent("\u00A7l")
				.appendSibling(elementInfo.getTitle())
				.appendText(": " + "\u00A7r\n")
				.appendSibling(elementInfo.getDescription());

		// Buffs:
		text.appendText("\n\n\u00A7l")
			.appendSibling(new TranslationTextComponent("gui.beastiary.elements.buffs"))
			.appendText(": " + "\u00A7r");
		for(String buff : this.elementInfo.buffs) {
			ResourceLocation effectResource = new ResourceLocation(buff);
			text.appendText("\n").appendSibling(new TranslationTextComponent("effect." + effectResource.getPath()));
			text.appendText(": ").appendSibling(new TranslationTextComponent("effect." + effectResource.getPath() + ".description"));
		}

		// Debuffs:
		text.appendText("\n\n\u00A7l")
				.appendSibling(new TranslationTextComponent("gui.beastiary.elements.debuffs"))
				.appendText(": " + "\u00A7r");
		for(String debuff : this.elementInfo.debuffs) {
			if("burning".equals(debuff)) {
				text.appendText("\n")
				.appendSibling(new TranslationTextComponent("effect.burning"))
				.appendText(": ")
				.appendSibling(new TranslationTextComponent("effect.burning.description"));
				continue;
			}
			ResourceLocation effectResource = new ResourceLocation(debuff);
			text.appendText("\n")
				.appendSibling(new TranslationTextComponent("effect." + effectResource.getPath()))
				.appendText(": ")
				.appendSibling(new TranslationTextComponent("effect." + effectResource.getPath() + ".description"));
		}

		return text.getFormattedText();
	}

	/** Overridden to change the background gradient without copying over an entire function. **/
	protected void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2) {
		color1 = 0x33101010;
		color2 = color1;
		GuiUtils.drawGradientRect(0, left, top, right, bottom, color1, color2);
	}
}
