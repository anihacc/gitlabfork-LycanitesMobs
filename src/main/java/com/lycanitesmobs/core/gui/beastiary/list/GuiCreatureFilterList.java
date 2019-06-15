package com.lycanitesmobs.core.gui.beastiary.list;

import com.lycanitesmobs.core.gui.GuiListBase;
import com.lycanitesmobs.core.gui.beastiary.GuiBeastiary;
import com.lycanitesmobs.core.info.CreatureInfo;

import java.util.ArrayList;
import java.util.List;

public class GuiCreatureFilterList extends GuiListBase<GuiBeastiary> {
	protected List<GuiCreatureList> filteredLists = new ArrayList<>();

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiCreatureFilterList(GuiBeastiary parentGui, int width, int height, int top, int bottom, int x, int slotHeight) {
		super(parentGui, width, height, top, bottom, x, slotHeight);
	}

	@Override
	public void createEntries() {

	}


	/**
	 * Reloads all items in this list.
	 */
	public void refreshList() {}


	@Override
	protected int getItemCount() {
		return 0;
	}


	/*@Override
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
        return this.getSize() * this.slotHeight;
    }


	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {}*/


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
