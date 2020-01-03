package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.core.info.CreatureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.util.ArrayList;
import java.util.List;

public class CreatureFilterList extends GuiScrollingList {
	protected List<CreatureList> filteredLists = new ArrayList<>();
	protected BeastiaryScreen parentGui;

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public CreatureFilterList(BeastiaryScreen parentGui, int width, int height, int top, int bottom, int x, int slotHeight) {
		super(Minecraft.getMinecraft(), width, height, top, bottom, x, slotHeight, width, height);
		this.parentGui = parentGui;
	}


	/**
	 * Reloads all items in this list.
	 */
	public void refreshList() {}


	@Override
	protected int getSize() {
		return 0;
	}


	@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.selectedIndex = index;
		for(CreatureList creatureList : this.filteredLists) {
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
        return this.getSize() * this.slotHeight;
    }


	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {}


	/**
	 * Adds a Creature List as a list that should be filtered by this filter list.
	 * @param creatureList The Creature List to add and refresh as this filter list changes.
	 */
	public void addFilteredList(CreatureList creatureList) {
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
	public boolean canListCreature(CreatureInfo creatureInfo, CreatureList.Type listType) {
		return true;
	}
}
