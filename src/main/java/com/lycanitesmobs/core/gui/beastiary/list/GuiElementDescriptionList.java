package com.lycanitesmobs.core.gui.beastiary.list;

import com.lycanitesmobs.core.gui.GuiListBase;
import com.lycanitesmobs.core.gui.beastiary.GuiBeastiary;
import com.lycanitesmobs.core.info.ElementInfo;
import com.lycanitesmobs.core.localisation.LanguageManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiElementDescriptionList extends GuiListBase {
	public ElementInfo elementInfo;

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiElementDescriptionList(GuiBeastiary parentGui, int width, int height, int top, int bottom, int x) {
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
		String text = "\u00A7l" + elementInfo.getTitle() + ": " + "\u00A7r";
		text += "\n" + elementInfo.getDescription();

		// Buffs:
		text += "\n\n\u00A7l" + LanguageManager.translate("gui.beastiary.elements.buffs") + ": " + "\u00A7r";
		for(String buff : this.elementInfo.buffs) {
			ResourceLocation effectResource = new ResourceLocation(buff);
			text += "\n" + LanguageManager.translate("effect." + effectResource.getPath());
			text += ": " + LanguageManager.translate("effect." + effectResource.getPath() + ".description");
		}

		// Debuffs:
		text += "\n\n\u00A7l" + LanguageManager.translate("gui.beastiary.elements.debuffs") + ": " + "\u00A7r";
		for(String debuff : this.elementInfo.debuffs) {
			if("burning".equals(debuff)) {
				text += "\n" + LanguageManager.translate("effect.burning");
				text += ": " + LanguageManager.translate("effect.burning.description");
				continue;
			}
			ResourceLocation effectResource = new ResourceLocation(debuff);
			text += "\n" + LanguageManager.translate("effect." + effectResource.getPath());
			text += ": " + LanguageManager.translate("effect." + effectResource.getPath() + ".description");
		}

		return text;
	}

	/** Overridden to change the background gradient without copying over an entire function. **/
	protected void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2) {
		color1 = 0x33101010;
		color2 = color1;
		GuiUtils.drawGradientRect(0, left, top, right, bottom, color1, color2);
	}
}