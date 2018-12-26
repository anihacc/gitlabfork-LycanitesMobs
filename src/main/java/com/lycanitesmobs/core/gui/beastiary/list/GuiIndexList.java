package com.lycanitesmobs.core.gui.beastiary.list;

import com.lycanitesmobs.core.VersionChecker;
import com.lycanitesmobs.core.gui.beastiary.GuiBeastiary;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.*;

public class GuiIndexList extends GuiScrollingList {
	protected List<GuiCreatureList> filteredLists = new ArrayList<>();
	protected GuiBeastiary parentGui;
	public VersionChecker.VersionInfo versionInfo;
	public int yOffset = 0;

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiIndexList(GuiBeastiary parentGui, int width, int height, int top, int bottom, int x) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, x, 10800, width, height);
		this.parentGui = parentGui;
	}


	@Override
	protected int getSize() {
		return 1;
	}


	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.selectedIndex = index;
		for(GuiCreatureList creatureList : this.filteredLists) {
			if(creatureList != null) {
				creatureList.refreshList();
			}
		}
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
			this.parentGui.drawSplitString(this.getContent(), this.parentGui.colRightX + 8, boxTop, this.listWidth - 20, 0xFFFFFF, true);
		}
	}


	public String getContent() {
		if(this.versionInfo == null) {
			return "";
		}

		String content = "§l§n" + I18n.translateToLocal("gui.beastiary.index.changes") + "§r";
		content += "\n§l" + I18n.translateToLocal("gui.beastiary.index.changes.name") + ":§r " + this.versionInfo.name;
		if(this.versionInfo.newFeatures.length() > 0)
			content += "\n\n§l" + I18n.translateToLocal("gui.beastiary.index.changes.new") + ":§r\n" + this.versionInfo.newFeatures;
		if(this.versionInfo.configChanges.length() > 0)
			content += "\n\n§l" + I18n.translateToLocal("gui.beastiary.index.changes.config") + ":§r\n" + this.versionInfo.configChanges;
		if(this.versionInfo.majorFixes.length() > 0)
			content += "\n\n§l" + I18n.translateToLocal("gui.beastiary.index.changes.major") + ":§r\n" + this.versionInfo.majorFixes;
		if(this.versionInfo.changes.length() > 0)
			content += "\n\n§l" + I18n.translateToLocal("gui.beastiary.index.changes.gameplay") + ":§r\n" + this.versionInfo.changes;
		if(this.versionInfo.balancing.length() > 0)
			content += "\n\n§l" + I18n.translateToLocal("gui.beastiary.index.changes.balancing") + ":§r\n" + this.versionInfo.balancing;
		if(this.versionInfo.minorFixes.length() > 0)
			content += "\n\n§l" + I18n.translateToLocal("gui.beastiary.index.changes.minor") + ":§r\n" + this.versionInfo.minorFixes;

		return content;
	}


	/**
	 * Adds a Creature List as a list that should be filtered by this filter list.
	 * @param creatureList The Creature List to add and refresh as this filter list changes.
	 */
	public void addFilteredList(GuiCreatureList creatureList) {
		if(!this.filteredLists.contains(creatureList)) {
			this.filteredLists.add(creatureList);
		}
	}


	/**
	 * Returns if this filter list allows the provided Creature Info to be added to the display list.
	 * @param creatureInfo The Creature info to display.
	 * @param listType The type of Creature List.
	 * @return True if the Creature Info should be included.
	 */
	public boolean canListCreature(CreatureInfo creatureInfo, GuiCreatureList.Type listType) {
		return true;
	}
}
