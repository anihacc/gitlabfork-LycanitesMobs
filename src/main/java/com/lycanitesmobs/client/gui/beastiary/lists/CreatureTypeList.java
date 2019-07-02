package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.client.gui.widgets.BaseListEntry;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.CreatureType;

import javax.annotation.Nullable;
import java.util.*;

public class CreatureTypeList extends CreatureFilterList {

	/**
	 * Constructor
	 * @param width The width of the list.
	 * @param height The height of the list.
	 * @param top The y position that the list starts at.
	 * @param bottom The y position that the list stops at.
	 * @param x The x position of the list.
	 */
	public CreatureTypeList(BeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
		super(parentGui, width, height, top, bottom, x, 16);
		this.refreshList();
	}

	@Override
	public void refreshList() {
		this.replaceEntries(new ArrayList<>());

		int typeIndex = 0;
		List<CreatureType> creatureTypes = new ArrayList<>();
		creatureTypes.addAll(CreatureManager.getInstance().creatureTypes.values());
		creatureTypes.sort(Comparator.comparing(CreatureType::getName));
		for(CreatureType creatureType : creatureTypes) {
			if(this.screen.playerExt.beastiary.getCreaturesDescovered(creatureType) > 0) {
				this.addEntry(new Entry(this, typeIndex++, creatureType));
			}
		}
	}

	@Override
	public void setSelected(@Nullable BaseListEntry entry) {
		if(entry instanceof Entry)
			this.screen.playerExt.selectedCreatureType = ((Entry)entry).creatureType;
		super.setSelected(entry);
	}

	@Override
	protected boolean isSelectedItem(int index) {
		if(!(this.getEntry(index) instanceof Entry))
			return false;
		return this.screen.playerExt.selectedCreatureType != null && this.screen.playerExt.selectedCreatureType.equals(((Entry)this.getEntry(index)).creatureType);
	}

	@Override
	public boolean canListCreature(CreatureInfo creatureInfo, CreatureList.Type listType) {
		if(this.screen.playerExt.selectedCreatureType == null || creatureInfo == null) {
			return false;
		}
		return creatureInfo.creatureType == this.screen.playerExt.selectedCreatureType;
	}

	/**
	 * List Entry
	 */
	public static class Entry extends CreatureFilterList.Entry {
		public CreatureType creatureType;

		public Entry(CreatureFilterList parentList, int index, CreatureType creatureType) {
			super(parentList, index);
			this.creatureType = creatureType;
		}

		@Override
		public void render(int index, int top, int left, int bottom, int right, int mouseX, int mouseY, boolean focus, float partialTicks) {
			this.parentList.screen.getFontRenderer().drawString(this.creatureType.getTitle().getFormattedText(), left + 4 , top + 2, 0xFFFFFF);
		}
	}
}
