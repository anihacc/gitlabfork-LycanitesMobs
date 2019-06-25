package com.lycanitesmobs.core.gui.beastiary.list;

import com.lycanitesmobs.core.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.CreatureType;

import java.util.*;

public class GuiCreatureTypeList extends GuiCreatureFilterList {
	private Map<Integer, CreatureType> creatureTypeList = new HashMap<>();

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public GuiCreatureTypeList(BeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
		super(parentGui, width, height, top, bottom, x, 16);
		this.refreshList();
	}

	@Override
	public void createEntries() {

	}

	@Override
	public void refreshList() {
		this.creatureTypeList.clear();

		int groupIndex = 0;
		List<CreatureType> creatureTypes = new ArrayList<>();
		creatureTypes.addAll(CreatureManager.getInstance().creatureTypes.values());
		creatureTypes.sort(Comparator.comparing(CreatureType::getTitle));
		for(CreatureType creatureType : creatureTypes) {
			if(this.screen.playerExt.beastiary.getCreaturesDescovered(creatureType) > 0) {
				this.creatureTypeList.put(groupIndex++, creatureType);
			}
		}
	}


	@Override
	protected int getItemCount() {
		return this.creatureTypeList.size();
	}


	/*@Override
	protected void elementClicked(int index, boolean doubleClick) {
		this.parentGui.playerExt.selectedCreatureType = this.creatureTypeList.get(index);
		super.elementClicked(index, doubleClick);
	}


	@Override
	protected boolean isSelected(int index) {
		return this.parentGui.playerExt.selectedCreatureType != null && this.parentGui.playerExt.selectedCreatureType.equals(this.creatureTypeList.get(index));
	}
	

	@Override
	protected void drawBackground() {}


	@Override
	protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
		CreatureType creatureType = this.creatureTypeList.get(index);
		if(creatureType == null) {
			return;
		}
		this.parentGui.getFontRenderer().drawString(creatureType.getTitle(), this.left + 4 , boxTop + 2, 0xFFFFFF, true);
	}*/


	@Override
	public boolean canListCreature(CreatureInfo creatureInfo, GuiCreatureList.Type listType) {
		if(this.screen.playerExt.selectedCreatureType == null || creatureInfo == null) {
			return false;
		}
		return creatureInfo.creatureType == this.screen.playerExt.selectedCreatureType;
	}
}
