package com.lycanitesmobs.core.gui.beastiary.list;

import com.lycanitesmobs.core.VersionChecker;
import com.lycanitesmobs.core.gui.widgets.BaseList;
import com.lycanitesmobs.core.gui.beastiary.BeastiaryScreen;

public class GuiIndexList extends BaseList {
	public VersionChecker.VersionInfo versionInfo;

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiIndexList(BeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
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
		if(index == 0 && this.versionInfo != null) {
			this.parentGui.drawSplitString(this.getContent(), this.left + 6, boxTop, this.listWidth - 20, 0xFFFFFF, true);
		}
	}


	public String getContent() {
		if(this.versionInfo == null) {
			return "";
		}

		String content = "\u00A7l\u00A7n" + LanguageManager.translate("gui.beastiary.index.changes") + "\u00A7r";
		content += "\n\u00A7l" + LanguageManager.translate("gui.beastiary.index.changes.name") + ":\u00A7r " + this.versionInfo.name;
		if(this.versionInfo.newFeatures.length() > 0)
			content += "\n\n\u00A7l" + LanguageManager.translate("gui.beastiary.index.changes.new") + ":\u00A7r\n" + this.versionInfo.newFeatures;
		if(this.versionInfo.configChanges.length() > 0)
			content += "\n\n\u00A7l" + LanguageManager.translate("gui.beastiary.index.changes.config") + ":\u00A7r\n" + this.versionInfo.configChanges;
		if(this.versionInfo.majorFixes.length() > 0)
			content += "\n\n\u00A7l" + LanguageManager.translate("gui.beastiary.index.changes.major") + ":\u00A7r\n" + this.versionInfo.majorFixes;
		if(this.versionInfo.changes.length() > 0)
			content += "\n\n\u00A7l" + LanguageManager.translate("gui.beastiary.index.changes.gameplay") + ":\u00A7r\n" + this.versionInfo.changes;
		if(this.versionInfo.balancing.length() > 0)
			content += "\n\n\u00A7l" + LanguageManager.translate("gui.beastiary.index.changes.balancing") + ":\u00A7r\n" + this.versionInfo.balancing;
		if(this.versionInfo.minorFixes.length() > 0)
			content += "\n\n\u00A7l" + LanguageManager.translate("gui.beastiary.index.changes.minor") + ":\u00A7r\n" + this.versionInfo.minorFixes;

		return content;
	}*/
}
